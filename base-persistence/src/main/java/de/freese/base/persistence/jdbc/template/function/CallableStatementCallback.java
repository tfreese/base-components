// Created: 04.02.2017
package de.freese.base.persistence.jdbc.template.function;

import java.sql.CallableStatement;
import java.sql.SQLException;

/**
 * Inspired by org.springframework.jdbc.core<br>
 *
 * @author Thomas Freese
 *
 * @param <T> Return-Type
 */
@FunctionalInterface
public interface CallableStatementCallback<T>
{
    /**
     * @param statement {@link CallableStatement}
     *
     * @return Object
     *
     * @throws SQLException Falls was schief geht.
     */
    T doInStatement(CallableStatement statement) throws SQLException;
}
