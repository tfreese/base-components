/**
 * Created: 04.02.2017
 */

package de.freese.base.persistence.jdbc.template.function;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Analog-Implementierung vom org.springframework.jdbc.core.CallableStatementCreator<br>
 * jedoch ohne die Abhängigkeiten zum Spring-Framework.<br>
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface CallableStatementCreator
{
    /**
     * Erzeugt ein {@link CallableStatement}.
     *
     * @param connection {@link Connection}
     * @return {@link CallableStatement}
     * @throws SQLException Falls was schief geht.
     */
    public CallableStatement createCallableStatement(Connection connection) throws SQLException;
}
