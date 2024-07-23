// Created: 22 Juli 2024
package de.freese.base.persistence.jdbc.client;

import java.sql.PreparedStatement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.base.persistence.jdbc.function.StatementConfigurer;
import de.freese.base.persistence.jdbc.function.StatementSetter;

/**
 * @author Thomas Freese
 */
abstract class AbstractStatementSpec<S extends JdbcClient.StatementSpec<?>> implements JdbcClient.StatementSpec<S> {
    private final Logger logger = LoggerFactory.getLogger(DefaultUpdateSpec.class);
    
    private StatementConfigurer statementConfigurer;
    private StatementSetter<PreparedStatement> statementSetter;

    @Override
    public S statementConfigurer(final StatementConfigurer statementConfigurer) {
        this.statementConfigurer = statementConfigurer;

        return self();
    }

    @Override
    public S statementSetter(final StatementSetter<PreparedStatement> statementSetter) {
        this.statementSetter = statementSetter;

        return self();
    }

    protected Logger getLogger() {
        return logger;
    }

    protected StatementConfigurer getStatementConfigurer() {
        return statementConfigurer;
    }

    protected StatementSetter<PreparedStatement> getStatementSetter() {
        return statementSetter;
    }

    protected abstract S self();
}
