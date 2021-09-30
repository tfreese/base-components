// Created: 04.02.2017
package de.freese.base.persistence.jdbc.template.function;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Inspired by org.springframework.jdbc.core<br>
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface PreparedStatementCreator
{
    /**
     * @param connection {@link Connection}
     *
     * @return {@link PreparedStatement}
     *
     * @throws SQLException Falls was schief geht.
     */
    PreparedStatement createPreparedStatement(Connection connection) throws SQLException;
}
