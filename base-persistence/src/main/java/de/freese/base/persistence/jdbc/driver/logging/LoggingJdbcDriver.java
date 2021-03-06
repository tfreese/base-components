/**
 *
 */
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
 * Transparenter JDBC-Treiber, der Logausgaben für konfigurierbare Methodennamen erzeugt.<br>
 * Der Treiber wird aktiviert durch das Url-Postfix: jdbc:logger:<br>
 * <br>
 * J2SE-Konfiguration:<br>
 * <code>
 * DriverManager.registerDriver(new LoggingJdbcDriver("execute", "executeQuery", "executeUpdate", "prepareStatement", "prepareCall"));<br>
 * Connection connection = DriverManager.getConnection("jdbc:logger:jdbc:generic:mem"))
 * </code><br>
 * <br>
 * J2EE-Konfiguration:<br>
 * Registrierung des Treibers z.b. als ServletListener, {@link LoggingJdbcDriverServletContextListener}<br>
 * </code>
 *
 * @author Thomas Freese
 */
public class LoggingJdbcDriver implements Driver
{
    /**
     *
     */
    private static final Set<String> LOG_METHODS = new HashSet<>();

    /**
    *
    */
    static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(LoggingJdbcDriver.class);
    /**
     *
     */
    public static final String PREFIX = "jdbc:logger:";

    /**
     *
     */
    public static void addDefaultLogMethods()
    {
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

    /**
     * @param logMethod String
     */
    public static void addLogMethod(final String logMethod)
    {
        LOG_METHODS.add(logMethod);
    }

    /**
     * Erstellt ein neues {@link LoggingJdbcDriver} Object.
     */
    public LoggingJdbcDriver()
    {
        super();
    }

    /**
     * @see java.sql.Driver#acceptsURL(java.lang.String)
     */
    @Override
    public boolean acceptsURL(final String url) throws SQLException
    {
        return url.startsWith(PREFIX);
    }

    /**
     * @see java.sql.Driver#connect(java.lang.String, java.util.Properties)
     */
    @SuppressWarnings("resource")
    @Override
    public Connection connect(final String url, final Properties info) throws SQLException
    {
        if (acceptsURL(url))
        {
            final Driver targetDriver = DriverManager.getDriver(url.substring(PREFIX.length()));
            final Connection targetConnection = targetDriver.connect(url.substring(PREFIX.length()), info);

            return (Connection) Proxy.newProxyInstance(ClassUtils.getDefaultClassLoader(), new Class<?>[]
            {
                    Connection.class
            }, new LoggingJdbcInvocationHandler(targetConnection, LOG_METHODS));
        }

        return null;
    }

    /**
     * @see java.sql.Driver#getMajorVersion()
     */
    @Override
    public int getMajorVersion()
    {
        return 1;
    }

    /**
     * @see java.sql.Driver#getMinorVersion()
     */
    @Override
    public int getMinorVersion()
    {
        return 1;
    }

    /**
     * @see java.sql.Driver#getParentLogger()
     */
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException
    {
        throw new SQLFeatureNotSupportedException();
        // return null;
    }

    /**
     * @see java.sql.Driver#getPropertyInfo(java.lang.String, java.util.Properties)
     */
    @Override
    public DriverPropertyInfo[] getPropertyInfo(final String url, final Properties info) throws SQLException
    {
        final Driver targetDriver = DriverManager.getDriver(url.substring(PREFIX.length()));

        return targetDriver.getPropertyInfo(url, info);
    }

    /**
     * @see java.sql.Driver#jdbcCompliant()
     */
    @Override
    public boolean jdbcCompliant()
    {
        return true;
    }
}
