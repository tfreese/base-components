// Created: 12.01.2017
package de.freese.base.persistence.jdbc.template.function;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Inspired by org.springframework.jdbc.core<br>
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface PreparedStatementSetter
{
    /**
     * @param preparedStatement {@link PreparedStatement}
     *
     * @throws SQLException Falls was schiefgeht.
     */
    void setValues(PreparedStatement preparedStatement) throws SQLException;
}
