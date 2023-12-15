// Created: 11.11.23
package de.freese.base.persistence.jdbc.client;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Objects;

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
class DefaultUpdateSpec implements JdbcClient.UpdateSpec {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultUpdateSpec.class);

    private final JdbcClient jdbcClient;
    private final CharSequence sql;

    private StatementConfigurer statementConfigurer;

    DefaultUpdateSpec(final CharSequence sql, final JdbcClient jdbcClient) {
        super();

        this.sql = Objects.requireNonNull(sql, "sql required");
        this.jdbcClient = Objects.requireNonNull(jdbcClient, "jdbcClient required");
    }

    @Override
    public int execute(final PreparedStatementSetter preparedStatementSetter) {
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
    public <T> int executeBatch(final Collection<T> batchArgs, final ParameterizedPreparedStatementSetter<T> ppss, final int batchSize) {
        final StatementCreator<PreparedStatement> statementCreator = con -> this.jdbcClient.createPreparedStatement(con, sql, statementConfigurer);

        return this.jdbcClient.executeBatch(batchArgs, ppss, batchSize, statementCreator, LOGGER);
    }

    @Override
    public JdbcClient.UpdateSpec statementConfigurer(final StatementConfigurer statementConfigurer) {
        this.statementConfigurer = statementConfigurer;

        return this;
    }
}
