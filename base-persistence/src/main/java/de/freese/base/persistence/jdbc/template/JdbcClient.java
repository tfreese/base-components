// Created: 20.08.23
package de.freese.base.persistence.jdbc.template;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Spliterator;
import java.util.concurrent.Flow;
import java.util.function.Consumer;
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
import de.freese.base.persistence.jdbc.template.function.PreparedStatementSetter;
import de.freese.base.persistence.jdbc.template.function.ResultSetCallback;
import de.freese.base.persistence.jdbc.template.function.ResultSetExtractor;
import de.freese.base.persistence.jdbc.template.function.RowMapper;
import de.freese.base.persistence.jdbc.template.function.StatementCallback;
import de.freese.base.persistence.jdbc.template.function.StatementConfigurer;
import de.freese.base.persistence.jdbc.template.function.StatementCreator;
import de.freese.base.persistence.jdbc.template.transaction.SimpleTransactionHandler;
import de.freese.base.persistence.jdbc.template.transaction.TransactionHandler;
import de.freese.base.utils.JdbcUtils;

/**
 * @author Thomas Freese
 */
public class JdbcClient {

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

    public class SelectBuilder extends AbstractJdbcBuilder {

        public SelectBuilder(final CharSequence sql) {
            super(sql);
        }

        public <T> T execute(final ResultSetExtractor<T> resultSetExtractor) {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("execute: {}", getSql());
            }

            ResultSetCallback<T> resultSetCallback = resultSet -> resultSetExtractor.extractData(resultSet);

            return executePrepared(getSql(), getStatementConfigurer(), getPreparedStatementSetter(), resultSetCallback, true);
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

            return executePrepared(getSql(), getStatementConfigurer(), getPreparedStatementSetter(), resultSetCallback, false);
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

            return executePrepared(getSql(), getStatementConfigurer(), getPreparedStatementSetter(), resultSetCallback, false);
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

            return executePrepared(getSql(), getStatementConfigurer(), getPreparedStatementSetter(), resultSetCallback, false);
        }
    }

    private final DataSource dataSource;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final TransactionHandler transactionHandler;

    public JdbcClient(final DataSource dataSource) {
        this(dataSource, new SimpleTransactionHandler());
    }

    public JdbcClient(final DataSource dataSource, final TransactionHandler transactionHandler) {
        super();

        this.dataSource = Objects.requireNonNull(dataSource, "dataSource required");
        this.transactionHandler = Objects.requireNonNull(transactionHandler, "transactionHandler required");
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

    /**
     * @param pss {@link PreparedStatementSetter}; optional
     */
    public <T> T executePrepared(final CharSequence sql, final StatementConfigurer statementConfigurer, final PreparedStatementSetter pss, final ResultSetCallback<T> resultSetCallback, final boolean closeResources) {
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

    public SelectBuilder select(final CharSequence sql) {
        return new SelectBuilder(sql);
    }

    protected void close(final Connection connection) {
        getTransactionHandler().close(connection, getDataSource());
    }

    protected void close(final ResultSet resultSet) {
        getLogger().debug("close resultSet");

        try {
            JdbcUtils.close(resultSet);
        }
        catch (Exception ex) {
            getLogger().error("Could not close JDBC ResultSet", ex);
        }
    }

    protected void close(final Statement statement) {
        getLogger().debug("close statement");

        try {
            JdbcUtils.close(statement);
        }
        catch (Exception ex) {
            getLogger().error("Could not close JDBC Statement", ex);
        }
    }

    protected RuntimeException convertException(final Exception ex) {
        Throwable th = ex;

        if (th instanceof RuntimeException e) {
            throw e;
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

    protected PreparedStatement createPreparedStatement(final Connection connection, final CharSequence sql, final StatementConfigurer configurer) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sql.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

        if (configurer != null) {
            configurer.configure(preparedStatement);
        }

        return preparedStatement;
    }

    protected Connection getConnection() throws SQLException {
        return getTransactionHandler().getConnection(getDataSource());
    }

    protected DataSource getDataSource() {
        return this.dataSource;
    }

    protected Logger getLogger() {
        return logger;
    }

    protected TransactionHandler getTransactionHandler() {
        return transactionHandler;
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
}
