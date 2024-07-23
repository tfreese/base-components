// Created: 12.01.2017
package de.freese.base.persistence.jdbc.function;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * Inspired by org.springframework.jdbc.core<br>
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface StatementSetter<S extends Statement> {
    void setParameter(S stmt) throws SQLException;
}
