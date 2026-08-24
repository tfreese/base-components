package de.freese.base.core.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.util.Objects;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.Weigher;

/**
 * @author Thomas Freese
 */
public class CaffeineResourceCache extends FileResourceCache {
    private final LoadingCache<URI, byte[]> cache;

    /**
     * @param keepBytesInMemory int; Disable Caching = 0
     */
    public CaffeineResourceCache(final Path cacheDirectory, final int keepBytesInMemory) {
        super(cacheDirectory);

        this.cache = createCache(keepBytesInMemory);
    }

    @Override
    public void clear() {
        cache.invalidateAll();
        cache.cleanUp();

        super.clear();
    }

    @Override
    public InputStream getResource(final URI uri) throws Exception {
        final byte[] content = cache.get(uri);

        if (content.length == 0) {
            return InputStream.nullInputStream();
        }

        return new ByteArrayInputStream(content);
    }

    /**
     * @param keepBytesInMemory int; Disable Caching = 0
     */
    private LoadingCache<URI, byte[]> createCache(final int keepBytesInMemory) {
        // Size of File = Weight
        final Weigher<URI, byte[]> weigher = (key, value) -> value.length;

        final CacheLoader<URI, byte[]> cacheLoader = key -> {
            byte[] content = {};

            final int size = 1024;

            try (InputStream inputStream = super.getResource(key);
                 ByteArrayOutputStream baos = new ByteArrayOutputStream(size)) {
                inputStream.transferTo(baos);

                baos.flush();
                content = baos.toByteArray();
            }

            return content;
        };

        return Caffeine.newBuilder()
                // expireAfterWrite(Duration.ofHours(1L))
                .maximumWeight(keepBytesInMemory)
                .weigher(weigher)
                .evictionListener((key, value, cause) -> getLogger().info("Eviction: {} - {} - {}kB", cause, key,
                        Objects.requireNonNull(value).length / 1024))
                .removalListener((key, value, cause) -> getLogger().info("Removal: {} - {} - {}kB", cause, key,
                        Objects.requireNonNull(value).length / 1024))
                .build(cacheLoader)
                ;
    }
}
