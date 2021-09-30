// Created: 12.01.2017
package de.freese.base.persistence.jdbc.template.function;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Inspired by org.springframework.jdbc.core<br>
 *
 * @author Thomas Freese
 *
 * @param <T> Argument-Type
 */
@FunctionalInterface
public interface ParameterizedPreparedStatementSetter<T>
{
    /**
     * @param preparedStatement {@link PreparedStatement}
     * @param argument Object
     *
     * @throws SQLException Falls was schief geht.
     */
    void setValues(PreparedStatement preparedStatement, T argument) throws SQLException;
}
