// Created: 12.01.2017
package de.freese.base.persistence.jdbc.function;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Inspired by org.springframework.jdbc.core<br>
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface ParameterizedPreparedStatementSetter<T> {
    void setParameter(PreparedStatement preparedStatement, T argument) throws SQLException;
}
