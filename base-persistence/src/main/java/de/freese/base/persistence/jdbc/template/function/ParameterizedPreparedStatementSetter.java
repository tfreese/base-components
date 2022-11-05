// Created: 12.01.2017
package de.freese.base.persistence.jdbc.template.function;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Inspired by org.springframework.jdbc.core<br>
 *
 * @param <T> Argument-Type
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface ParameterizedPreparedStatementSetter<T>
{
    void setValues(PreparedStatement preparedStatement, T argument) throws SQLException;
}
