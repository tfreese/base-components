/**
 * Created: 22.05.2016
 */
package de.freese.base.core.cache;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Optional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class TestResourceCache
{
    /**
     *
     */
    private static ResourceCache CACHE = null;

    /**
     *
     */
    private static URL URL_FILE = null;

    /**
     *
     */
    private static URL URL_IMAGE = null;

    /**
     *
     */
    @AfterAll
    public static void afterAll()
    {
        CACHE.clear();
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @BeforeAll
    public static void beforeAll() throws Exception
    {
        // CACHE = new FileCache();
        CACHE = new MemoryResourceCache();
        URL_FILE = Paths.get("src/test/java/de/freese/base/core/cache/TestResourceCache.java").toUri().toURL();
        URL_IMAGE = new URL("http://www.freese-home.de/s/img/emotionheader.jpg");
    }

    /**
     * Erstellt ein neues {@link TestResourceCache} Object.
     */
    public TestResourceCache()
    {
        super();
    }

    /**
     *
     */
    @AfterEach
    public void afterEach()
    {
        // Empty
    }

    /**
     *
     */
    @BeforeEach
    public void beforeEach()
    {
        // Empty
    }

    /**
     *
     */
    @SuppressWarnings("resource")
    @Test
    public void test0010FileCache()
    {
        Optional<InputStream> optional = CACHE.getResource(URL_FILE);
        assertNotNull(optional);
        assertNotNull(optional.get());

        // Reload
        optional = CACHE.getResource(URL_FILE);
        assertNotNull(optional);
        assertNotNull(optional.get());
    }

    /**
     *
     */
    @SuppressWarnings("resource")
    @Test
    public void test0020URLCache()
    {
        Optional<InputStream> optional = CACHE.getResource(URL_IMAGE);
        assertNotNull(optional);
        assertNotNull(optional.get());

        // Reload
        optional = CACHE.getResource(URL_IMAGE);
        assertNotNull(optional);
        assertNotNull(optional.get());
    }
}
