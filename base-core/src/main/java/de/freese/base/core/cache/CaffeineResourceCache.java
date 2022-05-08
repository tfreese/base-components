package de.freese.base.core.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.util.Optional;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.Weigher;

/**
 * @author Thomas Freese
 */
public class CaffeineResourceCache extends FileResourceCache
{
    /**
     *
     */
    private final LoadingCache<URI, byte[]> cache;

    /**
     * @param cacheDirectory {@link Path}
     * @param keepBytesInMemory int; Disable Caching = 0
     */
    public CaffeineResourceCache(final Path cacheDirectory, final int keepBytesInMemory)
    {
        super(cacheDirectory);

        this.cache = createCache(keepBytesInMemory);
    }

    @Override
    public void clear()
    {
        this.cache.invalidateAll();
        this.cache.cleanUp();

        super.clear();
    }

    @Override
    public Optional<InputStream> getResource(final URI uri)
    {
        byte[] content = this.cache.get(uri);

        if ((content == null) || (content.length == 0))
        {
            return Optional.empty();
        }

        return Optional.of(new ByteArrayInputStream(content));
    }

    /**
     * @param keepBytesInMemory int; Disable Caching = 0
     *
     * @return LoadingCache
     */
    private LoadingCache<URI, byte[]> createCache(final int keepBytesInMemory)
    {
        // Größe der Datei = Gewicht
        Weigher<URI, byte[]> weigher = (key, value) -> value.length;

        CacheLoader<URI, byte[]> cacheLoader = key ->
        {
            byte[] content = {};

            Optional<InputStream> optional = super.getResource(key);

            if (optional.isPresent())
            {
                // int size = (int) getContentLength(key);
                int size = 1024;

                try (InputStream inputStream = optional.get();
                     ByteArrayOutputStream baos = new ByteArrayOutputStream(size))
                {
                    inputStream.transferTo(baos);

                    baos.flush();
                    content = baos.toByteArray();
                }
            }

            return content;
        };

        // @formatter:off
        return Caffeine.newBuilder()
                .maximumWeight(keepBytesInMemory)
                .weigher(weigher)
                .evictionListener((key,  value,  cause) -> getLogger().info("Eviction: {} - {} - {}kB",  cause, key, value.length / 1024))
                .removalListener((key,  value,  cause) -> getLogger().info("Removal: {} - {} - {}kB",  cause, key,value.length / 1024))
                .build(cacheLoader)
                ;
        // @formatter:o
    }
}
