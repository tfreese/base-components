package de.freese.base.core.blobstore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import javax.sql.DataSource;

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

        //        for (DataSource dataSource : List.of(dataSourceH2, dataSourceHsqldb))
        //        {
        //            if (dataSource instanceof JdbcConnectionPool p)
        //            {
        //                p.dispose();
        //            }
        //            else if (dataSource instanceof JDBCPool p)
        //            {
        //                p.close(1);
        //            }
        //            else if (dataSource instanceof AutoCloseable ac)
        //            {
        //                ac.close();
        //            }
        //            else if (dataSource instanceof Closeable c)
        //            {
        //                c.close();
        //            }
        //            else if (dataSource instanceof DisposableBean db)
        //            {
        //                db.destroy();
        //            }
        //        }
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

        //        // H2
        //        JdbcConnectionPool poolH2 = JdbcConnectionPool.create("jdbc:h2:file:" + dbPath.resolve("h2"), "sa", "sa");
        //        poolH2.setMaxConnections(3);
        //        dataSourceH2 = poolH2;
        //
        //        // Hsqldb
        //        JDBCPool poolHsqldb = new JDBCPool(3);
        //        poolHsqldb.setUrl("jdbc:hsqldb:file:" + dbPath.resolve("hsqldb"));
        //        poolHsqldb.setUser("sa");
        //        poolHsqldb.setPassword("sa");
        //        dataSourceHsqldb = poolH2;
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
                Arguments.of("File", new FileBlobStore(PATH_TEST))
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
        Path path = Paths.get("pom.xml");
        long fileSize = Files.size(path);

        URI uri = path.toUri();
        BlobId blobId = new BlobId(uri);

        assertFalse(blobStore.exists(blobId));

        try (InputStream inputStream = Files.newInputStream(path))
        {
            blobStore.create(blobId, inputStream::transferTo);
        }

        assertTrue(blobStore.exists(blobId));

        Blob blob = blobStore.get(blobId);
        assertNotNull(blob);
        assertEquals(fileSize, blob.getLength());
        assertEquals(uri, blob.getId().getUri());

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

        blobStore.delete(blobId);
    }
}
