// Created: 12.01.2017
package de.freese.base.persistence.jdbc.template.function;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Analog-Implementierung vom org.springframework.jdbc.core.ParameterizedPreparedStatementSetter<br>
 * jedoch ohne die Abhängigkeiten zum Spring-Framework.<br>
 *
 * @author Thomas Freese
 *
 * @param <T> Konkreter Row-Typ
 */
@FunctionalInterface
public interface ParameterizedPreparedStatementSetter<T>
{
    /**
     * Setzt die Values des {@link PreparedStatement}.
     *
     * @param preparedStatement {@link PreparedStatement}
     * @param argument Object
     *
     * @throws SQLException Falls was schief geht.
     */
    void setValues(PreparedStatement preparedStatement, T argument) throws SQLException;
}
