// Created: 12.01.2017
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
import java.util.concurrent.Flow.Publisher;
import java.util.function.Consumer;
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
import de.freese.base.persistence.jdbc.template.function.ResultSetExtractor;
import de.freese.base.persistence.jdbc.template.function.RowMapper;
import de.freese.base.persistence.jdbc.template.function.StatementCallback;
import de.freese.base.persistence.jdbc.template.function.StatementCreator;
import de.freese.base.utils.JdbcUtils;

/**
 * Inspired by org.springframework.jdbc.core.JdbcTemplate.<br>
 *
 * @author Thomas Freese
 */
public class SimpleJdbcTemplate {
    private final DataSource dataSource;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private int fetchSize = 1000;

    private int maxRows = -1;

    private int queryTimeout = -1;

    public SimpleJdbcTemplate(final DataSource dataSource) {
        super();

        this.dataSource = Objects.requireNonNull(dataSource, "dataSource required");
    }

    /**
     * {call my_procedure()};
     */
    public void call(final String sql) {
        call(sql, null);
    }

    /**
     * {? = call my_procedure(?)};
     */
    public boolean call(final CharSequence sql, final PreparedStatementSetter pss) {
        StatementCreator<CallableStatement> sc = con -> createCallableStatement(con, sql);
        StatementCallback<CallableStatement, Boolean> action = stmt -> {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("call: {}", sql);
            }

            stmt.clearParameters();

            if (pss != null) {
                pss.setValues(stmt);
            }

            return stmt.execute();
        };

        return execute(sc, action, true);
    }

    public boolean execute(final CharSequence sql) {
        StatementCreator<Statement> sc = this::createStatement;
        StatementCallback<Statement, Boolean> action = stmt -> {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("execute: {}", sql);
            }

            return stmt.execute(sql.toString());
        };

        return execute(sc, action, true);
    }

    public DataSource getDataSource() {
        return this.dataSource;
    }

    public int getFetchSize() {
        return this.fetchSize;
    }

    public int getMaxRows() {
        return this.maxRows;
    }

    public int getQueryTimeout() {
        return this.queryTimeout;
    }

    public boolean isBatchSupported() {
        ConnectionCallback<Boolean> action = this::isBatchSupported;

        return execute(action, true);
    }

    /**
     * @return {@link List}; Map-Key = COLUMN_NAME in UpperCase
     */
    public List<Map<String, Object>> query(final CharSequence sql) {
        return query(sql, new ColumnMapRowMapper(), (PreparedStatementSetter) null);
    }

    public <T> T query(final CharSequence sql, final ResultSetExtractor<T> rse) {
        return query(sql, rse, (PreparedStatementSetter) null);
    }

    public <T> T query(final CharSequence sql, final ResultSetExtractor<T> rse, final Object... params) {
        return query(sql, rse, new ArgumentPreparedStatementSetter(params));
    }

    public <T> T query(final CharSequence sql, final ResultSetExtractor<T> rse, final PreparedStatementSetter pss) {
        StatementCreator<PreparedStatement> sc = con -> createPreparedStatement(con, sql);
        StatementCallback<PreparedStatement, T> action = stmt -> {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("query: {}", sql);
            }

            ResultSet resultSet = null;

            try {
                stmt.clearParameters();

                if (pss != null) {
                    pss.setValues(stmt);
                }

                resultSet = stmt.executeQuery();

                return rse.extractData(resultSet);
            }
            finally {
                close(resultSet);
            }
        };

        return execute(sc, action, true);
    }

    public <T> List<T> query(final CharSequence sql, final RowMapper<T> rowMapper) {
        return query(sql, rowMapper, (PreparedStatementSetter) null);
    }

    public <T> List<T> query(final CharSequence sql, final RowMapper<T> rowMapper, final Object... params) {
        return query(sql, rowMapper, new ArgumentPreparedStatementSetter(params));
    }

    public <T> List<T> query(final CharSequence sql, final RowMapper<T> rowMapper, final PreparedStatementSetter pss) {
        return query(sql, new RowMapperResultSetExtractor<>(rowMapper), pss);
    }

    /**
     * @see #queryAsFlux(CharSequence, RowMapper, PreparedStatementSetter)
     */
    public <T> Flux<T> queryAsFlux(final CharSequence sql, final RowMapper<T> rowMapper) {
        return queryAsFlux(sql, rowMapper, (PreparedStatementSetter) null);
    }

    /**
     * @see #queryAsFlux(CharSequence, RowMapper, PreparedStatementSetter)
     */
    public <T> Flux<T> queryAsFlux(final CharSequence sql, final RowMapper<T> rowMapper, final Object... params) {
        return queryAsFlux(sql, rowMapper, new ArgumentPreparedStatementSetter(params));
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
    public <T> Flux<T> queryAsFlux(final CharSequence sql, final RowMapper<T> rowMapper, final PreparedStatementSetter pss) {
        StatementCreator<PreparedStatement> sc = con -> createPreparedStatement(con, sql);
        StatementCallback<PreparedStatement, Flux<T>> action = stmt -> {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("queryAsFlux: {}", sql);
            }

            stmt.clearParameters();

            if (pss != null) {
                pss.setValues(stmt);
            }

            final ResultSet resultSet = stmt.executeQuery();
            final Connection connection = stmt.getConnection();

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
                close(stmt);
                close(connection);
            });
        };

        return execute(sc, action, false);
    }

    /**
     * @see #queryAsPublisher(CharSequence, RowMapper, PreparedStatementSetter)
     */
    public <T> Publisher<T> queryAsPublisher(final CharSequence sql, final RowMapper<T> rowMapper) {
        return queryAsPublisher(sql, rowMapper, (PreparedStatementSetter) null);
    }

    /**
     * @see #queryAsPublisher(CharSequence, RowMapper, PreparedStatementSetter)
     */
    public <T> Publisher<T> queryAsPublisher(final CharSequence sql, final RowMapper<T> rowMapper, final Object... params) {
        return queryAsPublisher(sql, rowMapper, new ArgumentPreparedStatementSetter(params));
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
    public <T> Publisher<T> queryAsPublisher(final CharSequence sql, final RowMapper<T> rowMapper, final PreparedStatementSetter pss) {
        StatementCreator<PreparedStatement> sc = con -> createPreparedStatement(con, sql);
        StatementCallback<PreparedStatement, Publisher<T>> action = stmt -> {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("queryAsPublisher: {}", sql);
            }

            stmt.clearParameters();

            if (pss != null) {
                pss.setValues(stmt);
            }

            final ResultSet resultSet = stmt.executeQuery();
            final Connection connection = stmt.getConnection();

            Consumer<ResultSet> doOnClose = rs -> {
                close(rs);
                close(stmt);
                close(connection);
            };

            return new ResultSetPublisher<>(resultSet, rowMapper, doOnClose);
        };

        return execute(sc, action, false);
    }

    /**
     * @see #queryAsStream(CharSequence, RowMapper, PreparedStatementSetter)
     */
    public <T> Stream<T> queryAsStream(final CharSequence sql, final RowMapper<T> rowMapper) {
        return queryAsStream(sql, rowMapper, (PreparedStatementSetter) null);
    }

    /**
     * @see #queryAsStream(CharSequence, RowMapper, PreparedStatementSetter)
     */
    public <T> Stream<T> queryAsStream(final CharSequence sql, final RowMapper<T> rowMapper, final Object... params) {
        return queryAsStream(sql, rowMapper, new ArgumentPreparedStatementSetter(params));
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
    public <T> Stream<T> queryAsStream(final CharSequence sql, final RowMapper<T> rowMapper, final PreparedStatementSetter pss) {
        StatementCreator<PreparedStatement> sc = con -> createPreparedStatement(con, sql);
        StatementCallback<PreparedStatement, Stream<T>> action = stmt -> {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("queryAsStream: {}", sql);
            }

            stmt.clearParameters();

            if (pss != null) {
                pss.setValues(stmt);
            }

            final ResultSet resultSet = stmt.executeQuery();
            final Connection connection = stmt.getConnection();

            // @formatter:off
            Spliterator<T> spliterator = new ResultSetSpliterator<>(resultSet, rowMapper);

            return StreamSupport.stream(spliterator, false)
                    .onClose(() -> {
                        getLogger().debug("close jdbc stream");

                        close(resultSet);
                        close(stmt);
                        close(connection);
                    });
            // @formatter:on
        };

        return execute(sc, action, false);
    }

    public void setFetchSize(final int fetchSize) {
        this.fetchSize = fetchSize;
    }

    public void setMaxRows(final int maxRows) {
        this.maxRows = maxRows;
    }

    public void setQueryTimeout(final int queryTimeout) {
        this.queryTimeout = queryTimeout;
    }

    /**
     * INSERT, UPDATE, DELETE
     *
     * @return int; affectedRows
     */
    public int update(final CharSequence sql) {
        return update(sql, (PreparedStatementSetter) null);
    }

    /**
     * INSERT, UPDATE, DELETE
     *
     * @return int; affectedRows
     */
    public int update(final CharSequence sql, final Object... params) {
        return update(sql, new ArgumentPreparedStatementSetter(params));
    }

    /**
     * INSERT, UPDATE, DELETE
     *
     * @return int; affectedRows
     */
    public int update(final CharSequence sql, final PreparedStatementSetter pss) {
        StatementCreator<PreparedStatement> sc = con -> createPreparedStatement(con, sql);
        StatementCallback<PreparedStatement, Integer> action = stmt -> {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("update: {}", sql);
            }

            stmt.clearParameters();

            if (pss != null) {
                pss.setValues(stmt);
            }

            return stmt.executeUpdate();
        };

        return execute(sc, action, true);
    }

    /**
     * INSERT, UPDATE, DELETE
     *
     * @return int[]; affectedRows
     */
    public <T> int[] updateBatch(final CharSequence sql, final Collection<T> batchArgs, final ParameterizedPreparedStatementSetter<T> ppss, final int batchSize) {
        StatementCreator<PreparedStatement> sc = con -> createPreparedStatement(con, sql);
        StatementCallback<PreparedStatement, int[]> action = stmt -> {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("updateBatch: size={}; {}", batchArgs.size(), sql);
            }

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
                            int batchIndex = ((n % batchSize) == 0) ? n / batchSize : (n / batchSize) + 1;
                            int items = n - ((((n % batchSize) == 0) ? (n / batchSize) - 1 : (n / batchSize)) * batchSize);
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

        return execute(sc, action, true);
    }

    /**
     * @see #setFetchSize
     * @see #setMaxRows
     * @see #setQueryTimeout
     */
    protected void applyStatementSettings(final Statement statement) throws SQLException {
        int fs = getFetchSize();

        if (fs != -1) {
            statement.setFetchSize(fs);
        }

        int mr = getMaxRows();

        if (mr != -1) {
            statement.setMaxRows(mr);
        }

        int qt = getQueryTimeout();

        if (qt != -1) {
            statement.setQueryTimeout(qt);
        }
    }

    protected void close(final Connection connection) {
        getLogger().debug("close connection");

        // Spring-Variant
        // DataSourceUtils.releaseConnection(connection, getDataSource());

        try {
            JdbcUtils.close(connection);
        }
        catch (Exception ex) {
            getLogger().error("Could not close JDBC Connection", ex);
        }
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

    protected CallableStatement createCallableStatement(final Connection connection, final CharSequence sql) throws SQLException {
        return connection.prepareCall(sql.toString());
    }

    protected PreparedStatement createPreparedStatement(final Connection connection, final CharSequence sql) throws SQLException {
        return connection.prepareStatement(sql.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
    }

    protected Statement createStatement(final Connection connection) throws SQLException {
        return connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
    }

    protected <T> T execute(final ConnectionCallback<T> action, final boolean closeResources) {
        Connection connection = null;

        try {
            connection = getConnection();

            return action.doInConnection(connection);
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
     * callableStatement.registerOutParameter(2, java.sql.Types.VARCHAR);<br>
     * callableStatement.execute(); ODER executeUpdate();<br>
     * callableStatement.getString(2);
     */
    protected <S extends Statement, T> T execute(final StatementCreator<S> sc, final StatementCallback<S, T> action, final boolean closeResources) {
        ConnectionCallback<T> connectionCallback = con -> {
            S stmt = null;

            try {
                stmt = sc.createStatement(con);

                applyStatementSettings(stmt);

                T result = action.doInStatement(stmt);

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

    protected Connection getConnection() throws SQLException {
        // Spring-Variant
        // return DataSourceUtils.getConnection(getDataSource());

        return getDataSource().getConnection();
    }

    protected Logger getLogger() {
        return this.logger;
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
