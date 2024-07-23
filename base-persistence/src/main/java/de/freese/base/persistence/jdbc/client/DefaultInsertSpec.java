// Created: 11.11.23
package de.freese.base.persistence.jdbc.client;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.Objects;
import java.util.function.LongConsumer;

import de.freese.base.persistence.jdbc.function.ParameterizedPreparedStatementSetter;
import de.freese.base.persistence.jdbc.function.StatementCallback;
import de.freese.base.persistence.jdbc.function.StatementCreator;

/**
 * @author Thomas Freese
 */
class DefaultInsertSpec extends AbstractStatementSpec<JdbcClient.InsertSpec> implements JdbcClient.InsertSpec {
    private final JdbcClient jdbcClient;
    private final CharSequence sql;

    DefaultInsertSpec(final CharSequence sql, final JdbcClient jdbcClient) {
        super();

        this.sql = Objects.requireNonNull(sql, "sql required");
        this.jdbcClient = Objects.requireNonNull(jdbcClient, "jdbcClient required");
    }

    @Override
    public int execute(final LongConsumer generatedKeysConsumer) {
        final StatementCreator<PreparedStatement> statementCreator = con -> this.jdbcClient.createPreparedStatementForInsert(con, sql, getStatementConfigurer());
        final StatementCallback<PreparedStatement, Integer> statementCallback = stmt -> {
            if (getStatementSetter() != null) {
                getStatementSetter().setParameter(stmt);
            }

            final int affectedRows = stmt.executeUpdate();

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
    public int execute() {
        return execute(null);
    }

    @Override
    public <T> int executeBatch(final Collection<T> batchArgs, final ParameterizedPreparedStatementSetter<T> ppss, final int batchSize) {
        final StatementCreator<PreparedStatement> statementCreator = con -> this.jdbcClient.createPreparedStatementForInsert(con, sql, getStatementConfigurer());

        return this.jdbcClient.executeBatch(batchArgs, ppss, batchSize, statementCreator, getLogger());
    }

    @Override
    protected DefaultInsertSpec self() {
        return this;
    }
}
