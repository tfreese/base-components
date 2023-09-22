// Created: 20.08.23
package de.freese.base.persistence.jdbc.template;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Spliterator;
import java.util.concurrent.Flow;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;

import de.freese.base.persistence.jdbc.reactive.ResultSetSpliterator;
import de.freese.base.persistence.jdbc.reactive.flow.ResultSetPublisher;
import de.freese.base.persistence.jdbc.reactive.flow.ResultSetSubscription;
import de.freese.base.persistence.jdbc.template.function.ConnectionCallback;
import de.freese.base.persistence.jdbc.template.function.ParameterizedPreparedStatementSetter;
import de.freese.base.persistence.jdbc.template.function.PreparedStatementSetter;
import de.freese.base.persistence.jdbc.template.function.ResultSetCallback;
import de.freese.base.persistence.jdbc.template.function.ResultSetExtractor;
import de.freese.base.persistence.jdbc.template.function.RowMapper;
import de.freese.base.persistence.jdbc.template.function.StatementCallback;
import de.freese.base.persistence.jdbc.template.function.StatementConfigurer;
import de.freese.base.persistence.jdbc.template.function.StatementCreator;
import de.freese.base.persistence.jdbc.template.transaction.SimpleTransaction;
import de.freese.base.persistence.jdbc.template.transaction.Transaction;

/**
 * @author Thomas Freese
 */
public class JdbcTemplate {

    public static final ScopedValue<Transaction> TRANSACTION = ScopedValue.newInstance();

    private abstract static class AbstractJdbcBuilder<B extends AbstractJdbcBuilder<?>> {

        private final List<Object> params = new ArrayList<>();

        private final CharSequence sql;

        private PreparedStatementSetter preparedStatementSetter;

        private StatementConfigurer statementConfigurer;

        protected AbstractJdbcBuilder(final CharSequence sql) {
            super();

            this.sql = Objects.requireNonNull(sql, "sql required");
        }

        @SuppressWarnings("unchecked")
        public B param(final Object param) {
            params.add(param);

            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B preparedStatementSetter(final PreparedStatementSetter preparedStatementSetter) {
            this.preparedStatementSetter = preparedStatementSetter;

            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B statementConfigurer(final StatementConfigurer statementConfigurer) {
            this.statementConfigurer = statementConfigurer;

            return (B) this;
        }

        PreparedStatementSetter getPreparedStatementSetter() {
            if (!params.isEmpty() && preparedStatementSetter != null) {
                throw new IllegalStateException("use preparedStatementSetter or param, but not booth");
            }

            PreparedStatementSetter pss = preparedStatementSetter;

            if (!params.isEmpty()) {
                pss = new ArgumentPreparedStatementSetter(params);
            }

            return pss;
        }

        CharSequence getSql() {
            return sql;
        }

        StatementConfigurer getStatementConfigurer() {
            return statementConfigurer;
        }
    }

    public class SelectBuilder extends AbstractJdbcBuilder<SelectBuilder> {

        public SelectBuilder(final CharSequence sql) {
            super(sql);
        }

        public <T> T execute(final ResultSetExtractor<T> resultSetExtractor) {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("execute: {}", getSql());
            }

            return JdbcTemplate.this.execute(getSql(), getStatementConfigurer(), getPreparedStatementSetter(), resultSetExtractor::extractData, true);
        }

        /**
         * Closing of the Resources ({@link ResultSet}, {@link Statement}, {@link Connection}) happens in {@link Flux#doFinally}-Method.<br>
         * <b>The JDBC-Treiber must support ResultSet-Streaming(setFetchSize(int)) !</b><br>
         * Reuse is not possible, because the Resources are closed after first usage.<br>
         * Example: <code>
         * <pre>
         * Flux&lt;Entity&gt; flux = jdbcTemplate.queryAsFlux(Sql, RowMapper, PreparedStatementSetter));
         * flux.subscribe(System.out::println);
         * </pre>
         * </code>
         */
        public <T> Flux<T> executeAsFlux(final RowMapper<T> rowMapper) {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("executeAsFlux: {}", getSql());
            }

            ResultSetCallback<Flux<T>> resultSetCallback = resultSet -> {
                final Statement statement = resultSet.getStatement();
                final Connection connection = statement.getConnection();

                return Flux.generate((final SynchronousSink<T> sink) -> {
                    try {
                        if (resultSet.next()) {
                            sink.next(rowMapper.mapRow(resultSet));
                        }
                        else {
                            sink.complete();
                        }
                    }
                    catch (SQLException ex) {
                        sink.error(ex);
                    }
                }).doFinally(state -> {
                    getLogger().debug("close jdbc flux");

                    close(resultSet);
                    close(statement);
                    close(connection);
                });
            };

            return JdbcTemplate.this.execute(getSql(), getStatementConfigurer(), getPreparedStatementSetter(), resultSetCallback, false);
        }

        public <T> List<T> executeAsList(final RowMapper<T> rowMapper) {
            return execute(new RowMapperResultSetExtractor<>(rowMapper));
        }

        /**
         * @return {@link List}; Map-Key = COLUMN_NAME in UpperCase
         */
        public List<Map<String, Object>> executeAsList() {
            return executeAsList(new ColumnMapRowMapper());
        }

        /**
         * Closing of the Resources ({@link ResultSet}, {@link Statement}, {@link Connection}) happens in {@link ResultSetSubscription}<br>
         * <b>The JDBC-Treiber must support ResultSet-Streaming(setFetchSize(int)) !</b><br>
         * Reuse is not possible, because the Resources are closed after first usage.<br>
         * Example: <code>
         * <pre>
         * Publisher&lt;Entity&gt; publisher = jdbcTemplate.queryAsPublisher(Sql, RowMapper, PreparedStatementSetter));
         * publisher.subscribe(new java.util.concurrent.Flow.Subscriber);
         * </pre>
         * </code>
         */
        public <T> Flow.Publisher<T> executeAsPublisher(final RowMapper<T> rowMapper) {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("executeAsPublisher: {}", getSql());
            }

            ResultSetCallback<Flow.Publisher<T>> resultSetCallback = resultSet -> {
                final Statement statement = resultSet.getStatement();
                final Connection connection = statement.getConnection();

                Consumer<ResultSet> doOnClose = rs -> {
                    getLogger().debug("close jdbc publisher");

                    close(rs);
                    close(statement);
                    close(connection);
                };

                return new ResultSetPublisher<>(resultSet, rowMapper, doOnClose);
            };

            return JdbcTemplate.this.execute(getSql(), getStatementConfigurer(), getPreparedStatementSetter(), resultSetCallback, false);
        }

        /**
         * Closing of the Resources ({@link ResultSet}, {@link Statement}, {@link Connection}) happens in  {@link Stream#onClose}-Method.<br>
         * {@link Stream#close}-Method MUST be called (try-resource).<br>
         * <b>The JDBC-Treiber must support ResultSet-Streaming(setFetchSize(int)) !</b><br>
         * Example: <code>
         * <pre>
         * try (Stream&lt;Entity&gt; stream = jdbcTemplate.queryAsStream(Sql, RowMapper, PreparedStatementSetter))
         * {
         *     stream.forEach(System.out::println);
         * }
         * </pre>
         * </code>
         */
        public <T> Stream<T> executeAsStream(final RowMapper<T> rowMapper) {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("executeAsStream: {}", getSql());
            }

            ResultSetCallback<Stream<T>> resultSetCallback = resultSet -> {
                final Statement statement = resultSet.getStatement();
                final Connection connection = statement.getConnection();

                // @formatter:off
                Spliterator<T> spliterator = new ResultSetSpliterator<>(resultSet, rowMapper);

                return StreamSupport.stream(spliterator, false)
                        .onClose(() -> {
                            getLogger().debug("close jdbc stream");

                            close(resultSet);
                            close(statement);
                            close(connection);
                        });
                // @formatter:on
            };

            return JdbcTemplate.this.execute(getSql(), getStatementConfigurer(), getPreparedStatementSetter(), resultSetCallback, false);
        }
    }

    public class CallBuilder extends AbstractJdbcBuilder<CallBuilder> {

        public CallBuilder(final CharSequence sql) {
            super(sql);
        }

        public boolean execute() {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("call: {}", getSql());
            }

            StatementCreator<CallableStatement> statementCreator = con -> createCallableStatement(con, getSql(), getStatementConfigurer());
            StatementCallback<CallableStatement, Boolean> statementCallback = stmt -> {

                try {
                    PreparedStatementSetter pss = getPreparedStatementSetter();

                    if (pss != null) {
                        pss.setValues(stmt);
                    }

                    return stmt.execute();
                }
                catch (SQLException ex) {
                    throw convertException(ex);
                }
            };

            return JdbcTemplate.this.execute(statementCreator, statementCallback, true);
        }

        public <T> T execute(final ResultSetExtractor<T> resultSetExtractor) {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("call: {}", getSql());
            }

            StatementCreator<CallableStatement> statementCreator = con -> createCallableStatement(con, getSql(), getStatementConfigurer());
            StatementCallback<CallableStatement, T> statementCallback = stmt -> {
                ResultSet resultSet = null;

                try {
                    PreparedStatementSetter pss = getPreparedStatementSetter();

                    if (pss != null) {
                        pss.setValues(stmt);
                    }

                    resultSet = stmt.executeQuery();

                    return resultSetExtractor.extractData(resultSet);
                }
                catch (SQLException ex) {
                    throw convertException(ex);
                }
                finally {
                    close(resultSet);
                }
            };

            return JdbcTemplate.this.execute(statementCreator, statementCallback, true);
        }
    }

    public class UpdateBuilder extends AbstractJdbcBuilder<UpdateBuilder> {

        public UpdateBuilder(final CharSequence sql) {
            super(sql);
        }

        public int execute() {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("update: {}", getSql());
            }

            StatementCreator<PreparedStatement> statementCreator = con -> createPreparedStatement(con, getSql(), getStatementConfigurer());
            StatementCallback<PreparedStatement, Integer> statementCallback = stmt -> {

                PreparedStatementSetter pss = getPreparedStatementSetter();

                if (pss != null) {
                    pss.setValues(stmt);
                }

                return stmt.executeUpdate();
            };

            return JdbcTemplate.this.execute(statementCreator, statementCallback, true);
        }

        public <T> int[] executeBatch(final Collection<T> batchArgs, final ParameterizedPreparedStatementSetter<T> ppss, final int batchSize) {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("updateBatch: size={}; {}", batchArgs.size(), getSql());
            }

            StatementCreator<PreparedStatement> statementCreator = con -> createPreparedStatement(con, getSql(), getStatementConfigurer());
            StatementCallback<PreparedStatement, int[]> statementCallback = stmt -> {

                boolean supportsBatch = isBatchSupported(stmt.getConnection());

                List<int[]> affectedRows = new ArrayList<>();
                int n = 0;

                for (T arg : batchArgs) {
                    stmt.clearParameters();
                    ppss.setValues(stmt, arg);
                    n++;

                    if (supportsBatch) {
                        stmt.addBatch();

                        if (((n % batchSize) == 0) || (n == batchArgs.size())) {
                            if (getLogger().isDebugEnabled()) {
                                int batchIndex = ((n % batchSize) == 0) ? (n / batchSize) : ((n / batchSize) + 1);
                                int items = n - ((((n % batchSize) == 0) ? ((n / batchSize) - 1) : (n / batchSize)) * batchSize);
                                getLogger().debug("Sending SQL batch update #{} with {} items", batchIndex, items);
                            }

                            affectedRows.add(stmt.executeBatch());
                            stmt.clearBatch();
                        }
                    }
                    else {
                        // Batch not possible -> direct execution.
                        int affectedRow = stmt.executeUpdate();

                        affectedRows.add(new int[]{affectedRow});
                    }
                }

                return affectedRows.stream().flatMapToInt(IntStream::of).toArray();
            };

            return JdbcTemplate.this.execute(statementCreator, statementCallback, true);
        }
    }

    private final DataSource dataSource;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Function<DataSource, Transaction> transactionHandler;

    public JdbcTemplate(final DataSource dataSource) {
        this(dataSource, SimpleTransaction::new);
    }

    public JdbcTemplate(final DataSource dataSource, final Function<DataSource, Transaction> transactionHandler) {
        super();

        this.dataSource = Objects.requireNonNull(dataSource, "dataSource required");
        this.transactionHandler = Objects.requireNonNull(transactionHandler, "transactionHandler required");
    }

    /**
     * {call my_procedure(?)};
     */
    public CallBuilder call(final CharSequence sql) {
        return new CallBuilder(sql);
    }

    public Transaction createTransaction() {
        return transactionHandler.apply(getDataSource());
    }

    public boolean execute(final CharSequence sql) {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("execute: {}", sql);
        }

        StatementCreator<Statement> sc = con -> createStatement(con, null);
        StatementCallback<Statement, Boolean> action = stmt -> stmt.execute(sql.toString());

        return execute(sc, action, true);
    }

    public <S extends Statement, T> T execute(final StatementCreator<S> statementCreator, final StatementCallback<S, T> statementCallback, final boolean closeResources) {
        ConnectionCallback<T> connectionCallback = con -> {
            S stmt = null;

            try {
                stmt = statementCreator.createStatement(con);

                T result = statementCallback.doInStatement(stmt);

                handleWarnings(stmt);

                return result;
            }
            catch (SQLException ex) {
                throw convertException(ex);
            }
            finally {
                if (closeResources) {
                    close(stmt);
                }
            }
        };

        return execute(connectionCallback, closeResources);
    }

    public <T> T execute(final ConnectionCallback<T> connectionCallback, final boolean closeResources) {
        Connection connection = null;

        try {
            connection = getConnection();

            return connectionCallback.doInConnection(connection);
        }
        catch (SQLException ex) {
            throw convertException(ex);
        }
        finally {
            if (closeResources) {
                close(connection);
            }
        }
    }

    /**
     * @param pss {@link PreparedStatementSetter}; optional
     */
    public <T> T execute(final CharSequence sql, final StatementConfigurer statementConfigurer, final PreparedStatementSetter pss, final ResultSetCallback<T> resultSetCallback, final boolean closeResources) {
        StatementCreator<PreparedStatement> statementCreator = con -> createPreparedStatement(con, sql, statementConfigurer);
        StatementCallback<PreparedStatement, T> statementCallback = stmt -> {
            ResultSet resultSet = null;

            try {
                if (pss != null) {
                    pss.setValues(stmt);
                }

                resultSet = stmt.executeQuery();

                return resultSetCallback.doInResultSet(resultSet);
            }
            catch (SQLException ex) {
                throw convertException(ex);
            }
            finally {
                if (closeResources) {
                    close(resultSet);
                }
            }
        };

        return execute(statementCreator, statementCallback, closeResources);
    }

    public boolean isBatchSupported() {
        ConnectionCallback<Boolean> action = this::isBatchSupported;

        return execute(action, true);
    }

    public SelectBuilder select(final CharSequence sql) {
        return new SelectBuilder(sql);
    }

    public UpdateBuilder update(final CharSequence sql) {
        return new UpdateBuilder(sql);
    }

    protected void close(final Connection connection) {
        Transaction transaction = TRANSACTION.orElse(null);

        if (transaction != null) {
            // Closed by Transaction#close.
            return;
        }

        getLogger().debug("close connection");

        try {
            if ((connection == null) || connection.isClosed()) {
                return;
            }

            connection.close();
        }
        catch (Exception ex) {
            //            throw new UncheckedSqlException(ex);
            getLogger().error("Could not close JDBC Connection", ex);
        }
    }

    protected void close(final ResultSet resultSet) {
        getLogger().debug("close resultSet");

        try {
            if ((resultSet == null) || resultSet.isClosed()) {
                return;
            }

            resultSet.close();
        }
        catch (Exception ex) {
            getLogger().error("Could not close JDBC ResultSet", ex);
        }
    }

    protected void close(final Statement statement) {
        getLogger().debug("close statement");

        try {
            if ((statement == null) || statement.isClosed()) {
                return;
            }

            statement.close();
        }
        catch (Exception ex) {
            getLogger().error("Could not close JDBC Statement", ex);
        }
    }

    protected RuntimeException convertException(final Exception ex) {
        Throwable th = ex;

        if (th instanceof RuntimeException re) {
            throw re;
        }

        if (th.getCause() instanceof SQLException) {
            th = th.getCause();
        }

        // while (!(th instanceof SQLException))
        // {
        // th = th.getCause();
        // }

        return new RuntimeException(th);
    }

    protected CallableStatement createCallableStatement(final Connection connection, final CharSequence sql, final StatementConfigurer configurer) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall(sql.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

        if (configurer != null) {
            configurer.configure(callableStatement);
        }

        return callableStatement;
    }

    protected PreparedStatement createPreparedStatement(final Connection connection, final CharSequence sql, final StatementConfigurer configurer) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sql.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

        if (configurer != null) {
            configurer.configure(preparedStatement);
        }

        return preparedStatement;
    }

    protected Statement createStatement(final Connection connection, final StatementConfigurer configurer) throws SQLException {
        Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

        if (configurer != null) {
            configurer.configure(statement);
        }

        return statement;
    }

    protected Connection getConnection() {
        Transaction transaction = TRANSACTION.orElse(null);

        if (transaction != null) {
            return transaction.getConnection();
        }

        try {
            return getDataSource().getConnection();
        }
        catch (SQLException ex) {
            throw new UncheckedSqlException(ex);
        }
    }

    protected DataSource getDataSource() {
        return this.dataSource;
    }

    protected Logger getLogger() {
        return logger;
    }

    protected void handleWarnings(final Statement stmt) throws SQLException {
        if (getLogger().isDebugEnabled()) {
            SQLWarning warning = stmt.getWarnings();

            while (warning != null) {
                getLogger().debug("SQLWarning ignored: SQL state '{}', error code '{}', message [{}]", warning.getSQLState(), warning.getErrorCode(), warning.getMessage());

                warning = warning.getNextWarning();
            }
        }
    }

    protected boolean isBatchSupported(final Connection connection) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();

        return metaData.supportsBatchUpdates();
    }
}
