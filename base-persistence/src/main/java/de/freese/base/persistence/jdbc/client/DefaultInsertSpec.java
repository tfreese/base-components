// Created: 11.11.23
package de.freese.base.persistence.jdbc.client;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.Objects;
import java.util.function.LongConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.base.persistence.jdbc.function.ParameterizedPreparedStatementSetter;
import de.freese.base.persistence.jdbc.function.PreparedStatementSetter;
import de.freese.base.persistence.jdbc.function.StatementCallback;
import de.freese.base.persistence.jdbc.function.StatementConfigurer;
import de.freese.base.persistence.jdbc.function.StatementCreator;

/**
 * @author Thomas Freese
 */
class DefaultInsertSpec implements JdbcClient.InsertSpec {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultInsertSpec.class);

    private final JdbcClient jdbcClient;
    private final CharSequence sql;

    private StatementConfigurer statementConfigurer;

    DefaultInsertSpec(final CharSequence sql, final JdbcClient jdbcClient) {
        super();

        this.sql = Objects.requireNonNull(sql, "sql required");
        this.jdbcClient = Objects.requireNonNull(jdbcClient, "jdbcClient required");
    }

    @Override
    public int execute(final LongConsumer generatedKeysConsumer, final PreparedStatementSetter preparedStatementSetter) {
        StatementCreator<PreparedStatement> statementCreator = con -> this.jdbcClient.createPreparedStatementForInsert(con, sql, statementConfigurer);
        StatementCallback<PreparedStatement, Integer> statementCallback = stmt -> {
            if (preparedStatementSetter != null) {
                preparedStatementSetter.setValues(stmt);
            }

            int affectedRows = stmt.executeUpdate();

            if (generatedKeysConsumer != null) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    while (generatedKeys.next()) {
                        generatedKeysConsumer.accept(generatedKeys.getLong(1));
                    }
                }
            }

            return affectedRows;
        };

        return this.jdbcClient.execute(statementCreator, statementCallback, true);
    }

    @Override
    public int execute(final PreparedStatementSetter preparedStatementSetter) {
        return execute(null, preparedStatementSetter);
    }

    @Override
    public <T> int executeBatch(final Collection<T> batchArgs, final ParameterizedPreparedStatementSetter<T> ppss, final int batchSize) {
        StatementCreator<PreparedStatement> statementCreator = con -> this.jdbcClient.createPreparedStatementForInsert(con, sql, statementConfigurer);

        return this.jdbcClient.executeBatch(batchArgs, ppss, batchSize, statementCreator, LOGGER);
    }

    @Override
    public JdbcClient.InsertSpec statementConfigurer(final StatementConfigurer statementConfigurer) {
        this.statementConfigurer = statementConfigurer;

        return this;
    }
}
