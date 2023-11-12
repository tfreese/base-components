// Created: 11.11.23
package de.freese.base.persistence.jdbc.client;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.base.persistence.jdbc.function.PreparedStatementSetter;
import de.freese.base.persistence.jdbc.function.StatementConfigurer;

/**
 * @author Thomas Freese
 */
class DefaultDeleteSpec implements JdbcClient.DeleteSpec {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultDeleteSpec.class);

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
        // TODO
        throw new UnsupportedOperationException();
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
