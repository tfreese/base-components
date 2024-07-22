// Created: 22 Juli 2024
package de.freese.base.persistence.jdbc.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.base.persistence.jdbc.function.PreparedStatementSetter;
import de.freese.base.persistence.jdbc.function.StatementConfigurer;

/**
 * @author Thomas Freese
 */
abstract class AbstractStatementSpec<S extends JdbcClient.StatementSpec<?>> implements JdbcClient.StatementSpec<S> {
    private final Logger logger = LoggerFactory.getLogger(DefaultUpdateSpec.class);

    private PreparedStatementSetter preparedStatementSetter;
    private StatementConfigurer statementConfigurer;

    @Override
    public S statementConfigurer(final StatementConfigurer statementConfigurer) {
        this.statementConfigurer = statementConfigurer;

        return self();
    }

    @Override
    public S statementSetter(final PreparedStatementSetter preparedStatementSetter) {
        this.preparedStatementSetter = preparedStatementSetter;

        return self();
    }

    protected Logger getLogger() {
        return logger;
    }

    protected PreparedStatementSetter getPreparedStatementSetter() {
        return preparedStatementSetter;
    }

    protected StatementConfigurer getStatementConfigurer() {
        return statementConfigurer;
    }

    protected abstract S self();
}
