// Created: 22.05.2016
package de.freese.base.core.cache;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Paths;
import java.util.Map;
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
class TestResourceCache {
    private static final ResourceCache CACHE_CAFFEINE = new CaffeineResourceCache(Paths.get(System.getProperty("java.io.tmpdir"), ".javaCache2"), 6000);
    private static final ResourceCache CACHE_FILE = new FileResourceCache(Paths.get(System.getProperty("java.io.tmpdir"), ".javaCache1"));
    private static final ResourceCache CACHE_MEMORY = new MemoryResourceCache();
    private static final Map<String, byte[]> MAP = new ConcurrentHashMap<>();

    @AfterAll
    static void afterAll() {
        MAP.clear();
        CACHE_FILE.clear();
        CACHE_MEMORY.clear();
        CACHE_CAFFEINE.clear();
    }

    @BeforeAll
    static void beforeAll() throws Exception {
        // JUL-Logger ausschalten.
        // LogManager.getLogManager().reset();

        // JUL-Logger auf slf4j umleiten.
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    static Stream<Arguments> createArgumentes() throws Exception {
        final URI urlLocalFile = Paths.get("pom.xml").toUri();
        final URI urlHttpImage = URI.create("http://avatars.githubusercontent.com/u/1973918?v=4"); // Redirect -> https

        return Stream.of(
                Arguments.of("FileCache - Local File", CACHE_FILE, urlLocalFile),
                Arguments.of("FileCache - HTTP Image", CACHE_FILE, urlHttpImage),
                Arguments.of("MemoryCache - Local File", CACHE_MEMORY, urlLocalFile),
                Arguments.of("MemoryCache - HTTP Image", CACHE_MEMORY, urlHttpImage),
                Arguments.of("CaffeineCache - Local File", CACHE_CAFFEINE, urlLocalFile),
                Arguments.of("CaffeineCache - HTTP Image", CACHE_CAFFEINE, urlHttpImage)
        );
    }

    @AfterEach
    void afterEach() {
        // Empty
    }

    @BeforeEach
    void beforeEach() {
        // Empty
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("createArgumentes")
    @Order(1)
    void testInitialLoad(final String name, final ResourceCache resourceCache, final URI uri) throws Exception {
        final InputStream inputStream = resourceCache.getResource(uri);
        assertNotNull(inputStream);

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        inputStream.transferTo(baos);

        baos.flush();

        final byte[] bytes = baos.toByteArray();

        MAP.put(name, bytes);
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("createArgumentes")
    @Order(2)
    void testReload(final String name, final ResourceCache resourceCache, final URI uri) throws Exception {
        final InputStream inputStream = resourceCache.getResource(uri);
        assertNotNull(inputStream);

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        inputStream.transferTo(baos);

        baos.flush();

        final byte[] bytes = baos.toByteArray();

        assertArrayEquals(MAP.get(name), bytes);
    }
}
