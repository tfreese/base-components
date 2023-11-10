// Created: 12.01.2017
package de.freese.base.persistence.jdbc.client;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Inspired by org.springframework.jdbc.core<br>
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface StatementSetter<T> {
    void setValues(PreparedStatement preparedStatement, T argument) throws SQLException;
}
