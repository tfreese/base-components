/**
 * Created: 04.02.2017
 */

package de.freese.base.persistence.jdbc.template.function;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Erzeugt ein {@link Statement}.
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface StatementCreator
{
    /**
     * Erzeugt ein {@link Statement}.
     *
     * @param connection {@link Connection}
     * @return {@link Statement}
     * @throws SQLException Falls was schief geht.
     */
    public Statement createStatement(Connection connection) throws SQLException;
}
