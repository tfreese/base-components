// Created: 04.02.2017
package de.freese.base.persistence.jdbc.template.function;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Inspired by org.springframework.jdbc.core<br>
 *
 * @author Thomas Freese
 *
 * @param <T> Return-Type
 */
@FunctionalInterface
public interface PreparedStatementCallback<T>
{
    /**
     * @param statement {@link PreparedStatement}
     *
     * @return Object
     *
     * @throws SQLException Falls was schief geht.
     */
    T doInStatement(PreparedStatement statement) throws SQLException;
}
