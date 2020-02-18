/**
 * Created: 04.02.2017
 */

package de.freese.base.persistence.jdbc.template.function;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * Analog-Implementierung vom org.springframework.jdbc.core.StatementCallback<br>
 * jedoch ohne die Abhängigkeiten zum Spring-Framework.<br>
 *
 * @author Thomas Freese
 * @param <T> Konkreter Return-Typ
 */
@FunctionalInterface
public interface StatementCallback<T>
{
    /**
     * Ausführung von Code für ein {@link Statement}.
     *
     * @param statement {@link Statement}
     * @return Object
     * @throws SQLException Falls was schief geht.
     */
    public T doInStatement(Statement statement) throws SQLException;
}
