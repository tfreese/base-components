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
public class SingleDataSource implements DataSource, AutoCloseable {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger("SimpleDataSource");

    private final ReentrantLock reentrantLock = new ReentrantLock();

    private Boolean autoCommit;
    private Connection connection;
    private Properties connectionProperties;
    private String password;
    private Connection proxyConnection;
    private Boolean readOnly;
    private String url;
    private String username;

    @Override
    public void close() {
        try {
            destroy();
        }
        catch (Exception th) {
            LOGGER.error(th.getMessage(), th);
        }
    }

    public void destroy() {
        this.reentrantLock.lock();

        try {
            closeConnection();
        }
        finally {
            this.reentrantLock.unlock();
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        this.reentrantLock.lock();

        try {
            if (this.connection == null) {
                initConnection();
            }

            if (this.connection.isClosed()) {
                throw new SQLException(
                        "Connection was closed in SingleConnectionDataSource. Check that user code checks shouldClose() before closing Connections, or set 'suppressClose' to 'true'");
            }
        }
        finally {
            this.reentrantLock.unlock();
        }

        return this.proxyConnection;
    }

    @Override
    public Connection getConnection(final String username, final String password) throws SQLException {
        if (Objects.equals(username, getUsername()) && Objects.equals(password, getPassword())) {
            return getConnection();
        }

        throw new SQLException("SimpleDataSource does not support custom username and password");
    }

    public Properties getConnectionProperties() {
        return this.connectionProperties;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        throw new UnsupportedOperationException("getLogWriter");
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

        // return null;
        // throw new SQLFeatureNotSupportedException();
    }

    public String getPassword() {
        return this.password;
    }

    public String getUrl() {
        return this.url;
    }

    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return iface.isInstance(this);
    }

    public void setAutoCommit(final boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    public void setConnectionProperties(final Properties connectionProperties) {
        Objects.requireNonNull(connectionProperties);

        this.connectionProperties = new Properties(connectionProperties);
    }

    public void setDriverClassName(final String driverClassName) {
        Objects.requireNonNull(driverClassName);

        final String driverClassNameToUse = driverClassName.strip();

        try {
            Class.forName(driverClassNameToUse, true, Thread.currentThread().getContextClassLoader());
        }
        catch (final ClassNotFoundException ex) {
            throw new IllegalStateException("Could not load JDBC driver class [" + driverClassNameToUse + "]", ex);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Loaded JDBC driver: {}", driverClassNameToUse);
        }
    }

    @Override
    public void setLogWriter(final PrintWriter out) throws SQLException {
        throw new UnsupportedOperationException("setLogWriter");
    }

    @Override
    public void setLoginTimeout(final int seconds) throws SQLException {
        throw new UnsupportedOperationException("setLoginTimeout");
    }

    public void setPassword(final String password) {
        Objects.requireNonNull(password);

        this.password = password.strip();
    }

    public void setReadOnly(final boolean readOnly) {
        this.readOnly = readOnly;
    }

    public void setUrl(final String url) {
        Objects.requireNonNull(url);

        this.url = url.strip();
    }

    public void setUsername(final String username) {
        Objects.requireNonNull(username);

        this.username = username.strip();
    }

    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return iface.cast(this);
        }

        throw new SQLException("DataSource of type [" + getClass().getName() + "] cannot be unwrapped as [" + iface.getName() + "]");
    }

    private void closeConnection() {
        if (this.connection != null) {
            this.proxyConnection = null;

            try {
                this.connection.close();
            }
            catch (final Exception th) {
                LOGGER.warn("Could not close shared JDBC Connection", th);
            }
        }
    }

    private Boolean getAutoCommitValue() {
        return this.autoCommit;
    }

    private Connection getCloseSuppressingConnectionProxy(final Connection connection) {
        return (Connection) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[]{Connection.class},
                new ConnectionNotClosingInvocationHandler(connection));
    }

    private Connection getConnectionFromDriver(final Properties props) throws SQLException {
        final String _url = getUrl();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Creating new JDBC DriverManager Connection to [{}]", _url);
        }

        return getConnectionFromDriverManager(_url, props);
    }

    private Connection getConnectionFromDriver(final String username, final String password) throws SQLException {
        final Properties mergedProps = new Properties();
        final Properties connProps = getConnectionProperties();

        if (connProps != null) {
            mergedProps.putAll(connProps);
        }

        if (username != null) {
            mergedProps.setProperty("user", username);
        }

        if (password != null) {
            mergedProps.setProperty("password", password);
        }

        return getConnectionFromDriver(mergedProps);
    }

    private Connection getConnectionFromDriverManager(final String url, final Properties props) throws SQLException {
        return DriverManager.getConnection(url, props);
    }

    private Boolean getReadOnlyValue() {
        return this.readOnly;
    }

    private void initConnection() throws SQLException {
        if (getUrl() == null) {
            throw new IllegalStateException("'url' property is required for lazily initializing a Connection");
        }

        closeConnection();

        this.connection = getConnectionFromDriver(getUsername(), getPassword());
        prepareConnection(this.connection);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Established shared JDBC Connection: {}", this.connection);
        }

        this.proxyConnection = getCloseSuppressingConnectionProxy(this.connection);
    }

    private void prepareConnection(final Connection con) throws SQLException {
        final Boolean _readOnly = getReadOnlyValue();

        if ((_readOnly != null) && (con.isReadOnly() != _readOnly)) {
            con.setReadOnly(_readOnly);
        }

        final Boolean _autoCommit = getAutoCommitValue();

        if ((_autoCommit != null) && (con.getAutoCommit() != _autoCommit)) {
            con.setAutoCommit(_autoCommit);
        }
    }
}
