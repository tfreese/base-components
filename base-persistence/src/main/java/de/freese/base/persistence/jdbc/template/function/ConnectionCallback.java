// Created: 04.02.2017
package de.freese.base.persistence.jdbc.template.function;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Inspired by org.springframework.jdbc.core.ConnectionCallback<br>
 *
 * @author Thomas Freese
 *
 * @param <T> Type
 */
@FunctionalInterface
public interface ConnectionCallback<T>
{
    /**
     * Ausführung von Code in einer {@link Connection}.
     *
     * @param connection {@link Connection}
     *
     * @return Object
     *
     * @throws SQLException Falls was schief geht.
     */
    T doInConnection(Connection connection) throws SQLException;
}
