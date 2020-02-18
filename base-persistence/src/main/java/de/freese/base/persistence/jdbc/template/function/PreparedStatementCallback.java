/**
 * Created: 04.02.2017
 */

package de.freese.base.persistence.jdbc.template.function;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Analog-Implementierung vom org.springframework.jdbc.core.PreparedStatementCallback<br>
 * jedoch ohne die Abhängigkeiten zum Spring-Framework.<br>
 *
 * @author Thomas Freese
 * @param <T> Konkreter Return-Typ
 */
@FunctionalInterface
public interface PreparedStatementCallback<T>
{
    /**
     * Ausführung von Code für ein {@link PreparedStatement}.
     *
     * @param preparedStatement {@link PreparedStatement}
     * @return Object
     * @throws SQLException Falls was schief geht.
     */
    public T doInPreparedStatement(PreparedStatement preparedStatement) throws SQLException;
}
