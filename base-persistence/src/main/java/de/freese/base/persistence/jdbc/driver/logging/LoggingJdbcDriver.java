// Created: 08.09.2016
package de.freese.base.persistence.jdbc.driver.logging;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;

/**
 * Transparent JDBC-Driver, who is creating Logs for configurable Method-Names.<br>
 * The Driver is activated by Url-Postfix: jdbc:logger:<br>
 * <br>
 * J2SE-Configuration:<br>
 * <pre>
 * {@code
 * DriverManager.registerDriver(new LoggingJdbcDriver("execute", "executeQuery", "executeUpdate", "prepareStatement", "prepareCall"));<br>
 * Connection connection = DriverManager.getConnection("jdbc:logger:jdbc:generic:mem"))
 * }</pre>
 * <br>
 * J2EE-Konfiguration:<br>
 * Register the Driver by ServletListener: {@link LoggingJdbcDriverServletContextListener}<br>
 *
 * @author Thomas Freese
 */
public class LoggingJdbcDriver implements Driver {
    public static final String PREFIX = "jdbc:logger:";

    static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(LoggingJdbcDriver.class);

    private static final Set<String> LOG_METHODS = new HashSet<>();

    public static void addDefaultLogMethods() {
        addLogMethod("execute");
        addLogMethod("executeQuery");
        addLogMethod("executeUpdate");
        addLogMethod("prepareStatement");
        addLogMethod("prepareCall");

        // Für PreparedStatements
        addLogMethod("setByte");
        addLogMethod("setBytes");
        addLogMethod("setDate");
        addLogMethod("setDouble");
        addLogMethod("setFloat");
        addLogMethod("setInt");
        addLogMethod("setLong");
        addLogMethod("setNull");
        addLogMethod("setObject");
        addLogMethod("setShort");
        addLogMethod("setString");
        addLogMethod("setTime");
    }

    public static void addLogMethod(final String logMethod) {
        LOG_METHODS.add(logMethod);
    }

    @Override
    public boolean acceptsURL(final String url) throws SQLException {
        return url.startsWith(PREFIX);
    }

    @Override
    public Connection connect(final String url, final Properties info) throws SQLException {
        if (acceptsURL(url)) {
            final Driver targetDriver = DriverManager.getDriver(url.substring(PREFIX.length()));
            final Connection targetConnection = targetDriver.connect(url.substring(PREFIX.length()), info);

            return (Connection) Proxy.newProxyInstance(ClassUtils.getDefaultClassLoader(), new Class<?>[]{Connection.class}, new LoggingJdbcInvocationHandler(targetConnection, LOG_METHODS));
        }

        return null;
    }

    @Override
    public int getMajorVersion() {
        return 1;
    }

    @Override
    public int getMinorVersion() {
        return 1;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
        // return null;
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(final String url, final Properties info) throws SQLException {
        final Driver targetDriver = DriverManager.getDriver(url.substring(PREFIX.length()));

        return targetDriver.getPropertyInfo(url, info);
    }

    @Override
    public boolean jdbcCompliant() {
        return true;
    }
}
