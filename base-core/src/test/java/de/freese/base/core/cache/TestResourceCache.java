// Created: 22.05.2016
package de.freese.base.core.cache;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestResourceCache
{
    /**
     *
     */
    private static final ResourceCache CACHE_FILE = new FileResourceCache(Paths.get(System.getProperty("java.io.tmpdir"), ".javacache"));
    /**
     *
     */
    private static final ResourceCache CACHE_MEMORY = new MemoryResourceCache();
    /**
     *
     */
    private static final Map<String, byte[]> MAP = new ConcurrentHashMap<>();

    /**
     *
     */
    @AfterAll
    static void afterAll()
    {
        MAP.clear();
        CACHE_FILE.clear();
        CACHE_MEMORY.clear();
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @BeforeAll
    static void beforeAll() throws Exception
    {
        // Empty
    }

    /**
     * Create objects stream.
     *
     * @return {@link Stream}
     *
     * @throws Exception Falls was schief geht.
     */
    static Stream<Arguments> createArgumentes() throws Exception
    {
        URI urlLocalFile = Paths.get("src/test/java/de/freese/base/core/cache/TestResourceCache.java").toUri();
        URI urlHttpImage = URI.create("http://www.freese-home.de/s/img/emotionheader.jpg");

        // @formatter:off
        return Stream.of(
                Arguments.of("FileCache - Local File", CACHE_FILE, urlLocalFile),
                Arguments.of("FileCache - HTTP Image", CACHE_FILE, urlHttpImage),
                Arguments.of("MemoryCache - Local File", CACHE_MEMORY, urlLocalFile),
                Arguments.of("MemoryCache - HTTP Image", CACHE_MEMORY,urlHttpImage)
                );
        // @formatter:on
    }

    /**
     *
     */
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
     * @param resourceCache {@link ResourceCache}
     * @param uri {@link URI}
     *
     * @throws Exception Falls was schief geht.
     */
    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("createArgumentes")
    @Order(1)
    void testInitialLoad(final String name, final ResourceCache resourceCache, final URI uri) throws Exception
    {
        Optional<InputStream> optional = resourceCache.getResource(uri);
        assertNotNull(optional);
        assertTrue(optional.isPresent());

        byte[] bytes = null;

        try (InputStream inputStream = optional.get();
             ByteArrayOutputStream baos = new ByteArrayOutputStream())
        {
            inputStream.transferTo(baos);

            bytes = baos.toByteArray();
        }

        MAP.put(name, bytes);
    }

    /**
     * @param name String
     * @param resourceCache {@link ResourceCache}
     * @param uri {@link URI}
     *
     * @throws Exception Falls was schief geht.
     */
    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("createArgumentes")
    @Order(2)
    void testReload(final String name, final ResourceCache resourceCache, final URI uri) throws Exception
    {
        Optional<InputStream> optional = resourceCache.getResource(uri);
        assertNotNull(optional);
        assertTrue(optional.isPresent());

        byte[] bytes = null;

        try (InputStream inputStream = optional.get();
             ByteArrayOutputStream baos = new ByteArrayOutputStream())
        {
            inputStream.transferTo(baos);

            bytes = baos.toByteArray();
        }

        assertArrayEquals(MAP.get(name), bytes);
    }
}
