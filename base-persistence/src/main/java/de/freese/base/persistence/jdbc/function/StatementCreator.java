// Created: 04.02.2017
package de.freese.base.persistence.jdbc.function;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Inspired by org.springframework.jdbc.core<br>
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface StatementCreator<S extends Statement> {
    S createStatement(Connection connection) throws SQLException;
}
