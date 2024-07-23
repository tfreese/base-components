// Created: 10.11.23
package de.freese.base.persistence.jdbc.client;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Flow;
import java.util.function.Function;
import java.util.function.LongConsumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.slf4j.Logger;
import reactor.core.publisher.Flux;

import de.freese.base.persistence.jdbc.function.ConnectionCallback;
import de.freese.base.persistence.jdbc.function.ParameterizedPreparedStatementSetter;
import de.freese.base.persistence.jdbc.function.ResultSetCallback;
import de.freese.base.persistence.jdbc.function.RowMapper;
import de.freese.base.persistence.jdbc.function.StatementCallback;
import de.freese.base.persistence.jdbc.function.StatementConfigurer;
import de.freese.base.persistence.jdbc.function.StatementCreator;
import de.freese.base.persistence.jdbc.function.StatementSetter;
import de.freese.base.persistence.jdbc.reactive.flow.ResultSetSubscription;
import de.freese.base.persistence.jdbc.transaction.SimpleTransaction;
import de.freese.base.persistence.jdbc.transaction.Transaction;

/**
 * <a href="https://github.com/spring-projects/spring-framework/blob/main/spring-jdbc/src/main/java/org/springframework/jdbc/core/simple/JdbcClient.java">Spring's JdbcClient</a>
 *
 * @author Thomas Freese
 */
public class JdbcClient extends AbstractJdbcClient {
    public interface DeleteSpec extends StatementSpec<DeleteSpec> {
        int execute();
    }

    public interface InsertSpec extends StatementSpec<InsertSpec> {
        int execute();

        int execute(LongConsumer generatedKeysConsumer);

        <T> int executeBatch(Collection<T> batchArgs, ParameterizedPreparedStatementSetter<T> ppss, int batchSize);
    }

    public interface SelectSpec extends StatementSpec<SelectSpec> {
        /**
         * <pre>{@code
         * List<?> results = execute(ArrayList::new, rowMapper)
         * Set<?> results = execute(LinkedHashSet::new, rowMapper)
         * }</pre>
         */
        <T, C extends Collection<T>> C execute(Supplier<C> collectionFactory, RowMapper<T> rowMapper);

        <T> T execute(ResultSetCallback<T> resultSetCallback);

        /**
         * Closing of the Resources ({@link ResultSet}, {@link Statement}, {@link Connection}) happens in {@link Flux#doFinally}-Method.<br>
         * <b>The JDBC-Treiber must support ResultSet-Streaming(setFetchSize(int)) !</b><br>
         * Reuse is not possible, because the Resources are closed after first usage.<br>
         * Example:<br>
         * <pre>{@code
         * Flux<Entity> flux = jdbcTemplate.queryAsFlux(Sql, RowMapper, StatementSetter));
         * flux.subscribe(System.out::println);
         * }</pre>
         */
        <T> Flux<T> executeAsFlux(RowMapper<T> rowMapper);

        List<Map<String, Object>> executeAsMap();

        /**
         * Closing of the Resources ({@link ResultSet}, {@link Statement}, {@link Connection}) happens in {@link ResultSetSubscription}<br>
         * <b>The JDBC-Treiber must support ResultSet-Streaming(setFetchSize(int)) !</b><br>
         * Reuse is not possible, because the Resources are closed after first usage.<br>
         * Example:<br>
         * <pre>{@code
         * Publisher<Entity> publisher = jdbcTemplate.queryAsPublisher(Sql, RowMapper, StatementSetter));
         * publisher.subscribe(new java.util.concurrent.Flow.Subscriber);
         * }</pre>
         */
        <T> Flow.Publisher<T> executeAsPublisher(RowMapper<T> rowMapper);

        /**
         * Closing of the Resources ({@link ResultSet}, {@link Statement}, {@link Connection}) happens in  {@link Stream#onClose}-Method.<br>
         * {@link Stream#close}-Method MUST be called (try-resource).<br>
         * <b>The JDBC-Treiber must support ResultSet-Streaming(setFetchSize(int)) !</b><br>
         * Example:<br>
         * <pre>{@code
         * try (Stream<Entity> stream = jdbcTemplate.queryAsStream(Sql, RowMapper, StatementSetter)) {
         *     stream.forEach(System.out::println);
         * }
         * }</pre>
         */
        <T> Stream<T> executeAsStream(RowMapper<T> rowMapper);
    }

    public interface StatementSpec<S extends StatementSpec<?>> {
        S statementConfigurer(StatementConfigurer statementConfigurer);

        /**
         * Not for Batch-Execution.
         */
        S statementSetter(StatementSetter<PreparedStatement> statementSetter);
    }

    public interface UpdateSpec extends StatementSpec<UpdateSpec> {
        int execute();

        <T> int executeBatch(Collection<T> batchArgs, ParameterizedPreparedStatementSetter<T> ppss, int batchSize);
    }

    public JdbcClient(final DataSource dataSource) {
        this(dataSource, SimpleTransaction::new);
    }

    public JdbcClient(final DataSource dataSource, final Function<DataSource, Transaction> transactionHandler) {
        super(dataSource, transactionHandler);
    }

    public DeleteSpec delete(final CharSequence sql) {
        return new DefaultDeleteSpec(sql, this);
    }

    public boolean execute(final CharSequence sql) {
        logSql(sql);

        final StatementCreator<Statement> sc = con -> createStatement(con, null);
        final StatementCallback<Statement, Boolean> action = stmt -> stmt.execute(sql.toString());

        return execute(sc, action, true);
    }

    public InsertSpec insert(final CharSequence sql) {
        return new DefaultInsertSpec(sql, this);
    }

    public SelectSpec select(final CharSequence sql) {
        return new DefaultSelectSpec(sql, this);
    }

    public UpdateSpec update(final CharSequence sql) {
        return new DefaultUpdateSpec(sql, this);
    }

    void close(final ResultSet resultSet) {
        getLogger().debug("close resultSet");

        try {
            if (resultSet == null || resultSet.isClosed()) {
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
            if (statement == null || statement.isClosed()) {
                return;
            }

            statement.close();
        }
        catch (Exception ex) {
            getLogger().error("Could not close JDBC Statement", ex);
        }
    }

    CallableStatement createCallableStatement(final Connection connection, final CharSequence sql, final StatementConfigurer configurer) throws SQLException {
        final CallableStatement callableStatement = connection.prepareCall(sql.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

        if (configurer != null) {
            configurer.configure(callableStatement);
        }

        return callableStatement;
    }

    PreparedStatement createPreparedStatement(final Connection connection, final CharSequence sql, final StatementConfigurer configurer) throws SQLException {
        final PreparedStatement preparedStatement = connection.prepareStatement(sql.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

        if (configurer != null) {
            configurer.configure(preparedStatement);
        }

        return preparedStatement;
    }

    PreparedStatement createPreparedStatementForInsert(final Connection connection, final CharSequence sql, final StatementConfigurer configurer) throws SQLException {
        final PreparedStatement preparedStatement = connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);

        if (configurer != null) {
            configurer.configure(preparedStatement);
        }

        return preparedStatement;
    }

    Statement createStatement(final Connection connection, final StatementConfigurer configurer) throws SQLException {
        final Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

        if (configurer != null) {
            configurer.configure(statement);
        }

        return statement;
    }

    <S extends Statement, T> T execute(final StatementCreator<S> statementCreator, final StatementCallback<S, T> statementCallback, final boolean closeResources) {
        final ConnectionCallback<T> connectionCallback = con -> {
            S stmt = null;

            try {
                stmt = statementCreator.createStatement(con);

                final T result = statementCallback.doInStatement(stmt);

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
     * @param pss {@link StatementSetter}; optional
     */
    <T> T execute(final CharSequence sql, final StatementConfigurer statementConfigurer, final StatementSetter<PreparedStatement> pss, final ResultSetCallback<T> resultSetCallback,
                  final boolean closeResources) {
        logSql(sql);

        final StatementCreator<PreparedStatement> statementCreator = con -> createPreparedStatement(con, sql, statementConfigurer);
        final StatementCallback<PreparedStatement, T> statementCallback = stmt -> {
            ResultSet resultSet = null;

            try {
                if (pss != null) {
                    pss.setParameter(stmt);
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

    <T> int executeBatch(final Collection<T> batchArgs, final ParameterizedPreparedStatementSetter<T> ppss, final int batchSize,
                         final StatementCreator<PreparedStatement> statementCreator, final Logger logger) {
        final StatementCallback<PreparedStatement, Integer> statementCallback = stmt -> {
            final boolean supportsBatch = isBatchSupported(stmt.getConnection());

            final List<int[]> affectedRows = new ArrayList<>();
            int n = 0;

            for (T arg : batchArgs) {
                stmt.clearParameters();
                ppss.setParameter(stmt, arg);
                n++;

                if (supportsBatch) {
                    stmt.addBatch();

                    if ((n % batchSize) == 0 || n == batchArgs.size()) {
                        if (logger.isDebugEnabled()) {
                            final int batchIndex = ((n % batchSize) == 0) ? (n / batchSize) : ((n / batchSize) + 1);
                            final int items = n - ((((n % batchSize) == 0) ? ((n / batchSize) - 1) : (n / batchSize)) * batchSize);
                            logger.debug("Sending SQL batch update #{} with {} items", batchIndex, items);
                        }

                        affectedRows.add(stmt.executeBatch());
                        handleWarnings(stmt);
                        stmt.clearBatch();
                    }
                }
                else {
                    // Batch not possible -> direct execution.
                    final int affectedRow = stmt.executeUpdate();
                    handleWarnings(stmt);

                    affectedRows.add(new int[]{affectedRow});
                }
            }

            return affectedRows.stream().flatMapToInt(IntStream::of).sum();
        };

        return execute(statementCreator, statementCallback, true);
    }
}
