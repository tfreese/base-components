// Created: 04.02.2017
package de.freese.base.persistence.jdbc.template.function;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * Inspired by org.springframework.jdbc.core<br>
 *
 * @param <T> Return-Type
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface StatementCallback<T>
{
    /**
     * @param statement {@link Statement}
     *
     * @return Object
     *
     * @throws SQLException Falls was schiefgeht.
     */
    T doInStatement(Statement statement) throws SQLException;
}
