// Created: 23 Juli 2024
package de.freese.base.persistence.jdbc.client;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.LongConsumer;
import java.util.stream.IntStream;

import de.freese.base.persistence.jdbc.function.CallableStatementMapper;
import de.freese.base.persistence.jdbc.function.ConnectionCallback;
import de.freese.base.persistence.jdbc.function.ParameterizedPreparedStatementSetter;
import de.freese.base.persistence.jdbc.function.StatementConfigurer;
import de.freese.base.persistence.jdbc.function.StatementSetter;

/**
 * @author Thomas Freese
 */
class DefaultStatementSpec implements StatementSpec {
    private final JdbcClient jdbcClient;
    private final CharSequence sql;

    private StatementConfigurer statementConfigurer;
    private StatementSetter<PreparedStatement> statementSetter;

    DefaultStatementSpec(final CharSequence sql, final JdbcClient jdbcClient) {
        super();

        this.sql = Objects.requireNonNull(sql, "sql required");
        this.jdbcClient = Objects.requireNonNull(jdbcClient, "jdbcClient required");
    }

    @Override
    public <R> R call(final StatementSetter<CallableStatement> statementSetter, final CallableStatementMapper<R> mapper) {
        jdbcClient.logSql(sql);

        final ConnectionCallback<R> connectionCallback = connection -> {
            try (CallableStatement callableStatement = connection.prepareCall(sql.toString())) {
                statementSetter.setParameter(callableStatement);
                callableStatement.execute();

                jdbcClient.handleWarnings(callableStatement);

                return mapper.map(callableStatement);
            }
        };

        return jdbcClient.execute(connectionCallback, true);
    }

    @Override
    public boolean execute() {
        jdbcClient.logSql(sql);

        final ConnectionCallback<Boolean> connectionCallback = connection -> {
            try (Statement statement = connection.createStatement()) {
                if (statementConfigurer != null) {
                    statementConfigurer.configure(statement);
                }

                final boolean isResultSet = statement.execute(sql.toString());

                jdbcClient.handleWarnings(statement);

                return isResultSet;
            }
        };

        return jdbcClient.execute(connectionCallback, true);
    }

    @Override
    public int executeUpdate() {
        jdbcClient.logSql(sql);

        final ConnectionCallback<Integer> connectionCallback = connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql.toString())) {
                if (statementConfigurer != null) {
                    statementConfigurer.configure(preparedStatement);
                }

                if (statementSetter != null) {
                    statementSetter.setParameter(preparedStatement);
                }

                final int affectedRows = preparedStatement.executeUpdate();

                jdbcClient.handleWarnings(preparedStatement);

                return affectedRows;
            }
        };

        return jdbcClient.execute(connectionCallback, true);
    }

    @Override
    public int executeUpdate(final LongConsumer generatedKeysConsumer) {
        jdbcClient.logSql(sql);

        // connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS): funktioniert bei HSQLDB nicht !
        // connection.prepareStatement(sql, new String[]{"ID"})
        final ConnectionCallback<Integer> connectionCallback = connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS)) {
                if (statementConfigurer != null) {
                    statementConfigurer.configure(preparedStatement);
                }

                if (statementSetter != null) {
                    statementSetter.setParameter(preparedStatement);
                }

                final int affectedRows = preparedStatement.executeUpdate();

                jdbcClient.handleWarnings(preparedStatement);

                if (generatedKeysConsumer != null) {
                    try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                        while (generatedKeys.next()) {
                            generatedKeysConsumer.accept(generatedKeys.getLong(1));
                        }
                    }
                }

                return affectedRows;
            }
        };

        return jdbcClient.execute(connectionCallback, true);
    }

    @Override
    public <T> int executeUpdateBatch(final int batchSize, final Collection<T> batchArgs, final ParameterizedPreparedStatementSetter<T> parameterizedPreparedStatementSetter) {
        jdbcClient.logSql(sql);

        final ConnectionCallback<Integer> connectionCallback = connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql.toString())) {
                final boolean supportsBatch = jdbcClient.isBatchSupported(connection);

                final List<int[]> affectedRows = new ArrayList<>();
                int n = 0;

                for (T arg : batchArgs) {
                    preparedStatement.clearParameters();
                    parameterizedPreparedStatementSetter.setParameter(preparedStatement, arg);
                    n++;

                    if (supportsBatch) {
                        preparedStatement.addBatch();

                        if ((n % batchSize) == 0 || n == batchArgs.size()) {
                            if (jdbcClient.getLogger().isDebugEnabled()) {
                                final int batchIndex = ((n % batchSize) == 0) ? (n / batchSize) : ((n / batchSize) + 1);
                                final int items = n - ((((n % batchSize) == 0) ? ((n / batchSize) - 1) : (n / batchSize)) * batchSize);
                                jdbcClient.getLogger().debug("Sending SQL batch #{} with {} items", batchIndex, items);
                            }

                            affectedRows.add(preparedStatement.executeBatch());
                            jdbcClient.handleWarnings(preparedStatement);
                            preparedStatement.clearBatch();
                        }
                    }
                    else {
                        // Batch not possible -> direct execution.
                        final int affectedRow = preparedStatement.executeUpdate();
                        jdbcClient.handleWarnings(preparedStatement);

                        affectedRows.add(new int[]{affectedRow});
                    }
                }

                return affectedRows.stream().flatMapToInt(IntStream::of).sum();
            }
        };

        return jdbcClient.execute(connectionCallback, true);
    }

    @Override
    public QuerySpec query() {
        return new DefaultQuerySpec(sql, jdbcClient, statementConfigurer, statementSetter);
    }

    @Override
    public StatementSpec statementConfigurer(final StatementConfigurer statementConfigurer) {
        this.statementConfigurer = statementConfigurer;

        return this;
    }

    @Override
    public StatementSpec statementSetter(final StatementSetter<PreparedStatement> statementSetter) {
        this.statementSetter = statementSetter;

        return this;
    }
}
