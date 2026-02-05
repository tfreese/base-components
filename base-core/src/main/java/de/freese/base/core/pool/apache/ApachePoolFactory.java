// Created: 04 Feb. 2026
package de.freese.base.core.pool.apache;

import java.time.Duration;
import java.util.Objects;

import org.apache.commons.lang3.function.FailableSupplier;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import de.freese.base.core.pool.Pool;
import de.freese.base.core.pool.PoolFactory;

/**
 * @author Thomas Freese
 */
public final class ApachePoolFactory<T> implements PoolFactory<T> {
    private Duration expiry = Duration.ofHours(1L);
    private int maxSize = Integer.MAX_VALUE - 1;
    private int minSize = 1;
    private FailableSupplier<T, Exception> objectSupplier;

    @Override
    public Pool<T> build() {
        Objects.requireNonNull(objectSupplier, "objectSupplier required");

        if (minSize > maxSize) {
            throw new IllegalArgumentException("minSize > maxSize: " + minSize + " > " + maxSize);
        }

        final PooledObjectFactory<T> objectFactory = new ExpiryObjectFactory<>(expiry, objectSupplier);

        final GenericObjectPoolConfig<T> config = new GenericObjectPoolConfig<>();
        config.setMaxTotal(maxSize);
        config.setMaxIdle(maxSize);
        config.setMinIdle(minSize);
        config.setFairness(true);
        config.setTestOnBorrow(true);
        config.setTestOnReturn(false);

        final GenericObjectPool<T> pool = new GenericObjectPool<>(objectFactory, config);

        return new ApachePool<>(pool);
    }

    @Override
    public PoolFactory<T> expiry(final Duration expiry) {
        this.expiry = Objects.requireNonNull(expiry, "expiry required");

        return this;
    }

    @Override
    public PoolFactory<T> maxSize(final int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize <= 0: " + maxSize);
        }

        this.maxSize = maxSize;

        return this;
    }

    @Override
    public PoolFactory<T> minSize(final int minSize) {
        if (minSize <= 0) {
            throw new IllegalArgumentException("minSize <= 0: " + minSize);
        }

        this.minSize = minSize;

        return this;
    }

    @Override
    public PoolFactory<T> objectSupplier(final FailableSupplier<T, Exception> objectSupplier) {
        this.objectSupplier = Objects.requireNonNull(objectSupplier, "objectSupplier required");

        return this;
    }
}
