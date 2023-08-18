// Created: 18.06.23
package de.freese.base.persistence.jdbc.template.transaction;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.base.utils.JdbcUtils;

/**
 * @author Thomas Freese
 */
public class SimpleTransactionHandler implements TransactionHandler {
    private static final ThreadLocal<Connection> CONNECTIONS = new ThreadLocal<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTransactionHandler.class);

    @Override
    public void beginTransaction(final DataSource dataSource) throws SQLException {
        if (CONNECTIONS.get() != null) {
            throw new SQLException("Transaction already started");
        }

        Connection connection = getConnection(dataSource);

        if (connection.getAutoCommit()) {
            connection.setAutoCommit(false);
        }

        CONNECTIONS.set(connection);
    }

    @Override
    public void close(final Connection connection, final DataSource dataSource) {
        if (connection.equals(CONNECTIONS.get())) {
            return;
        }

        LOGGER.debug("close connection");

        try {
            JdbcUtils.close(connection);
        }
        catch (Exception ex) {
            LOGGER.error("Could not close JDBC Connection", ex);
        }
    }

    @Override
    public void commitTransaction() throws SQLException {
        Connection connection = CONNECTIONS.get();

        if (connection == null) {
            throw new SQLException("no Transaction active");
        }

        connection.commit();

        close(connection, null);

        CONNECTIONS.remove();
    }

    @Override
    public Connection getConnection(final DataSource dataSource) throws SQLException {
        Connection connection = CONNECTIONS.get();

        if (connection != null) {
            return connection;
        }

        return dataSource.getConnection();
    }

    @Override
    public void rollbackTransaction() throws SQLException {
        Connection connection = CONNECTIONS.get();

        if (connection == null) {
            throw new SQLException("no Transaction active");
        }

        connection.rollback();

        close(connection, null);

        CONNECTIONS.remove();
    }
}
