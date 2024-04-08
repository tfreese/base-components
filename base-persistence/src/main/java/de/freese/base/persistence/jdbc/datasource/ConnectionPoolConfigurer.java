// Created: 26.01.2018
package de.freese.base.persistence.jdbc.datasource;

import java.time.Duration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.pool.HikariPool;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

/**
 * Konfiguriert die Tomcat {@link PoolProperties} mit vernünftigen Default-Werten.
 *
 * @author Thomas Freese
 */
public final class ConnectionPoolConfigurer {
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
     * @param validationQuery String; optional
     */
    public static void configureBasic(final BasicDataSource basicDataSource, final String driverClassName, final String url, final String userName, final String password,
                                      final String validationQuery) {
        basicDataSource.setDriverClassName(driverClassName);
        basicDataSource.setUrl(url);
        basicDataSource.setUsername(userName);
        basicDataSource.setPassword(password);

        basicDataSource.setMaxTotal(3);
        basicDataSource.setMaxIdle(3);
        basicDataSource.setMinIdle(1);
        basicDataSource.setInitialSize(1);

        // Max. 5 Sekunden warten auf Connection.
        basicDataSource.setMaxWait(Duration.ofSeconds(5));

        basicDataSource.setDefaultAutoCommit(Boolean.FALSE);
        basicDataSource.setDefaultReadOnly(Boolean.FALSE);

        if (validationQuery != null && !validationQuery.isBlank()) {
            basicDataSource.setValidationQuery(validationQuery);

            // Nach 3 Sekunden wird die ValidationQuery als ungültig interpretiert.
            basicDataSource.setValidationQueryTimeout(Duration.ofSeconds(3));

            // Connections prüfen, die IDLE sind.
            basicDataSource.setTestWhileIdle(true);

            // Connections prüfen, die geholt werden.
            basicDataSource.setTestOnBorrow(true);

            // Connections prüfen, die zurückgegeben werden.
            basicDataSource.setTestOnReturn(false);
        }

        // 60 Sekunden: Zeit nach der eine Connection als "Idle" markiert wird.
        basicDataSource.setMinEvictableIdle(Duration.ofSeconds(60));

        // Alle 60 Sekunden auf Idle-Connections prüfen.
        basicDataSource.setDurationBetweenEvictionRuns(Duration.ofSeconds(60));
        basicDataSource.setNumTestsPerEvictionRun(1);

        // Eine Connection darf max. 1 Stunde alt werden, 0 = keine Alterung.
        basicDataSource.setMaxConn(Duration.ofHours(1));

        // Entfernen von verwaisten (Timeout) Connections/Langläufern.
        basicDataSource.setAbandonedUsageTracking(true);
        // basicDataSource.setAbandonedLogWriter(logWriter);

        basicDataSource.setRemoveAbandonedOnMaintenance(true);

        // Nach 10 Minuten Connections/Langläufer als verwaist markieren.
        basicDataSource.setRemoveAbandonedTimeout(Duration.ofMinutes(10));
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
     * @param validationQuery String; optional
     */
    public static void configureHikari(final HikariConfig config, final String driverClassName, final String url, final String userName, final String password,
                                       final String validationQuery) {
        config.setDriverClassName(driverClassName);
        config.setJdbcUrl(url);
        config.setUsername(userName);
        config.setPassword(password);

        config.setMaximumPoolSize(3);
        config.setMinimumIdle(1);

        // Max. 5 Sekunden warten auf Connection.
        config.setConnectionTimeout(Duration.ofSeconds(5).toMillis());

        // 60 Sekunden: Zeit nach der eine Connection als "Idle" markiert wird.
        config.setIdleTimeout(Duration.ofSeconds(60).toMillis());
        config.setMaxLifetime(config.getIdleTimeout() * 3);

        config.setAutoCommit(false);
        config.setReadOnly(false);

        if (validationQuery != null && !validationQuery.isBlank()) {
            config.setConnectionTestQuery(validationQuery);

            // Nach 3 Sekunden wird die ValidationQuery als ungültig interpretiert.
            config.setValidationTimeout(Duration.ofSeconds(3).toMillis());
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
     * @param validationQuery String; optional
     */
    public static void configureTomcat(final PoolProperties poolProperties, final String driverClassName, final String url, final String userName, final String password,
                                       final String validationQuery) {
        poolProperties.setDriverClassName(driverClassName);
        poolProperties.setUrl(url);
        poolProperties.setUsername(userName);
        poolProperties.setPassword(password);

        poolProperties.setMaxActive(3);
        poolProperties.setMaxIdle(3);
        poolProperties.setMinIdle(1);
        poolProperties.setInitialSize(1);

        // Max. 5 Sekunden warten auf Connection.
        poolProperties.setMaxWait((int) Duration.ofSeconds(5).toMillis());

        poolProperties.setDefaultAutoCommit(Boolean.FALSE);
        poolProperties.setDefaultReadOnly(Boolean.FALSE);

        if (validationQuery != null && !validationQuery.isBlank()) {
            poolProperties.setValidationQuery(validationQuery);

            // Nach 3 Sekunden wird die ValidationQuery als ungültig interpretiert.
            poolProperties.setValidationQueryTimeout(3);

            // Wurde eine Connection vor 60 Sekunden validiert, nicht nochmal validieren.
            poolProperties.setValidationInterval(Duration.ofSeconds(60).toMillis());

            // Connections prüfen, die IDLE sind.
            poolProperties.setTestWhileIdle(true);

            // Connections prüfen, die geholt werden.
            poolProperties.setTestOnBorrow(true);

            // Connections prüfen, die zurückgegeben werden.
            poolProperties.setTestOnReturn(false);
        }

        // 60 Sekunden: Zeit nach der eine Connection als "Idle" markiert wird.
        poolProperties.setMinEvictableIdleTimeMillis((int) Duration.ofSeconds(60).toMillis());

        // Alle 60 Sekunden auf Idle-Connections prüfen.
        poolProperties.setTimeBetweenEvictionRunsMillis((int) Duration.ofSeconds(5).toMillis());

        // Eine Connection darf max. 1 Stunde alt werden, 0 = keine Alterung.
        poolProperties.setMaxAge(Duration.ofHours(1).toMillis());

        // Entfernen von verwaisten (Timeout) Connections/Langläufern.
        poolProperties.setRemoveAbandoned(true);
        poolProperties.setLogAbandoned(true);

        // Nach 10 Minuten Connections/Langläufer als verwaist markieren.
        poolProperties.setRemoveAbandonedTimeout((int) Duration.ofMinutes(10).toSeconds());

        // Entfernen von verwaisten (Timeout) Connections/Langläufer erst ab x% des Poolstandes.
        poolProperties.setAbandonWhenPercentageFull(90);

        // Caching für die Attribute autoCommit, readOnly, transactionIsolation und catalog.
        String jdbcInterceptors = "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;";

        // Jede Query bei Langläufern setzt den Abandon-Timer zurück.
        jdbcInterceptors += "org.apache.tomcat.jdbc.pool.interceptor.ResetAbandonedTimer;";

        // Schliesst alle Statements die durch createStatement, prepareStatement oder prepareCall erzeugt wurden, falls nötig.
        jdbcInterceptors += "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer";

        poolProperties.setJdbcInterceptors(jdbcInterceptors);
    }

    private ConnectionPoolConfigurer() {
        super();
    }
}
