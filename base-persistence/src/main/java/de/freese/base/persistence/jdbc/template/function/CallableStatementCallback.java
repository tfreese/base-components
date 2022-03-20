// Created: 04.02.2017
package de.freese.base.persistence.jdbc.template.function;

import java.sql.CallableStatement;
import java.sql.SQLException;

/**
 * Inspired by org.springframework.jdbc.core<br>
 *
 * @param <T> Return-Type
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface CallableStatementCallback<T>
{
    /**
     * @param statement {@link CallableStatement}
     *
     * @return Object
     *
     * @throws SQLException Falls was schiefgeht.
     */
    T doInStatement(CallableStatement statement) throws SQLException;
}
