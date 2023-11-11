// Created: 04.02.2017
package de.freese.base.persistence.jdbc.function;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * Inspired by org.springframework.jdbc.core<br>
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface StatementCallback<S extends Statement, T> {
    T doInStatement(S statement) throws SQLException;
}
