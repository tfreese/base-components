// Created: 30.08.23
package de.freese.base.persistence.jdbc.transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.base.persistence.exception.PersistenceException;

/**
 * @author Thomas Freese
 */
public final class SimpleTransaction implements Transaction {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTransaction.class);

    private Connection connection;

    public SimpleTransaction(final DataSource dataSource) {
        super();

        try {
            this.connection = Objects.requireNonNull(dataSource, "dataSource required").getConnection();
        }
        catch (SQLException ex) {
            throw new PersistenceException(ex);
        }
    }

    @Override
    public void begin() {
        try {
            validateConnection();
            getConnection().setAutoCommit(false);
        }
        catch (SQLException ex) {
            throw new PersistenceException(ex);
        }
    }

    @Override
    public void commit() {
        try {
            validateConnection();
            getConnection().commit();
            close();
        }
        catch (SQLException ex) {
            throw new PersistenceException(ex);
        }
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public void rollback() {
        try {
            validateConnection();
            getConnection().rollback();
            close();
        }
        catch (SQLException ex) {
            throw new PersistenceException(ex);
        }
    }

    private void close() {
        LOGGER.debug("close connection");

        try {
            if (connection == null || connection.isClosed()) {
                return;
            }

            connection.close();
            connection = null;
        }
        catch (SQLException ex) {
            throw new PersistenceException(ex);
        }
    }

    private void validateConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("connection is closed");
        }
    }
}
