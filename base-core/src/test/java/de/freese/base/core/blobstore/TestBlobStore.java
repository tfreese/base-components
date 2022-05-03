package de.freese.base.core.blobstore;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.freese.base.core.blobstore.datasource.DatasourceBlobStore;
import de.freese.base.core.blobstore.file.FileBlobStore;
import de.freese.base.core.blobstore.memory.MemoryBlobStore;
import de.freese.base.core.io.AbstractIoTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.DisposableBean;

/**
 * @author Thomas Freese
 */
class TestBlobStore
{
    /**
     *
     */
    private static final Path PATH_TEST = Paths.get(System.getProperty("java.io.tmpdir"), "blobStore");
    /**
     *
     */
    private static DataSource dataSourceDerby;
    /**
     *
     */
    private static DataSource dataSourceH2;
    /**
     *
     */
    private static DataSource dataSourceHsqldb;

    /**
     * @throws Exception Falls was schiefgeht
     */
    @AfterAll
    static void afterAll() throws Exception
    {
        AbstractIoTest.deleteDirectoryRecursive(PATH_TEST);

        for (DataSource dataSource : List.of(dataSourceH2, dataSourceHsqldb, dataSourceDerby))
        {
            if (dataSource instanceof AutoCloseable ac)
            {
                ac.close();
            }
            else if (dataSource instanceof Closeable c)
            {
                c.close();
            }
            else if (dataSource instanceof DisposableBean db)
            {
                db.destroy();
            }
        }
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @BeforeAll
    static void beforeAll() throws Exception
    {
        // JUL-Logger auf slf4j umleiten.
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        BiConsumer<String, HikariConfig> hikariConfigurer = (poolName, config) ->
        {
            config.setUsername("sa");
            config.setPassword("");
            config.setMinimumIdle(1);
            config.setMaximumPoolSize(3);
            config.setPoolName(poolName);
            config.setAutoCommit(true);
        };

        // org/springframework/boot/jdbc/DatabaseDriver.java

        // H2
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.h2.Driver");
        config.setJdbcUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        hikariConfigurer.accept("h2", config);
        dataSourceH2 = new HikariDataSource(config);

        // Hsqldb
        config = new HikariConfig();
        config.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
        config.setJdbcUrl("jdbc:hsqldb:mem:test;;shutdown=true");
        hikariConfigurer.accept("hsqldb", config);
        dataSourceHsqldb = new HikariDataSource(config);

        // Derby
        config = new HikariConfig();
        config.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
        config.setJdbcUrl("jdbc:derby:memory:test;create=true");
        hikariConfigurer.accept("derby", config);
        dataSourceDerby = new HikariDataSource(config);
    }

    /**
     * @return {@link Stream}
     *
     * @throws Exception Falls was schief geht.
     */
    static Stream<Arguments> createArgumentes() throws Exception
    {
        // @formatter:off
        return Stream.of(
                Arguments.of("Memory", new MemoryBlobStore()),
                Arguments.of("File", new FileBlobStore(PATH_TEST)),
                Arguments.of("DataSource-H2", new DatasourceBlobStore(dataSourceH2)),
                Arguments.of("DataSource-HSQLDB", new DatasourceBlobStore(dataSourceHsqldb)),
                Arguments.of("DataSource-Derby", new DatasourceBlobStore(dataSourceDerby))
        );
        // @formatter:on
    }

    @AfterEach
    void afterEach()
    {
        // Empty
    }

    /**
     *
     */
    @BeforeEach
    void beforeEach()
    {
        // Empty
    }

    /**
     * @param name String
     * @param blobStore {@link BlobStore}
     *
     * @throws Exception Falls was schief geht.
     */
    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("createArgumentes")
    @Order(1)
    void testBlobStore(final String name, final BlobStore blobStore) throws Exception
    {
        if (blobStore instanceof DatasourceBlobStore dsBs)
        {
            dsBs.createDatabaseIfNotExist();
        }

        Path path = Paths.get("pom.xml");
        long fileSize = Files.size(path);
        byte[] bytes = Files.readAllBytes(path);

        URI uri = path.toUri();
        BlobId blobId = new BlobId(uri);

        assertFalse(blobStore.exists(blobId));

        try (InputStream inputStream = Files.newInputStream(path);
             OutputStream outputStream = blobStore.create(blobId))
        {
            inputStream.transferTo(outputStream);
        }

        assertTrue(blobStore.exists(blobId));

        Blob blob = blobStore.get(blobId);
        assertNotNull(blob);
        assertEquals(fileSize, blob.getLength());
        assertEquals(uri, blob.getId().getUri());
        assertArrayEquals(bytes, blob.getAllBytes());

        blobStore.delete(blobId);
        assertFalse(blobStore.exists(blobId));

        try (InputStream inputStream = Files.newInputStream(path))
        {
            blobStore.create(blobId, inputStream);
        }

        blob = blobStore.get(blobId);
        assertNotNull(blob);
        assertEquals(fileSize, blob.getLength());
        assertEquals(uri, blob.getId().getUri());
        assertArrayEquals(bytes, blob.getAllBytes());

        blobStore.delete(blobId);
    }
}
