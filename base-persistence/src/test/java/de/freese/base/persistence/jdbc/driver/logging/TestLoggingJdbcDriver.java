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
    private static final String DRIVER = "org.h2.Driver";

    private static final List<ConnectionPool> POOLS = new ArrayList<>();

    private static final String URL = "jdbc:logger:jdbc:h2:mem:" + DbServerExtension.createDbName();

    /**
     * @author Thomas Freese
     */
    private interface ConnectionPool
    {
        void close() throws SQLException;

        Connection getConnection() throws SQLException;
    }

    /**
     * @author Thomas Freese
     */
    private static class BasicDataSourceConnectionPool implements ConnectionPool
    {
        private final BasicDataSource dataSource;

        BasicDataSourceConnectionPool()
        {
            super();

            this.dataSource = new BasicDataSource();

            // commons-dbcp2: Creates at first the Driver from the Class-Name, then from the DriverManager.
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
            // Empty
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
        private final HikariDataSource dataSource;

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
        private final SingleConnectionDataSource dataSource;

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
        private final DataSource dataSource;

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

    @BeforeAll
    static void beforeAll() throws Exception
    {
        // Backend-Driver: Is configured by App-Server or Datasource.
        // Class.forName(DRIVER, true, ClassUtils.getDefaultClassLoader());

        // Proxy-Driver, In Web-Apps configured by ServletContextListener or Spring.
        DriverManager.registerDriver(new LoggingJdbcDriver());
        LoggingJdbcDriver.addDefaultLogMethods();

        // Enable Logging.
        // System.setProperty("org.slf4j.simpleLogger.log.de.freese.base.persistence.jdbc.driver.LoggingJdbcDriver", "INFO");

        POOLS.add(new DriverManagerConnectionPool());
        POOLS.add(new SpringSingleConnectionDataSource());
        POOLS.add(new BasicDataSourceConnectionPool());
        POOLS.add(new TomcatConnectionPool());
        POOLS.add(new HikariConnectionPool());
    }

    // /**
    // * Method Annotations:
    // * <code>
    // * <pre>
    // * @ParameterizedTest
    // * @MethodSource("getPools")
    // * </pre>
    // * </code>
    // */
    // static Stream<ConnectionPool> getPools()
    // {
    // return POOLS.stream();
    // }

    void close(final ConnectionPool connectionPool) throws Exception
    {
        connectionPool.close();

        assertTrue(true);
    }

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
