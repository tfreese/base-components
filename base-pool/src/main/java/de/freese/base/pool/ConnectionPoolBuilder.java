/**
 * Created: 01.02.2014
 */
package de.freese.base.pool;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

/**
 * Erzeugt einen {@link ObjectPool} aus verschiedenen Implementierungen.<br>
 *
 * @author Thomas Freese
 */
public class ConnectionPoolBuilder extends AbstractPoolBuilder<ConnectionPoolBuilder>
{
    /**
     * Max. Alter einer "IDLE"-Connection.<br>
     * 600000 MilliSekunden
     */
    private static final int IDLE_TIMEOUT_MINUTES = 10;

    /**
     * Max. Alter einer Connection.<br>
     * 1800000 MilliSekunden
     */
    private static final int MAX_AGE_MINUTES = 30;

    /**
     * Erzeugt einen neuen Builder.
     *
     * @return {@link ConnectionPoolBuilder}
     */
    public static ConnectionPoolBuilder create()
    {
        ConnectionPoolBuilder builder = new ConnectionPoolBuilder();
        builder.min(1);
        builder.max(10);
        builder.validateOnGet(true);
        builder.determineValidationQuery(true);
        builder.poolPreparedStatements(true);

        return builder;
    }

    /**
     *
     */
    private boolean determineValidationQuery = false;

    /**
     *
     */
    private String driver = null;

    /**
     *
     */
    private boolean poolPreparedStatements = false;

    /**
     *
     */
    private String url = null;

    /**
     *
     */
    private String validationQuery = null;

    /**
     * Erstellt ein neues {@link ConnectionPoolBuilder} Object.
     */
    private ConnectionPoolBuilder()
    {
        super();
    }

    /**
     * Liefert eine {@link BasicDataSource}.<br>
     * Verwendete Attribute:
     * <ul>
     * <li>{@link #max}
     * <li>{@link #maxWait}
     * <li>{@link #min}
     * <li>{@link #validateOnGet}
     * <li>{@link #validateOnReturn}
     * <li>{@link #driver}
     * <li>{@link #url}
     * <li>{@link #poolPreparedStatements}
     * <li>{@link #validationQuery}
     * </ul>
     *
     * @param user     String
     * @param password {@link CharSequence}
     *
     * @return {@link DataSource}
     */
    public DataSource buildDBCP(final String user, final CharSequence password)
    {
        final BasicDataSource bds = new BasicDataSource();
        bds.setMaxTotal(getMax() <= 0 ? DEFAULT_MAX : getMax());
        bds.setMaxIdle(bds.getMaxTotal());
        bds.setMaxWaitMillis(getMaxWait());
        bds.setMinIdle(getMin() <= 0 ? DEFAULT_MIN : getMin());
        bds.setInitialSize(bds.getMinIdle());
        bds.setTestOnBorrow(isValidateOnGet());
        bds.setTestOnReturn(isValidateOnReturn());
        bds.setTestWhileIdle(false);
        bds.setDriverClassName(getDriver());
        bds.setUrl(getUrl());
        bds.setUsername(user);
        bds.setPassword(password.toString());
        bds.setPoolPreparedStatements(isPoolPreparedStatements());

        String query = getValidationQuery();

        if ((query == null) && isDetermineValidationQuery())
        {
            query = determineValidationQuery(getDriver());
        }

        if (query != null)
        {
            bds.setValidationQuery(query);
        }

        if (isRegisterShutdownHook())
        {
            Runtime.getRuntime().addShutdownHook(new Thread(() ->
            {
                try
                {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Close DataSource ");
                    sb.append(bds.getUsername()).append("@").append(bds.getUrl());
                    sb.append(" with ");
                    sb.append(bds.getNumIdle());
                    sb.append(" idle and ");
                    sb.append(bds.getNumActive());
                    sb.append(" active Connections");

                    getLogger().info(sb.toString());

                    bds.close();
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }, getDriver()));
        }

        return bds;
    }

    /**
     * Liefert eine {@link HikariDataSource}.<br>
     * Verwendete Attribute:
     * <ul>
     * <li>{@link #max}
     * <li>{@link #maxWait}
     * <li>{@link #min}
     * <li>{@link #driver}
     * <li>{@link #url}
     * <li>{@link #poolPreparedStatements}
     * <li>{@link #validationQuery}
     * </ul>
     *
     * @param user     String
     * @param password {@link CharSequence}
     *
     * @return {@link DataSource}
     */
    public DataSource buildHikari(final String user, final CharSequence password)
    {
        HikariConfig config = new HikariConfig();

        // Default = 10
        config.setMaximumPoolSize(getMax() <= 0 ? DEFAULT_MAX : getMax());

        // Default = 10
        config.setMinimumIdle(getMin() <= 0 ? DEFAULT_MIN : getMin());

        config.setDriverClassName(getDriver());
        config.setJdbcUrl(getUrl());
        config.setUsername(user);
        config.setPassword(password.toString());

        // Default = auto-generated
        config.setPoolName(user);

        // config.setThreadFactory(threadFactory);
        // config.setScheduledExecutor(executor);

        // Default = 30000 = 30 Sekunden
        // Max. N MilliSekunden auf eine Connection warten.
        config.setConnectionTimeout(5L * 1000L);

        // Default = 600000 = 10 Minuten
        // Nach N MilliSekunden wird die "IDLE"-Connection geschlossen.
        config.setIdleTimeout(TimeUnit.MINUTES.toMillis(IDLE_TIMEOUT_MINUTES));

        // Default = 1800000 = 30 Minuten
        // Nach N MilliSekunden wird die Connection geschlossen.
        config.setMaxLifetime(TimeUnit.MINUTES.toMillis(MAX_AGE_MINUTES));

        // Default = true
        config.setAutoCommit(false);

        // Default = false
        config.setReadOnly(false);

        // Default = none
        // If your driver supports JDBC4 we strongly recommend not setting this property !
        String vQuery = getValidationQuery();

        if ((vQuery == null) && isDetermineValidationQuery())
        {
            vQuery = determineValidationQuery(getDriver());
        }

        if (vQuery != null)
        {
            config.setConnectionTestQuery(vQuery);

            // Default = 5000 = 5 Sekunden
            // Nach N MilliSekunden wird die ValidationQuery als ungültig markiert.
            config.setValidationTimeout(3L * 1000L);
        }

        if (isPoolPreparedStatements())
        {
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            // config.addDataSourceProperty("useServerPrepStmts", "true");
            //
            // # https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration
            // cachePrepStmts: true
            // prepStmtCacheSize: 250
            // prepStmtCacheSqlLimit: 2048
            // useServerPrepStmts: true
            // useLocalSessionState: true
            // useLocalTransactionState: true
            // rewriteBatchedStatements: true
            // cacheResultSetMetadata: true
            // cacheServerConfiguration: true
            // elideSetAutoCommits: true
            // maintainTimeStats: false
        }

        HikariDataSource dataSource = new HikariDataSource(config);

        if (isRegisterShutdownHook())
        {
            Runtime.getRuntime().addShutdownHook(new Thread(() ->
            {
                try
                {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Close DataSource ");
                    sb.append(dataSource.getUsername()).append("@").append(dataSource.getJdbcUrl());
                    sb.append(" with ");
                    sb.append(dataSource.getHikariPoolMXBean().getIdleConnections());
                    sb.append(" idle and ");
                    sb.append(dataSource.getHikariPoolMXBean().getActiveConnections());
                    sb.append(" active Connections");

                    getLogger().info(sb.toString());

                    dataSource.close();
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }, getDriver()));
        }

        return dataSource;
    }

    /**
     * Liefert eine {@link org.apache.tomcat.jdbc.pool.DataSource}.<br>
     * Verwendete Attribute:
     * <ul>
     * <li>{@link #max}
     * <li>{@link #maxWait}
     * <li>{@link #min}
     * <li>{@link #validateOnGet}
     * <li>{@link #validateOnReturn}
     * <li>{@link #driver}
     * <li>{@link #url}
     * <li>{@link #validationQuery}
     * </ul>
     *
     * @param user     String
     * @param password {@link CharSequence}
     *
     * @return {@link DataSource}
     */
    public DataSource buildTomcat(final String user, final CharSequence password)
    {
        final PoolProperties poolProperties = new PoolProperties();
        poolProperties.setMaxActive(getMax() <= 0 ? DEFAULT_MAX : getMax());
        poolProperties.setMaxIdle(poolProperties.getMaxActive());
        poolProperties.setMinIdle(getMin() <= 0 ? DEFAULT_MIN : getMin());
        poolProperties.setInitialSize(poolProperties.getMinIdle());
        poolProperties.setTestOnBorrow(isValidateOnGet());
        poolProperties.setTestOnReturn(isValidateOnReturn());
        poolProperties.setTestWhileIdle(false);
        poolProperties.setDriverClassName(getDriver());
        poolProperties.setUrl(getUrl());
        poolProperties.setUsername(user);
        poolProperties.setPassword(password.toString());

        String query = getValidationQuery();

        if ((query == null) && isDetermineValidationQuery())
        {
            query = determineValidationQuery(getDriver());
        }

        if (query != null)
        {
            poolProperties.setValidationQuery(query);
            poolProperties.setValidationInterval(30 * 1000L); // Wurde eine Connection vor 30 Sekunden validiert, nicht nochmal validieren
        }

        poolProperties.setMinEvictableIdleTimeMillis(60 * 1000); // 60 Sekunden: Zeit nach der eine Connection als "Idle" markiert wird
        poolProperties.setTimeBetweenEvictionRunsMillis(60 * 1000); // Alle 60 Sekunden auf Idle-Connections prüfen
        poolProperties.setMaxAge(TimeUnit.MINUTES.toMillis(MAX_AGE_MINUTES));
        poolProperties.setJmxEnabled(false);
        poolProperties.setRemoveAbandoned(true);
        poolProperties.setLogAbandoned(true);
        poolProperties.setRemoveAbandonedTimeout(10 * 60); // Nach 10 Minuten Connections/Langläufer als verwaist markieren
        // poolProperties.setAbandonWhenPercentageFull(50); // Entfernen von verwaisten (Timeout) Connections/Langläufer erst ab Poolstand

        String jdbcInterceptors = "";
        // Caching für die Attribute autoCommit, readOnly, transactionIsolation und catalog.
        jdbcInterceptors += "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;";

        // Jede Query bei Langläufern setzt den Abandon-Timer zurück.
        jdbcInterceptors += "org.apache.tomcat.jdbc.pool.interceptor.ResetAbandonedTimer;";

        // Schliesst alle Statments, die durch createStatement, prepareStatement oder prepareCall erzeugt wurden, falls nötig.
        jdbcInterceptors += "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer";

        poolProperties.setJdbcInterceptors(jdbcInterceptors);

        final org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource(poolProperties);

        if (isRegisterShutdownHook())
        {
            Runtime.getRuntime().addShutdownHook(new Thread(() ->
            {
                try
                {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Close DataSource ");
                    sb.append(dataSource.getUsername()).append("@").append(dataSource.getUrl());
                    sb.append(" with ");
                    sb.append(dataSource.getNumIdle());
                    sb.append(" idle and ");
                    sb.append(dataSource.getNumActive());
                    sb.append(" active Connections");

                    getLogger().info(sb.toString());

                    dataSource.close();
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }, getDriver()));
        }

        return dataSource;
    }

    /**
     * Automatische Ermittlung der ValidationQuery für die Datenbank, wenn keine gesetzt wurde.
     *
     * @param determineValidationQuery boolean
     *
     * @return {@link ConnectionPoolBuilder}
     */
    public ConnectionPoolBuilder determineValidationQuery(final boolean determineValidationQuery)
    {
        this.determineValidationQuery = determineValidationQuery;

        return this;
    }

    /**
     * @param driver String
     *
     * @return {@link ConnectionPoolBuilder}
     */
    public ConnectionPoolBuilder driver(final String driver)
    {
        this.driver = driver;

        return this;
    }

    /**
     * @param poolPreparedStatements boolean
     *
     * @return {@link ConnectionPoolBuilder}
     */
    public ConnectionPoolBuilder poolPreparedStatements(final boolean poolPreparedStatements)
    {
        this.poolPreparedStatements = poolPreparedStatements;

        return this;
    }

    /**
     * @param url String
     *
     * @return {@link ConnectionPoolBuilder}
     */
    public ConnectionPoolBuilder url(final String url)
    {
        this.url = url;

        return this;
    }

    /**
     * @param validationQuery String
     *
     * @return {@link ConnectionPoolBuilder}
     */
    public ConnectionPoolBuilder validationQuery(final String validationQuery)
    {
        this.validationQuery = validationQuery;

        return this;
    }

    /**
     * Versucht aus dem Driver die Datenbank zu ermitteln und die passende ValidationQuery zu liefern.
     *
     * @param driver String
     *
     * @return String
     */
    protected String determineValidationQuery(final String driver)
    {
        if (driver == null)
        {
            return null;
        }

        String query = null;

        if (driver.contains(".hsqldb."))
        {
            query = "SELECT 1 FROM INFORMATION_SCHEMA.SYSTEM_USERS";
        }
        else if (driver.contains(".derby."))
        {
            query = "VALUES 1";
            // query = "VALUES 1 or SELECT 1 FROM SYSIBM.SYSDUMMY1";
        }
        else if (driver.contains(".oracle."))
        {
            query = "select 1 from dual";
        }
        else if (driver.contains(".db2."))
        {
            query = "select 1 from sysibm.sysdummy1";
        }
        else if (driver.contains(".firebird."))
        {
            query = "select 1 from rdb$database";
        }
        else
        {
            query = "select 1";
        }

        return query;
    }

    /**
     * @return String
     */
    protected String getDriver()
    {
        return this.driver;
    }

    /**
     * @return String
     */
    protected String getUrl()
    {
        return this.url;
    }

    /**
     * @return String
     */
    protected String getValidationQuery()
    {
        return this.validationQuery;
    }

    /**
     * Automatische Ermittlung der ValidationQuery für die Datenbank, wenn keine gesetzt wurde.
     *
     * @return boolean
     */
    protected boolean isDetermineValidationQuery()
    {
        return this.determineValidationQuery;
    }

    /**
     * @return boolean
     */
    protected boolean isPoolPreparedStatements()
    {
        return this.poolPreparedStatements;
    }
}
