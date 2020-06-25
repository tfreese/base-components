/*
 * To change this license header, choose License Headers in Project Properties. To change this template file, choose Tools | Templates and open the template in
 * the editor.
 */
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
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.freese.base.persistence.jdbc.TestSuiteJdbc;
import de.freese.base.persistence.jdbc.datasource.ConnectionPoolConfigurer;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
class TestLoggingJdbcDriver
{
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
                    "select 1 from INFORMATION_SCHEMA.SYSTEM_USERS");
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
    private static interface ConnectionPool
    {
        /**
         * @throws SQLException Falls was schief geht.
         */
        void close() throws SQLException;

        /**
         * @return {@link Connection}
         * @throws SQLException Falls was schief geht.
         */
        Connection getConnection() throws SQLException;
    }

    /**
     * @author Thomas Freese
     */
    private static class DriverManagerConnectionPool implements ConnectionPool
    {
        /**
         * Erstellt ein neues {@link DriverManagerConnectionPool} Object.
         */
        DriverManagerConnectionPool()
        {
            super();
        }

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
            return DriverManager.getConnection(URL);
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
                    "select 1 from INFORMATION_SCHEMA.SYSTEM_USERS");

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
                    "select 1 from INFORMATION_SCHEMA.SYSTEM_USERS");

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
    private static final String DRIVER = "org.hsqldb.jdbc.JDBCDriver";

    /**
     *
     */
    private static final List<ConnectionPool> POOLS = new ArrayList<>();

    /**
     *
     */
    private static final String URL = "jdbc:logger:jdbc:hsqldb:mem:" + TestSuiteJdbc.ATOMIC_INTEGER.getAndIncrement();

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
     * @return {@link Stream}
     */
    @TestFactory
    Stream<DynamicNode> connectionPools()
    {
        // @formatter:off
        return POOLS.stream()
                .map(cp -> dynamicContainer(cp.getClass().getSimpleName(),
                        Stream.of(
                                dynamicTest("testDriver", () -> test010Driver(cp)),
                                dynamicTest("Close Pool", () -> test020Close(cp))
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

    /**
     * @param connectionPool {@link ConnectionPool}
     * @throws Exception Falls was schief geht.
     */
    void test010Driver(final ConnectionPool connectionPool) throws Exception
    {
        // "jdbc:logger:jdbc:generic:file:/home/tommy/db/generic/generic;create=false;shutdown=true"
        try (Connection connection = connectionPool.getConnection())
        {
            try (PreparedStatement statement = connection.prepareStatement("select * from information_schema.tables where table_name like ?"))
            {
                statement.setString(1, "T%");

                int i = 0;

                try (ResultSet resultSet = statement.executeQuery())
                {
                    while (resultSet.next())
                    {
                        // System.out.println(resultSet.getString("TABLE_NAME"));
                        i++;
                    }
                }

                assertTrue(i > 1);
            }
        }
    }

    /**
     * @param connectionPool {@link ConnectionPool}
     * @throws Exception Falls was schief geht.
     */
    void test020Close(final ConnectionPool connectionPool) throws Exception
    {
        connectionPool.close();

        assertTrue(true);
    }
}
