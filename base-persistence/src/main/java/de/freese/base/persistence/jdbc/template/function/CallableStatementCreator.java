// Created: 04.02.2017
package de.freese.base.persistence.jdbc.template.function;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Inspired by org.springframework.jdbc.core<br>
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface CallableStatementCreator
{
    /**
     * @param connection {@link Connection}
     *
     * @return {@link CallableStatement}
     *
     * @throws SQLException Falls was schief geht.
     */
    CallableStatement createCallableStatement(Connection connection) throws SQLException;
}