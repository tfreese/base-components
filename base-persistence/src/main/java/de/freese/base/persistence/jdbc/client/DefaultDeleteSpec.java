// Created: 11.11.23
package de.freese.base.persistence.jdbc.client;

import java.sql.PreparedStatement;
import java.util.Objects;

import de.freese.base.persistence.jdbc.function.StatementCallback;
import de.freese.base.persistence.jdbc.function.StatementCreator;

/**
 * @author Thomas Freese
 */
class DefaultDeleteSpec extends AbstractStatementSpec<JdbcClient.DeleteSpec> implements JdbcClient.DeleteSpec {
    private final JdbcClient jdbcClient;
    private final CharSequence sql;

    DefaultDeleteSpec(final CharSequence sql, final JdbcClient jdbcClient) {
        super();

        this.sql = Objects.requireNonNull(sql, "sql required");
        this.jdbcClient = Objects.requireNonNull(jdbcClient, "jdbcClient required");
    }

    @Override
    public int execute() {
        final StatementCreator<PreparedStatement> statementCreator = con -> jdbcClient.createPreparedStatement(con, sql, getStatementConfigurer());
        final StatementCallback<PreparedStatement, Integer> statementCallback = stmt -> {
            if (getPreparedStatementSetter() != null) {
                getPreparedStatementSetter().setValues(stmt);
            }

            return stmt.executeUpdate();
        };

        return jdbcClient.execute(statementCreator, statementCallback, true);
    }

    @Override
    protected DefaultDeleteSpec self() {
        return this;
    }
}
