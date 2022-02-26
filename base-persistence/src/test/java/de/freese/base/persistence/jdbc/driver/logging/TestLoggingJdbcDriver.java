// Created: 08.09.2016
package de.freese.base.persistence.jdbc.driver.logging;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.freese.base.persistence.jdbc.DbServerExtension;
import de.freese.base.persistence.jdbc.datasource.ConnectionPoolConfigurer;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestLoggingJdbcDriver
{
    /**
     *
     */
    private static final String DRIVER = "org.h2.Driver";
    /**
     *
     */
    private static final List<ConnectionPool> POOLS = new ArrayList<>();
    /**
     *
     */
    private static final String URL = "jdbc:logger:jdbc:h2:mem:" + DbServerExtension.createDbName();

    /**
     * @author Thomas Freese
     */
    private interface ConnectionPool
    {
        /**
         * @throws SQLException Falls was schief geht.
         */
        void close() throws SQLException;

        /**
         * @return {@link Connection}
         *
         * @throws SQLException Falls was schief geht.
         */
        Connection getConnection() throws SQLException;
    }

    /**
     * @author Thomas Freese
     */
    private static class BasicDataSourceConnectionPool implements ConnectionPool
    {
        /**
         *
         */
        private final BasicDataSource dataSource;

        /**
         * Erstellt ein neues {@link BasicDataSourceConnectionPool} Object.
         */
        BasicDataSourceConnectionPool()
        {
            super();

            this.dataSource = new BasicDataSource();

            // commons-dbcp2: Erzeugt zuerst den Driver aus dem Class-Namen, dann erst aus dem DriverManager.
            ConnectionPoolConfigurer.configureBasic(this.dataSource, LoggingJdbcDriver.class.getName(), URL, "sa", null,
                    null);
        }

        /**
         * @see TestLoggingJdbcDriver.ConnectionPool#close()
         */
        @Override
        public void close() throws SQLException
        {
            this.dataSource.close();
        }

        /**
         * @see TestLoggingJdbcDriver.ConnectionPool#getConnection()
         */
        @Override
        public Connection getConnection() throws SQLException
        {
            return this.dataSource.getConnection();
        }
    }

    /**
     * @author Thomas Freese
     */
    private static class DriverManagerConnectionPool implements ConnectionPool
    {
        /**
         * @see TestLoggingJdbcDriver.ConnectionPool#close()
         */
        @Override
        public void close() throws SQLException
        {
            // NOOP
        }

        /**
         * @see TestLoggingJdbcDriver.ConnectionPool#getConnection()
         */
        @Override
        public Connection getConnection() throws SQLException
        {
            return DriverManager.getConnection(URL, "sa", null);
        }
    }

    /**
     * @author Thomas Freese
     */
    private static class HikariConnectionPool implements ConnectionPool
    {
        /**
         *
         */
        private final HikariDataSource dataSource;

        /**
         * Erstellt ein neues {@link HikariConnectionPool} Object.
         */
        HikariConnectionPool()
        {
            super();

            HikariConfig config = new HikariConfig();

            ConnectionPoolConfigurer.configureHikari(config, LoggingJdbcDriver.class.getName(), URL, "sa", null,
                    null);

            this.dataSource = new HikariDataSource(config);
        }

        /**
         * @see TestLoggingJdbcDriver.ConnectionPool#close()
         */
        @Override
        public void close() throws SQLException
        {
            this.dataSource.close();
        }

        /**
         * @see TestLoggingJdbcDriver.ConnectionPool#getConnection()
         */
        @Override
        public Connection getConnection() throws SQLException
        {
            return this.dataSource.getConnection();
        }
    }

    /**
     * @author Thomas Freese
     */
    private static class SpringSingleConnectionDataSource implements ConnectionPool
    {
        /**
         *
         */
        private final SingleConnectionDataSource dataSource;

        /**
         * Erstellt ein neues {@link SpringSingleConnectionDataSource} Object.
         */
        SpringSingleConnectionDataSource()
        {
            super();

            this.dataSource = new SingleConnectionDataSource();
            this.dataSource.setDriverClassName(DRIVER);
            this.dataSource.setUrl(URL);
            this.dataSource.setUsername("sa");
            this.dataSource.setPassword(null);
            this.dataSource.setSuppressClose(true);
        }

        /**
         * @see TestLoggingJdbcDriver.ConnectionPool#close()
         */
        @Override
        public void close() throws SQLException
        {
            this.dataSource.destroy();
        }

        /**
         * @see TestLoggingJdbcDriver.ConnectionPool#getConnection()
         */
        @Override
        public Connection getConnection() throws SQLException
        {
            return this.dataSource.getConnection();
        }
    }

    /**
     * @author Thomas Freese
     */
    private static class TomcatConnectionPool implements ConnectionPool
    {
        /**
         *
         */
        private final DataSource dataSource;

        /**
         * Erstellt ein neues {@link TomcatConnectionPool} Object.
         */
        TomcatConnectionPool()
        {
            super();

            PoolProperties poolProperties = new PoolProperties();

            ConnectionPoolConfigurer.configureTomcat(poolProperties, LoggingJdbcDriver.class.getName(), URL, "sa", null,
                    null);

            this.dataSource = new DataSource(poolProperties);
        }

        /**
         * @see TestLoggingJdbcDriver.ConnectionPool#close()
         */
        @Override
        public void close() throws SQLException
        {
            this.dataSource.close();
        }

        /**
         * @see TestLoggingJdbcDriver.ConnectionPool#getConnection()
         */
        @Override
        public Connection getConnection() throws SQLException
        {
            return this.dataSource.getConnection();
        }
    }

    /**
     *
     */
    @AfterAll
    static void afterAll()
    {
        for (ConnectionPool pool : POOLS)
        {
            try
            {
                pool.close();
            }
            catch (Exception ex)
            {
                // Ignore
            }
        }
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @BeforeAll
    static void beforeAll() throws Exception
    {
        // Backend-Treiber: Wird durch App-Server oder Datasource erledigt.
        // Class.forName(DRIVER, true, ClassUtils.getDefaultClassLoader());

        // Proxy-Treiber, bei Web-Anwendungen durch ServletContextListener erledigen oder durch Spring.
        DriverManager.registerDriver(new LoggingJdbcDriver());
        LoggingJdbcDriver.addDefaultLogMethods();

        // Logging einschalten.
        // System.setProperty("org.slf4j.simpleLogger.log.de.freese.base.persistence.jdbc.driver.LoggingJdbcDriver", "INFO");

        POOLS.add(new DriverManagerConnectionPool());
        POOLS.add(new SpringSingleConnectionDataSource());
        POOLS.add(new BasicDataSourceConnectionPool());
        POOLS.add(new TomcatConnectionPool());
        POOLS.add(new HikariConnectionPool());
    }

    // /**
    // * Methoden Annotations:
    // * <code>
    // * <pre>
    // * @ParameterizedTest
    // * @MethodSource("getPools")
    // * </pre>
    // * </code>
    // *
    // * @return {@link Stream}
    // */
    // static Stream<ConnectionPool> getPools()
    // {
    // return POOLS.stream();
    // }

    /**
     * @param connectionPool {@link ConnectionPool}
     *
     * @throws Exception Falls was schief geht.
     */
    void close(final ConnectionPool connectionPool) throws Exception
    {
        connectionPool.close();

        assertTrue(true);
    }

    /**
     * @param connectionPool {@link ConnectionPool}
     *
     * @throws Exception Falls was schief geht.
     */
    void driver(final ConnectionPool connectionPool) throws Exception
    {
        int i = 0;

        // "jdbc:logger:jdbc:generic:file:/home/tommy/db/generic/generic;create=false;shutdown=true"
        try (Connection connection = connectionPool.getConnection())
        {
            try (PreparedStatement statement = connection.prepareStatement("select * from information_schema.tables where table_name like ?"))
            {
                statement.setString(1, "T%");

                try (ResultSet resultSet = statement.executeQuery())
                {
                    while (resultSet.next())
                    {
                        i++;
                    }
                }

            }
        }

        assertTrue(i > 1);
    }

    /**
     * @return {@link Stream}
     */
    @TestFactory
    Stream<DynamicNode> testConnectionPools()
    {
        // @formatter:off
        return POOLS.stream()
                .map(cp -> dynamicContainer(cp.getClass().getSimpleName(),
                        Stream.of(
                                dynamicTest("Test Driver", () -> driver(cp)),
                                dynamicTest("Close Pool", () -> close(cp))
//                                dynamicContainer("Close",
//                                        Stream.of(dynamicTest("Close Pool", () -> test020Close(cp))
//                                        )
//                                )
                        )
                    )
                )
                ;
        // @formatter:on
    }
}
