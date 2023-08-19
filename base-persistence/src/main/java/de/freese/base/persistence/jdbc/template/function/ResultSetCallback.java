// Created: 19.08.23
package de.freese.base.persistence.jdbc.template.function;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Inspired by org.springframework.jdbc.core<br>
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface ResultSetCallback<T> {
    T doInResultSet(ResultSet resultSet) throws SQLException;
}
