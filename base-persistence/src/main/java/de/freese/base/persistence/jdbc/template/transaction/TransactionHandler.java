// Created: 18.06.23
package de.freese.base.persistence.jdbc.template.transaction;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * @author Thomas Freese
 */
public interface TransactionHandler {
    void beginTransaction(DataSource dataSource) throws SQLException;

    void close(Connection connection, DataSource dataSource);

    void commitTransaction() throws SQLException;

    Connection getConnection(DataSource dataSource) throws SQLException;

    void rollbackTransaction() throws SQLException;
}
