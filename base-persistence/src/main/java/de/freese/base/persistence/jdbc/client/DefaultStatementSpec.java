// Created: 23 Juli 2024
package de.freese.base.persistence.jdbc.client;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Objects;
import java.util.function.LongConsumer;

import de.freese.base.persistence.jdbc.function.CallableStatementMapper;
import de.freese.base.persistence.jdbc.function.ConnectionCallback;
import de.freese.base.persistence.jdbc.function.StatementConfigurer;
import de.freese.base.persistence.jdbc.function.StatementSetter;

/**
 * @author Thomas Freese
 */
class DefaultStatementSpec implements AbstractJdbcClient.StatementSpec {
    private final AbstractJdbcClient jdbcClient;
    private final CharSequence sql;

    private StatementConfigurer statementConfigurer;

    DefaultStatementSpec(final CharSequence sql, final AbstractJdbcClient jdbcClient) {
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

                return statement.execute(sql.toString());
            }
        };

        return jdbcClient.execute(connectionCallback, true);
    }

    @Override
    public int executeUpdate() {
        jdbcClient.logSql(sql);

        final ConnectionCallback<Integer> connectionCallback = connection -> {
            try (Statement statement = connection.createStatement()) {
                if (statementConfigurer != null) {
                    statementConfigurer.configure(statement);
                }

                return statement.executeUpdate(sql.toString());
            }
        };

        return jdbcClient.execute(connectionCallback, true);
    }

    @Override
    public int executeUpdate(final StatementSetter<PreparedStatement> statementSetter) {
        jdbcClient.logSql(sql);

        final ConnectionCallback<Integer> connectionCallback = connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql.toString())) {
                if (statementConfigurer != null) {
                    statementConfigurer.configure(preparedStatement);
                }

                statementSetter.setParameter(preparedStatement);

                return preparedStatement.executeUpdate();
            }
        };

        return jdbcClient.execute(connectionCallback, true);
    }

    @Override
    public int executeUpdate(final StatementSetter<PreparedStatement> statementSetter, final LongConsumer generatedKeysConsumer) {
        jdbcClient.logSql(sql);

        final ConnectionCallback<Integer> connectionCallback = connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS)) {
                if (statementConfigurer != null) {
                    statementConfigurer.configure(preparedStatement);
                }

                statementSetter.setParameter(preparedStatement);

                final int affectedRows = preparedStatement.executeUpdate();

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
    public AbstractJdbcClient.QuerySpec query() {
        return new DefaultQuerySpec(sql, jdbcClient, statementConfigurer);
    }

    @Override
    public AbstractJdbcClient.StatementSpec statementConfigurer(final StatementConfigurer statementConfigurer) {
        this.statementConfigurer = statementConfigurer;

        return this;
    }
}
