/**
 * Created: 04.02.2017
 */

package de.freese.base.persistence.jdbc.template.function;

import java.sql.CallableStatement;
import java.sql.SQLException;

/**
 * Analog-Implementierung vom org.springframework.jdbc.core.CallableStatementCallback<br>
 * jedoch ohne die Abhängigkeiten zum Spring-Framework.<br>
 *
 * @author Thomas Freese
 * @param <T> Konkreter Return-Typ
 */
@FunctionalInterface
public interface CallableStatementCallback<T>
{
    /**
     * Ausführung von Code für ein {@link CallableStatement}.
     *
     * @param callableStatement {@link CallableStatement}
     * @return Object
     * @throws SQLException Falls was schief geht.
     */
    public T doInCallableStatement(CallableStatement callableStatement) throws SQLException;
}
