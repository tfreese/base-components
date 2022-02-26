// Created: 24.05.2016
package de.freese.base.persistence.jdbc.datasource;

import java.io.PrintWriter;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.slf4j.LoggerFactory;

/**
 * Implementierung analog der org.springframework.jdbc.datasource.SingleConnectionDataSource<br>
 * jedoch ohne die Abhängigkeiten zum Spring-Framework.<br>
 * Die Connection wird in einem Proxy verpackt, der die close-Methode nicht ausführt.
 *
 * @author Thomas Freese
 */
public class SingleDataSource implements DataSource, AutoCloseable
{
    /**
     *
     */
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger("SimpleDataSource");
    /**
     *
     */
    private final ReentrantLock reentrantLock = new ReentrantLock();
    /**
     *
     */
    private Boolean autoCommit;
    /**
     *
     */
    private Connection connection;
    /**
     *
     */
    private Properties connectionProperties;
    /**
     *
     */
    private String password;
    /**
     *
     */
    private Connection proxyConnection;
    /**
     *
     */
    private Boolean readOnly;
    /**
     *
     */
    private String url;
    /**
     *
     */
    private String username;

    /**
     * @see java.lang.AutoCloseable#close()
     */
    @Override
    public void close()
    {
        try
        {
            destroy();
        }
        catch (Exception th)
        {
            LOGGER.error(null, th);
        }
    }

    /**
     *
     */
    public void destroy()
    {
        this.reentrantLock.lock();

        try
        {
            closeConnection();
        }
        finally
        {
            this.reentrantLock.unlock();
        }
    }

    /**
     * @see javax.sql.DataSource#getConnection()
     */
    @Override
    public Connection getConnection() throws SQLException
    {
        this.reentrantLock.lock();

        try
        {
            if (this.connection == null)
            {
                initConnection();
            }

            if (this.connection.isClosed())
            {
                throw new SQLException("Connection was closed in SingleConnectionDataSource. Check that user code checks "
                        + "shouldClose() before closing Connections, or set 'suppressClose' to 'true'");
            }
        }
        finally
        {
            this.reentrantLock.unlock();
        }

        return this.proxyConnection;
    }

    /**
     * @see javax.sql.DataSource#getConnection(java.lang.String, java.lang.String)
     */
    @Override
    public Connection getConnection(final String username, final String password) throws SQLException
    {
        if (Objects.equals(username, getUsername()) && Objects.equals(password, getPassword()))
        {
            return getConnection();
        }

        throw new SQLException("SimpleDataSource does not support custom username and password");
    }

    /**
     * @return {@link Properties}
     */
    public Properties getConnectionProperties()
    {
        return this.connectionProperties;
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

        // return null;
        // throw new SQLFeatureNotSupportedException();
    }

    /**
     * @return String
     */
    public String getPassword()
    {
        return this.password;
    }

    /**
     * @return String
     */
    public String getUrl()
    {
        return this.url;
    }

    /**
     * @return String
     */
    public String getUsername()
    {
        return this.username;
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
     * @param autoCommit boolean
     */
    public void setAutoCommit(final boolean autoCommit)
    {
        this.autoCommit = autoCommit;
    }

    /**
     * @param connectionProperties {@link Properties}
     */
    public void setConnectionProperties(final Properties connectionProperties)
    {
        Objects.requireNonNull(connectionProperties);

        this.connectionProperties = connectionProperties;
    }

    /**
     * @param driverClassName String
     */
    public void setDriverClassName(final String driverClassName)
    {
        Objects.requireNonNull(driverClassName);

        final String driverClassNameToUse = driverClassName.trim();

        try
        {
            Class.forName(driverClassNameToUse, true, Thread.currentThread().getContextClassLoader());
        }
        catch (final ClassNotFoundException ex)
        {
            throw new IllegalStateException("Could not load JDBC driver class [" + driverClassNameToUse + "]", ex);
        }

        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Loaded JDBC driver: {}", driverClassNameToUse);
        }
    }

    /**
     * @see javax.sql.CommonDataSource#setLogWriter(java.io.PrintWriter)
     */
    @Override
    public void setLogWriter(final PrintWriter out) throws SQLException
    {
        throw new UnsupportedOperationException("setLogWriter");
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
     * @param password String
     */
    public void setPassword(final String password)
    {
        Objects.requireNonNull(password);

        this.password = password.trim();
    }

    /**
     * @param readOnly boolean
     */
    public void setReadOnly(final boolean readOnly)
    {
        this.readOnly = readOnly;
    }

    /**
     * @param url String
     */
    public void setUrl(final String url)
    {
        Objects.requireNonNull(url);

        this.url = url.trim();
    }

    /**
     * @param username String
     */
    public void setUsername(final String username)
    {
        Objects.requireNonNull(username);

        this.username = username.trim();
    }

    /**
     * @see java.sql.Wrapper#unwrap(java.lang.Class)
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T unwrap(final Class<T> iface) throws SQLException
    {
        if (iface.isInstance(this))
        {
            return (T) this;
        }

        throw new SQLException("DataSource of type [" + getClass().getName() + "] cannot be unwrapped as [" + iface.getName() + "]");
    }

    /**
     *
     */
    private void closeConnection()
    {
        if (this.connection != null)
        {
            this.proxyConnection = null;

            try
            {
                this.connection.close();
            }
            catch (final Exception th)
            {
                LOGGER.warn("Could not close shared JDBC Connection", th);
            }
        }
    }

    /**
     * @return Boolean
     */
    private Boolean getAutoCommitValue()
    {
        return this.autoCommit;
    }

    /**
     * @param connection {@link Connection}
     *
     * @return {@link Connection}
     */
    private Connection getCloseSuppressingConnectionProxy(final Connection connection)
    {
        return (Connection) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[]
                {
                        Connection.class
                }, new ConnectionNotClosingInvocationHandler(connection));
    }

    /**
     * @param props {@link Properties}
     *
     * @return {@link Connection}
     *
     * @throws SQLException Falls was schief geht.
     */
    private Connection getConnectionFromDriver(final Properties props) throws SQLException
    {
        final String _url = getUrl();

        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Creating new JDBC DriverManager Connection to [{}]", _url);
        }

        return getConnectionFromDriverManager(_url, props);
    }

    /**
     * @param username String
     * @param password String
     *
     * @return {@link Connection}
     *
     * @throws SQLException Falls was schief geht.
     */
    private Connection getConnectionFromDriver(final String username, final String password) throws SQLException
    {
        final Properties mergedProps = new Properties();
        final Properties connProps = getConnectionProperties();

        if (connProps != null)
        {
            mergedProps.putAll(connProps);
        }

        if (username != null)
        {
            mergedProps.setProperty("user", username);
        }

        if (password != null)
        {
            mergedProps.setProperty("password", password);
        }

        return getConnectionFromDriver(mergedProps);
    }

    /**
     * @param url String
     * @param props {@link Properties}
     *
     * @return {@link Connection}
     *
     * @throws SQLException Falls was schief geht.
     */
    private Connection getConnectionFromDriverManager(final String url, final Properties props) throws SQLException
    {
        return DriverManager.getConnection(url, props);
    }

    /**
     * @return Boolean
     */
    private Boolean getReadOnlyValue()
    {
        return this.readOnly;
    }

    /**
     * @throws SQLException Falls was schief geht.
     */
    private void initConnection() throws SQLException
    {
        if (getUrl() == null)
        {
            throw new IllegalStateException("'url' property is required for lazily initializing a Connection");
        }

        closeConnection();

        this.connection = getConnectionFromDriver(getUsername(), getPassword());
        prepareConnection(this.connection);

        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Established shared JDBC Connection: {}", this.connection);
        }

        this.proxyConnection = getCloseSuppressingConnectionProxy(this.connection);
    }

    /**
     * @param con {@link Connection}
     *
     * @throws SQLException Falls was schief geht.
     */
    private void prepareConnection(final Connection con) throws SQLException
    {
        final Boolean _readOnly = getReadOnlyValue();

        if ((_readOnly != null) && (con.isReadOnly() != _readOnly))
        {
            con.setReadOnly(_readOnly);
        }

        final Boolean _autoCommit = getAutoCommitValue();

        if ((_autoCommit != null) && (con.getAutoCommit() != _autoCommit))
        {
            con.setAutoCommit(_autoCommit);
        }
    }
}
