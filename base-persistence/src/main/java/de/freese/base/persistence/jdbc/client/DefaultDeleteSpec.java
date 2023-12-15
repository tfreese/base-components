// Created: 11.11.23
package de.freese.base.persistence.jdbc.client;

import java.sql.PreparedStatement;
import java.util.Objects;

import de.freese.base.persistence.jdbc.function.PreparedStatementSetter;
import de.freese.base.persistence.jdbc.function.StatementCallback;
import de.freese.base.persistence.jdbc.function.StatementConfigurer;
import de.freese.base.persistence.jdbc.function.StatementCreator;

/**
 * @author Thomas Freese
 */
class DefaultDeleteSpec implements JdbcClient.DeleteSpec {
    private final JdbcClient jdbcClient;
    private final CharSequence sql;

    private PreparedStatementSetter preparedStatementSetter;
    private StatementConfigurer statementConfigurer;

    DefaultDeleteSpec(final CharSequence sql, final JdbcClient jdbcClient) {
        super();

        this.sql = Objects.requireNonNull(sql, "sql required");
        this.jdbcClient = Objects.requireNonNull(jdbcClient, "jdbcClient required");
    }

    @Override
    public int execute() {
        final StatementCreator<PreparedStatement> statementCreator = con -> jdbcClient.createPreparedStatement(con, sql, statementConfigurer);
        final StatementCallback<PreparedStatement, Integer> statementCallback = stmt -> {
            if (preparedStatementSetter != null) {
                preparedStatementSetter.setValues(stmt);
            }

            return stmt.executeUpdate();
        };

        return jdbcClient.execute(statementCreator, statementCallback, true);
    }

    @Override
    public JdbcClient.DeleteSpec statementConfigurer(final StatementConfigurer statementConfigurer) {
        this.statementConfigurer = statementConfigurer;

        return this;
    }

    @Override
    public JdbcClient.DeleteSpec statementSetter(final PreparedStatementSetter preparedStatementSetter) {
        this.preparedStatementSetter = preparedStatementSetter;

        return this;
    }
}
