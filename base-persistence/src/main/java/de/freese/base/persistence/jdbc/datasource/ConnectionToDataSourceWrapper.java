// Created: 07.06.2016
package de.freese.base.persistence.jdbc.datasource;

import java.io.PrintWriter;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 * {@link DataSource}-Wrapper für eine einzelne {@link Connection}.<br>
 * Die Connection wird in einem Proxy verpackt, der die close-Methode nicht ausführt.
 *
 * @author Thomas Freese
 */
public class ConnectionToDataSourceWrapper implements DataSource {
    private final Connection connection;

    public ConnectionToDataSourceWrapper(final Connection connection) {
        super();

        this.connection = (Connection) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[]{Connection.class},
                new ConnectionNotClosingInvocationHandler(connection));
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.connection;
    }

    @Override
    public Connection getConnection(final String username, final String password) throws SQLException {
        return getConnection();
    }

    @Override
    public PrintWriter getLogWriter() {
        throw new UnsupportedOperationException("getLogWriter");
    }

    @Override
    public int getLoginTimeout() {
        return 0;
    }

    @Override
    public Logger getParentLogger() {
        return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    }

    @Override
    public boolean isWrapperFor(final Class<?> iface) {
        return iface.isInstance(this);
    }

    @Override
    public void setLogWriter(final PrintWriter out) {
        throw new UnsupportedOperationException("setLoginTimeout");
    }

    @Override
    public void setLoginTimeout(final int seconds) {
        throw new UnsupportedOperationException("setLoginTimeout");
    }

    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return iface.cast(this);
        }

        throw new SQLException("DataSource of type [" + getClass().getName() + "] cannot be unwrapped as [" + iface.getName() + "]");
    }
}
