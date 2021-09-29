// Created: 04.02.2017
package de.freese.base.persistence.jdbc.template.function;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Creates a {@link Statement}.
 *
 * @author Thomas Freese
 *
 * @param <S> Konkretes Statement
 */
@FunctionalInterface
public interface StatementCreator<S extends Statement>
{
    /**
     * Creates a {@link Statement}.
     *
     * @param connection {@link Connection}
     *
     * @return {@link Statement}
     *
     * @throws SQLException Falls was schief geht.
     */
    S createStatement(Connection connection) throws SQLException;
}
