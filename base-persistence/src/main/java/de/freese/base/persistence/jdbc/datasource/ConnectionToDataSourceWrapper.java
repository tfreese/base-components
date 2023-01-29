// Created: 07.06.2016
package de.freese.base.persistence.jdbc.datasource;

import java.io.PrintWriter;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 * {@link DataSource}-Wrapper für eine einzelne {@link Connection}.<br>
 * Die Connection wird in einem Proxy verpackt, der die close-Methode nicht ausführt.
 *
 * @author Thomas Freese
 */
public class ConnectionToDataSourceWrapper implements DataSource
{
    private final Connection connection;

    public ConnectionToDataSourceWrapper(final Connection connection)
    {
        super();

        this.connection = (Connection) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[]
                {
                        Connection.class
                }, new ConnectionNotClosingInvocationHandler(connection));
    }

    /**
     * @see javax.sql.DataSource#getConnection()
     */
    @Override
    public Connection getConnection() throws SQLException
    {
        return this.connection;
    }

    /**
     * @see javax.sql.DataSource#getConnection(java.lang.String, java.lang.String)
     */
    @Override
    public Connection getConnection(final String username, final String password) throws SQLException
    {
        return getConnection();
    }

    /**
     * @see javax.sql.CommonDataSource#getLogWriter()
     */
    @Override
    public PrintWriter getLogWriter() throws SQLException
    {
        throw new UnsupportedOperationException("getLogWriter");
    }

    /**
     * @see javax.sql.CommonDataSource#getLoginTimeout()
     */
    @Override
    public int getLoginTimeout() throws SQLException
    {
        return 0;
    }

    /**
     * @see javax.sql.CommonDataSource#getParentLogger()
     */
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException
    {
        return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    }

    /**
     * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
     */
    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException
    {
        return iface.isInstance(this);
    }

    /**
     * @see javax.sql.CommonDataSource#setLogWriter(java.io.PrintWriter)
     */
    @Override
    public void setLogWriter(final PrintWriter out) throws SQLException
    {
        throw new UnsupportedOperationException("setLoginTimeout");
    }

    /**
     * @see javax.sql.CommonDataSource#setLoginTimeout(int)
     */
    @Override
    public void setLoginTimeout(final int seconds) throws SQLException
    {
        throw new UnsupportedOperationException("setLoginTimeout");
    }

    /**
     * @see java.sql.Wrapper#unwrap(java.lang.Class)
     */
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException
    {
        if (iface.isInstance(this))
        {
            return iface.cast(this);
        }

        throw new SQLException("DataSource of type [" + getClass().getName() + "] cannot be unwrapped as [" + iface.getName() + "]");
    }
}
