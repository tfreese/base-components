/**
 * Created: 01.02.2014
 */

package de.freese.base.pool;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class TestConnectionPoolBuilder
{
    /**
     *
     */
    private static final List<Arguments> DATA_SOURCES = new ArrayList<>();

    /**
     *
     */
    @BeforeAll
    static void beforeAll()
    {
        DATA_SOURCES.add(Arguments.of("Derby-DBCP", "derby", ConnectionPoolBuilder.create().driver("org.apache.derby.jdbc.EmbeddedDriver")
                .url("jdbc:derby:memory:test;create=true").registerShutdownHook(true).buildDBCP("sa", "")));

        DATA_SOURCES.add(Arguments.of("Derby-Tomcat", "derby", ConnectionPoolBuilder.create().driver("org.apache.derby.jdbc.EmbeddedDriver")
                .url("jdbc:derby:memory:test;create=true").registerShutdownHook(true).buildTomcat("sa", "")));

        DATA_SOURCES.add(Arguments.of("Derby-Hikari", "derby", ConnectionPoolBuilder.create().driver("org.apache.derby.jdbc.EmbeddedDriver")
                .url("jdbc:derby:memory:test;create=true").registerShutdownHook(true).buildHikari("sa", "")));

        DATA_SOURCES.add(Arguments.of("H2-DBCP", "h2", ConnectionPoolBuilder.create().driver("org.h2.Driver").url("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
                .registerShutdownHook(true).buildDBCP("sa", "")));

        DATA_SOURCES.add(Arguments.of("H2-Tomcat", "h2", ConnectionPoolBuilder.create().driver("org.h2.Driver").url("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
                .registerShutdownHook(true).buildTomcat("sa", "")));

        DATA_SOURCES.add(Arguments.of("H2-Hikari", "h2", ConnectionPoolBuilder.create().driver("org.h2.Driver").url("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
                .registerShutdownHook(true).buildHikari("sa", "")));

        DATA_SOURCES.add(Arguments.of("HSQLDB-DBCP", "hsql",
                ConnectionPoolBuilder.create().driver("org.hsqldb.jdbc.JDBCDriver").url("jdbc:hsqldb:mem:test").registerShutdownHook(true).buildDBCP("sa", "")));

        DATA_SOURCES.add(Arguments.of("HSQLDB-Tomcat", "hsql",
                ConnectionPoolBuilder.create().driver("org.hsqldb.jdbc.JDBCDriver").url("jdbc:hsqldb:mem:test").registerShutdownHook(true).buildTomcat("sa", "")));

        DATA_SOURCES.add(Arguments.of("HSQLDB-Hikari", "hsql",
                ConnectionPoolBuilder.create().driver("org.hsqldb.jdbc.JDBCDriver").url("jdbc:hsqldb:mem:test").registerShutdownHook(true).buildHikari("sa", "")));
    }

    /**
     * @return {@link Stream}
     */
    static Stream<Arguments> getConnectionPools()
    {
        return DATA_SOURCES.stream();
    }

    /**
     * Erstellt ein neues {@link TestConnectionPoolBuilder} Object.
     */
    public TestConnectionPoolBuilder()
    {
        super();
    }

    /**
     * @param name   String
     * @param dbName String
     * @param pool   {@link DataSource}
     *
     * @throws SQLException Falls was schief geht.
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("getConnectionPools")
    public void testPool(final String name, final String dbName, final DataSource pool) throws SQLException
    {
        DatabaseMetaData metaData = null;

        try (Connection connection = pool.getConnection())
        {
            metaData = connection.getMetaData();
            assertTrue(metaData.getDatabaseProductName().toLowerCase().contains(dbName));
        }

        assertNotNull(metaData);
    }
}
