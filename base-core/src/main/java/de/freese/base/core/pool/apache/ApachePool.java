// Created: 04 Feb. 2026
package de.freese.base.core.pool.apache;

import java.util.Objects;

import org.apache.commons.pool2.impl.DefaultPooledObjectInfo;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.base.core.pool.Pool;

/**
 * @author Thomas Freese
 */
final class ApachePool<T> implements Pool<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApachePool.class);

    private final GenericObjectPool<T> pool;

    ApachePool(final GenericObjectPool<T> pool) {
        super();

        this.pool = Objects.requireNonNull(pool, "pool required");
    }

    @Override
    public void close() {
        final String clazzName = pool.listAllObjects().stream()
                .filter(Objects::nonNull)
                .findFirst()
                .map(DefaultPooledObjectInfo::getPooledObjectType)
                .orElse("unknown");

        LOGGER.info("Closing Pool: {}, Size={}, NumActive={}, NumIdle={}, CreatedCount={}, DestroyedCount={}",
                clazzName,
                pool.getNumActive() + pool.getNumIdle(),
                pool.getNumActive(),
                pool.getNumIdle(),
                pool.getCreatedCount(),
                pool.getDestroyedCount()
        );

        pool.clear();
        pool.close();
    }

    @Override
    public T getObject() throws Exception {
        return pool.borrowObject();
    }

    @Override
    public boolean isWrapperFor(final Class<?> iFace) {
        if (iFace.isInstance(pool)) {
            return true;
        }
        else if (pool instanceof final Pool<?> p) {
            return p.isWrapperFor(iFace);
        }

        return false;
    }

    @Override
    public void returnObject(@Nullable final T object) {
        if (object == null) {
            return;
        }

        pool.returnObject(object);
    }

    @Override
    public <C> C unwrap(final Class<C> iFace) throws UnsupportedOperationException {
        if (iFace.isInstance(pool)) {
            return iFace.cast(pool);
        }
        else if (pool instanceof final Pool<?> p) {
            return p.unwrap(iFace);
        }

        throw new UnsupportedOperationException("Wrapped pool is not an instance of " + iFace);
    }
}
