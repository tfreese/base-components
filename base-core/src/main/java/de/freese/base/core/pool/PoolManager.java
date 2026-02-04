// Created: 04 Feb. 2026
package de.freese.base.core.pool;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
@SuppressWarnings("unchecked")
public final class PoolManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(PoolManager.class);

    private static final Map<String, Pool<?>> POOLS = new ConcurrentSkipListMap<>();

    public static void close() {
        LOGGER.info("Closing all pools");

        forEach((name, pool) -> {
            try {
                pool.close();
            }
            catch (Exception ex) {
                LOGGER.warn(ex.getMessage(), ex);
            }
        });

        POOLS.clear();
    }

    public static synchronized <T> Pool<T> createPool(final String name, final PoolFactory<T> poolFactory) {
        Pool<?> pool = POOLS.get(name);

        if (pool != null) {
            throw new IllegalStateException("Pool already exists: " + name);
        }

        pool = poolFactory.build();

        POOLS.put(name, pool);

        return (Pool<T>) pool;
    }

    public static void forEach(final BiConsumer<String, Pool<?>> consumer) {
        POOLS.forEach(consumer);
    }

    public static <T> Pool<T> getPool(final String name) {
        final Pool<T> pool = (Pool<T>) POOLS.get(name);

        if (pool == null) {
            throw new IllegalStateException("Pool not found: " + name);
        }

        return pool;
    }

    private PoolManager() {
        super();
    }
}
