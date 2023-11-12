// Created: 10.11.23
package de.freese.base.persistence.jdbc.client;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Flow;
import java.util.function.LongConsumer;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import de.freese.base.persistence.jdbc.UncheckedSqlException;
import de.freese.base.persistence.jdbc.function.ConnectionCallback;
import de.freese.base.persistence.jdbc.function.ParameterizedPreparedStatementSetter;
import de.freese.base.persistence.jdbc.function.PreparedStatementSetter;
import de.freese.base.persistence.jdbc.function.ResultSetCallback;
import de.freese.base.persistence.jdbc.function.RowMapper;
import de.freese.base.persistence.jdbc.function.StatementCallback;
import de.freese.base.persistence.jdbc.function.StatementConfigurer;
import de.freese.base.persistence.jdbc.function.StatementCreator;
import de.freese.base.persistence.jdbc.reactive.flow.ResultSetSubscription;

/**
 * <a href="https://github.com/spring-projects/spring-framework/blob/main/spring-jdbc/src/main/java/org/springframework/jdbc/core/simple/JdbcClient.java">Spring's JdbcClient</a>
 *
 * @author Thomas Freese
 */
public class JdbcClient {
    interface DeleteSpec {
        int execute();

        DeleteSpec statementConfigurer(StatementConfigurer statementConfigurer);

        DeleteSpec statementSetter(PreparedStatementSetter preparedStatementSetter);
    }

    interface InsertSpec {
        int execute();

        int execute(LongConsumer generatedKeysConsumer);

        <T> int[] executeBatch(Collection<T> batchArgs, ParameterizedPreparedStatementSetter<T> ppss, int batchSize);

        InsertSpec statementConfigurer(StatementConfigurer statementConfigurer);

        InsertSpec statementSetter(PreparedStatementSetter preparedStatementSetter);
    }

    interface SelectSpec {
        <T> T execute(ResultSetCallback<T> resultSetCallback);

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
        <T> Flux<T> executeAsFlux(RowMapper<T> rowMapper);

        <T> List<T> executeAsList(RowMapper<T> rowMapper);

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
        <T> Flow.Publisher<T> executeAsPublisher(RowMapper<T> rowMapper);

        <T> Set<T> executeAsSet(RowMapper<T> rowMapper);

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
        <T> Stream<T> executeAsStream(RowMapper<T> rowMapper);

        SelectSpec statementConfigurer(StatementConfigurer statementConfigurer);

        SelectSpec statementSetter(PreparedStatementSetter preparedStatementSetter);
    }

    interface UpdateSpec {
        int execute();

        <T> int executeBatch(Collection<T> batchArgs, ParameterizedPreparedStatementSetter<T> ppss, int batchSize);

        UpdateSpec statementConfigurer(StatementConfigurer statementConfigurer);

        UpdateSpec statementSetter(PreparedStatementSetter preparedStatementSetter);
    }

    private final DataSource dataSource;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public JdbcClient(final DataSource dataSource) {
        super();

        this.dataSource = Objects.requireNonNull(dataSource, "dataSource required");
    }

    public DeleteSpec delete(final CharSequence sql) {
        return new DefaultDeleteSpec(sql, this);
    }

    public boolean execute(final CharSequence sql) {
        StatementCreator<Statement> sc = con -> createStatement(con, null);
        StatementCallback<Statement, Boolean> action = stmt -> stmt.execute(sql.toString());

        return execute(sc, action, true);
    }

    public InsertSpec insert(final CharSequence sql) {
        return new DefaultInsertSpec(sql, this);
    }

    public boolean isBatchSupported() {
        ConnectionCallback<Boolean> action = this::isBatchSupported;

        return execute(action, true);
    }

    public SelectSpec select(final CharSequence sql) {
        return new DefaultSelectSpec(sql, this);
    }

    public UpdateSpec update(final CharSequence sql) {
        return new DefaultUpdateSpec(sql, this);
    }

    void close(final Connection connection) {
        //        Transaction transaction = TRANSACTION.orElse(null);
        //
        //        if (transaction != null) {
        //            // Closed by Transaction#close.
        //            return;
        //        }

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

    void close(final ResultSet resultSet) {
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

    void close(final Statement statement) {
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

    CallableStatement createCallableStatement(final Connection connection, final CharSequence sql, final StatementConfigurer configurer) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall(sql.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

        if (configurer != null) {
            configurer.configure(callableStatement);
        }

        return callableStatement;
    }

    PreparedStatement createPreparedStatement(final Connection connection, final CharSequence sql, final StatementConfigurer configurer) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sql.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

        if (configurer != null) {
            configurer.configure(preparedStatement);
        }

        return preparedStatement;
    }

    Statement createStatement(final Connection connection, final StatementConfigurer configurer) throws SQLException {
        Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

        if (configurer != null) {
            configurer.configure(statement);
        }

        return statement;
    }

    <T> T execute(final ConnectionCallback<T> connectionCallback, final boolean closeResources) {
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

    <S extends Statement, T> T execute(final StatementCreator<S> statementCreator, final StatementCallback<S, T> statementCallback, final boolean closeResources) {
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
    <T> T execute(final CharSequence sql, final StatementConfigurer statementConfigurer, final PreparedStatementSetter pss, final ResultSetCallback<T> resultSetCallback, final boolean closeResources) {
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

    boolean isBatchSupported(final Connection connection) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();

        return metaData.supportsBatchUpdates();
    }

    private RuntimeException convertException(final Exception ex) {
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

    private Connection getConnection() {
        try {
            return getDataSource().getConnection();
        }
        catch (SQLException ex) {
            throw new UncheckedSqlException(ex);
        }
    }

    private DataSource getDataSource() {
        return dataSource;
    }

    private Logger getLogger() {
        return logger;
    }

    private void handleWarnings(final Statement stmt) throws SQLException {
        if (getLogger().isDebugEnabled()) {
            SQLWarning warning = stmt.getWarnings();

            while (warning != null) {
                getLogger().debug("SQLWarning ignored: SQL state '{}', error code '{}', message [{}]", warning.getSQLState(), warning.getErrorCode(), warning.getMessage());

                warning = warning.getNextWarning();
            }
        }
    }
}
