// Created: 04.02.2017
package de.freese.base.persistence.jdbc.template.function;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * Inspired by org.springframework.jdbc.core.StatementCallback<br>
 *
 * @author Thomas Freese
 *
 * @param <S> Statement-Type
 * @param <T> Return-Type
 */
@FunctionalInterface
public interface StatementCallback<S extends Statement, T>
{
    /**
     * Ausführung von Code für ein {@link Statement}.
     *
     * @param statement {@link Statement}
     *
     * @return Object
     *
     * @throws SQLException Falls was schief geht.
     */
    T doInStatement(S statement) throws SQLException;
}
