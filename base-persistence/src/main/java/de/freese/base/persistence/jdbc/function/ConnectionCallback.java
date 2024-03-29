// Created: 04.02.2017
package de.freese.base.persistence.jdbc.function;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Inspired by org.springframework.jdbc.core<br>
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface ConnectionCallback<T> {
    T doInConnection(Connection connection) throws SQLException;
}
