// Created: 26.01.2018
package de.freese.base.persistence.jdbc.datasource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.pool.HikariPool;

/**
 * Konfiguriert die Tomcat {@link PoolProperties} mit vernünftigen Default-Werten.
 *
 * @author Thomas Freese
 */
public final class ConnectionPoolConfigurer
{
    /**
     * Konfiguriert die {@link BasicDataSource} mit vernünftigen Default-Werten.<br>
     * Defaults:
     *
     * <pre>
     * maxTotal = 3
     * maxIdle = 3
     * minIdle = 1
     * initialSize = 1
     * </pre>
     *
     * @param basicDataSource {@link BasicDataSource}
     * @param driverClassName String
     * @param url String
     * @param userName String
     * @param password String
     * @param validationQuery String; optional
     */
    public static void configureBasic(final BasicDataSource basicDataSource, final String driverClassName, final String url, final String userName,
                                      final String password, final String validationQuery)
    {
        basicDataSource.setDriverClassName(driverClassName);
        basicDataSource.setUrl(url);
        basicDataSource.setUsername(userName);
        basicDataSource.setPassword(password);

        basicDataSource.setMaxTotal(3);
        basicDataSource.setMaxIdle(3);
        basicDataSource.setMinIdle(1);
        basicDataSource.setInitialSize(1);

        // Max. 5 Sekunden warten auf Connection.
        basicDataSource.setMaxWaitMillis(5 * 1000L);

        basicDataSource.setDefaultAutoCommit(Boolean.FALSE);
        basicDataSource.setDefaultReadOnly(Boolean.FALSE);

        if ((validationQuery != null) && !validationQuery.isBlank())
        {
            basicDataSource.setValidationQuery(validationQuery);

            // Nach 3 Sekunden wird die ValidationQuery als ungültig interpretiert.
            basicDataSource.setValidationQueryTimeout(3);

            // Connections prüfen, die IDLE sind.
            basicDataSource.setTestWhileIdle(true);

            // Connections prüfen, die geholt werden.
            basicDataSource.setTestOnBorrow(true);

            // Connections prüfen, die zurückgegeben werden.
            basicDataSource.setTestOnReturn(false);
        }

        // 60 Sekunden: Zeit nach der eine Connection als "Idle" markiert wird.
        basicDataSource.setMinEvictableIdleTimeMillis(60 * 1000L);

        // Alle 60 Sekunden auf Idle-Connections prüfen.
        basicDataSource.setTimeBetweenEvictionRunsMillis(60 * 1000L);
        basicDataSource.setNumTestsPerEvictionRun(1);

        // Eine Connection darf max. 1 Stunde alt werden, 0 = keine Alterung.
        basicDataSource.setMaxConnLifetimeMillis(1 * 60 * 60 * 1000L);

        // Entfernen von verwaisten (Timeout) Connections/Langläufern.
        basicDataSource.setAbandonedUsageTracking(true);
        // basicDataSource.setAbandonedLogWriter(logWriter);

        basicDataSource.setRemoveAbandonedOnMaintenance(true);

        // Nach 10 Minuten Connections/Langläufer als verwaist markieren.
        basicDataSource.setRemoveAbandonedTimeout(10 * 60);
    }

    /**
     * Konfiguriert die {@link HikariPool} mit vernünftigen Default-Werten.<br>
     * Defaults:
     *
     * <pre>
     * maximumPoolSize = 3
     * minimumIdle = 1
     * </pre>
     *
     * @param config {@link HikariConfig}
     * @param driverClassName String
     * @param url String
     * @param userName String
     * @param password String
     * @param validationQuery String; optional
     */
    public static void configureHikari(final HikariConfig config, final String driverClassName, final String url, final String userName, final String password,
                                       final String validationQuery)
    {
        config.setDriverClassName(driverClassName);
        config.setJdbcUrl(url);
        config.setUsername(userName);
        config.setPassword(password);

        config.setMaximumPoolSize(3);
        config.setMinimumIdle(1);

        // Max. 5 Sekunden warten auf Connection.
        config.setConnectionTimeout(5 * 1000L);

        // 60 Sekunden: Zeit nach der eine Connection als "Idle" markiert wird.
        config.setIdleTimeout(60 * 1000L);
        config.setMaxLifetime(config.getIdleTimeout() * 3);

        config.setAutoCommit(false);
        config.setReadOnly(false);

        if ((validationQuery != null) && !validationQuery.isBlank())
        {
            config.setConnectionTestQuery(validationQuery);

            // Nach 3 Sekunden wird die ValidationQuery als ungültig interpretiert.
            config.setValidationTimeout(3 * 1000L);
        }

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

    /**
     * Konfiguriert die Tomcat {@link PoolProperties} mit vernünftigen Default-Werten.<br>
     * Defaults:
     *
     * <pre>
     * maxActive = 3
     * maxIdle = 3
     * minIdle = 1
     * initialSize = 1
     * </pre>
     *
     * @param poolProperties {@link PoolProperties}
     * @param driverClassName String
     * @param url String
     * @param userName String
     * @param password String
     * @param validationQuery String; optional
     */
    public static void configureTomcat(final PoolProperties poolProperties, final String driverClassName, final String url, final String userName,
                                       final String password, final String validationQuery)
    {
        poolProperties.setDriverClassName(driverClassName);
        poolProperties.setUrl(url);
        poolProperties.setUsername(userName);
        poolProperties.setPassword(password);

        poolProperties.setMaxActive(3);
        poolProperties.setMaxIdle(3);
        poolProperties.setMinIdle(1);
        poolProperties.setInitialSize(1);

        // Max. 5 Sekunden warten auf Connection.
        poolProperties.setMaxWait(5 * 1000);

        poolProperties.setDefaultAutoCommit(Boolean.FALSE);
        poolProperties.setDefaultReadOnly(Boolean.FALSE);

        if ((validationQuery != null) && !validationQuery.isBlank())
        {
            poolProperties.setValidationQuery(validationQuery);

            // Nach 3 Sekunden wird die ValidationQuery als ungültig interpretiert.
            poolProperties.setValidationQueryTimeout(3);

            // Wurde eine Connection vor 60 Sekunden validiert, nicht nochmal validieren.
            poolProperties.setValidationInterval(60 * 1000L);

            // Connections prüfen, die IDLE sind.
            poolProperties.setTestWhileIdle(true);

            // Connections prüfen, die geholt werden.
            poolProperties.setTestOnBorrow(true);

            // Connections prüfen, die zurückgegeben werden.
            poolProperties.setTestOnReturn(false);
        }

        // 60 Sekunden: Zeit nach der eine Connection als "Idle" markiert wird.
        poolProperties.setMinEvictableIdleTimeMillis(60 * 1000);

        // Alle 60 Sekunden auf Idle-Connections prüfen.
        poolProperties.setTimeBetweenEvictionRunsMillis(60 * 1000);

        // Eine Connection darf max. 1 Stunde alt werden, 0 = keine Alterung.
        poolProperties.setMaxAge(1 * 60 * 60 * 1000L);

        // Entfernen von verwaisten (Timeout) Connections/Langläufern.
        poolProperties.setRemoveAbandoned(true);
        poolProperties.setLogAbandoned(true);

        // Nach 10 Minuten Connections/Langläufer als verwaist markieren.
        poolProperties.setRemoveAbandonedTimeout(10 * 60);

        // Entfernen von verwaisten (Timeout) Connections/Langläufer erst ab x% des Poolstands.
        poolProperties.setAbandonWhenPercentageFull(90);

        // Caching für die Attribute autoCommit, readOnly, transactionIsolation und catalog.
        String jdbcInterceptors = "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;";

        // Jede Query bei Langläufern setzt den Abandon-Timer zurück.
        jdbcInterceptors += "org.apache.tomcat.jdbc.pool.interceptor.ResetAbandonedTimer;";

        // Schliesst alle Statments die durch createStatement, prepareStatement oder prepareCall erzeugt wurden, falls nötig.
        jdbcInterceptors += "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer";

        poolProperties.setJdbcInterceptors(jdbcInterceptors);
    }

    /**
     * Erzeugt eine neue Instanz von {@link ConnectionPoolConfigurer}.
     */
    private ConnectionPoolConfigurer()
    {
        super();
    }
}
