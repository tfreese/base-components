// Created: 12.01.2017
package de.freese.base.persistence.jdbc.template.function;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Analog-Implementierung vom org.springframework.jdbc.core.PreparedStatementSetter<br>
 * jedoch ohne die Abhängigkeiten zum Spring-Framework.<br>
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface PreparedStatementSetter
{
    /**
     * Setzt die Values des {@link PreparedStatement}.
     *
     * @param preparedStatement {@link PreparedStatement}
     *
     * @throws SQLException Falls was schief geht.
     */
    void setValues(PreparedStatement preparedStatement) throws SQLException;
}
