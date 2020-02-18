/**
 * Created: 04.02.2017
 */

package de.freese.base.persistence.jdbc.template.function;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Analog-Implementierung vom org.springframework.jdbc.core.PreparedStatementCreator<br>
 * jedoch ohne die Abhängigkeiten zum Spring-Framework.<br>
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface PreparedStatementCreator
{
    /**
     * Erzeugt ein {@link PreparedStatement}.
     *
     * @param connection {@link Connection}
     * @return {@link PreparedStatement}
     * @throws SQLException Falls was schief geht.
     */
    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException;
}
