// Created: 04.02.2017
package de.freese.base.persistence.jdbc.template.function;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Inspired by org.springframework.jdbc.core<br>
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface StatementCreator
{
    /**
     * @param connection {@link Connection}
     *
     * @return {@link Statement}
     *
     * @throws SQLException Falls was schief geht.
     */
    Statement createStatement(Connection connection) throws SQLException;
}
