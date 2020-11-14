/**
 * Created: 01.02.2014
 */

package de.freese.base.pool;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import de.freese.base.pool.ConnectionPoolBuilder.ConnectionPoolType;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestConnectionPoolBuilder
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
        for (ConnectionPoolType type : List.of(ConnectionPoolType.HIKARI, ConnectionPoolType.TOMCAT, ConnectionPoolType.COMMONS_DBCP))
        {
            DATA_SOURCES.add(Arguments.of("Derby-" + type, "derby", new ConnectionPoolBuilder().type(type).user("sa").password("").registerShutdownHook(true)
                    .driver("org.apache.derby.jdbc.EmbeddedDriver").url("jdbc:derby:memory:test;create=true").build()));

            DATA_SOURCES.add(Arguments.of("H2-" + type, "h2", new ConnectionPoolBuilder().type(type).user("sa").password("").registerShutdownHook(true)
                    .driver("org.h2.Driver").url("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1").build()));

            DATA_SOURCES.add(Arguments.of("HSQLDB-" + type, "hsql", new ConnectionPoolBuilder().type(type).user("sa").password("").registerShutdownHook(true)
                    .driver("org.hsqldb.jdbc.JDBCDriver").url("jdbc:hsqldb:mem:test").build()));
        }
    }

    /**
     * @return {@link Stream}
     */
    static Stream<Arguments> getConnectionPools()
    {
        return DATA_SOURCES.stream();
    }

    /**
     * @param name String
     * @param dbName String
     * @param pool {@link DataSource}
     * @throws SQLException Falls was schief geht.
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("getConnectionPools")
    void testPool(final String name, final String dbName, final DataSource pool) throws SQLException
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
