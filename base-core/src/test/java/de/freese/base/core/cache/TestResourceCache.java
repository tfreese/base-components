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
import org.junit.jupiter.api.Order;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * @author Thomas Freese
 */
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestResourceCache
{
    /**
     *
     */
    private static final ResourceCache CACHE_CAFFEINE = new CaffeineResourceCache(Paths.get(System.getProperty("java.io.tmpdir"), ".javacache2"), 6000);
    /**
     *
     */
    private static final ResourceCache CACHE_FILE = new FileResourceCache(Paths.get(System.getProperty("java.io.tmpdir"), ".javacache1"));
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
        CACHE_CAFFEINE.clear();
    }

    /**
     * @throws Exception Falls was schiefgeht.
     */
    @BeforeAll
    static void beforeAll() throws Exception
    {
        // JUL-Logger ausschalten.
        // LogManager.getLogManager().reset();

        // JUL-Logger auf slf4j umleiten.
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    /**
     * Create objects stream.
     *
     * @return {@link Stream}
     *
     * @throws Exception Falls was schiefgeht.
     */
    static Stream<Arguments> createArgumentes() throws Exception
    {
        URI urlLocalFile = Paths.get("src/test/java/de/freese/base/core/cache/TestResourceCache.java").toUri();
        URI urlHttpImage = URI.create("http://avatars.githubusercontent.com/u/1973918?v=4"); // Redirect -> https

        // @formatter:off
        return Stream.of(
                Arguments.of("FileCache - Local File", CACHE_FILE, urlLocalFile),
                Arguments.of("FileCache - HTTP Image", CACHE_FILE, urlHttpImage),
                Arguments.of("MemoryCache - Local File", CACHE_MEMORY, urlLocalFile),
                Arguments.of("MemoryCache - HTTP Image", CACHE_MEMORY,urlHttpImage),
                Arguments.of("CaffeineCache - Local File", CACHE_CAFFEINE, urlLocalFile),
                Arguments.of("CaffeineCache - HTTP Image", CACHE_CAFFEINE,urlHttpImage)
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
     * @throws Exception Falls was schiefgeht.
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
     * @throws Exception Falls was schiefgeht.
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
