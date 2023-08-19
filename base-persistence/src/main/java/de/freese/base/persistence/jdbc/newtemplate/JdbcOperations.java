// Created: 19.08.23
package de.freese.base.persistence.jdbc.newtemplate;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.base.persistence.jdbc.template.function.ConnectionCallback;
import de.freese.base.persistence.jdbc.template.function.StatementCallback;
import de.freese.base.persistence.jdbc.template.function.StatementCreator;
import de.freese.base.utils.JdbcUtils;

/**
 * @author Thomas Freese
 */
public class JdbcOperations {

    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcOperations.class);

    public void close(final Statement statement) {
        LOGGER.debug("close statement");

        try {
            JdbcUtils.close(statement);
        }
        catch (Exception ex) {
            LOGGER.error("Could not close JDBC Statement", ex);
        }
    }

    public void close(final Connection connection) {
        //        getTransactionHandler().close(connection, getDataSource());
        LOGGER.debug("close connection");

        try {
            JdbcUtils.close(connection);
        }
        catch (Exception ex) {
            LOGGER.error("Could not close JDBC Connection", ex);
        }
    }

    public void close(final ResultSet resultSet) {
        LOGGER.debug("close resultSet");

        try {
            JdbcUtils.close(resultSet);
        }
        catch (Exception ex) {
            LOGGER.error("Could not close JDBC ResultSet", ex);
        }
    }

    public <S extends Statement, T> T execute(Supplier<Connection> connectionSupplier, final StatementCreator<S> sc, final StatementCallback<S, T> action, final boolean closeResources) {
        ConnectionCallback<T> connectionCallback = con -> {
            S stmt = null;

            try {
                stmt = sc.createStatement(con);

                T result = action.doInStatement(stmt);

                handleWarnings(stmt);

                return result;
            }
            catch (SQLException ex) {
                throw convertException(ex);
            }
            finally {
                if (closeResources) {
                    close(stmt);
                }
            }
        };

        return execute(connectionSupplier, connectionCallback, closeResources);
    }

    public <T> T execute(Supplier<Connection> connectionSupplier, final ConnectionCallback<T> action, final boolean closeResources) {
        Connection connection = null;

        try {
            connection = connectionSupplier.get();

            return action.doInConnection(connection);
        }
        catch (SQLException ex) {
            throw convertException(ex);
        }
        finally {
            if (closeResources) {
                close(connection);
            }
        }
    }

    protected RuntimeException convertException(final Exception ex) {
        Throwable th = ex;

        if (th instanceof RuntimeException e) {
            throw e;
        }

        if (th.getCause() instanceof SQLException) {
            th = th.getCause();
        }

        // while (!(th instanceof SQLException))
        // {
        // th = th.getCause();
        // }

        return new RuntimeException(th);
    }

    protected void handleWarnings(final Statement stmt) throws SQLException {
        if (LOGGER.isDebugEnabled()) {
            SQLWarning warning = stmt.getWarnings();

            while (warning != null) {
                LOGGER.debug("SQLWarning ignored: SQL state '{}', error code '{}', message [{}]", warning.getSQLState(), warning.getErrorCode(), warning.getMessage());

                warning = warning.getNextWarning();
            }
        }
    }
}
