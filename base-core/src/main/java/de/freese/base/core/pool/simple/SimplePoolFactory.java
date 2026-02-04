// Created: 04 Feb. 2026
package de.freese.base.core.pool.simple;

import java.time.Duration;
import java.util.Objects;

import org.apache.commons.lang3.function.FailableSupplier;

import de.freese.base.core.pool.Pool;
import de.freese.base.core.pool.PoolFactory;

/**
 * @author Thomas Freese
 */
public final class SimplePoolFactory<T> implements PoolFactory<T> {
    private FailableSupplier<T, Exception> objectSupplier;

    @Override
    public Pool<T> build() {
        Objects.requireNonNull(objectSupplier, "objectSupplier required");

        return new SimplePool<>(objectSupplier);
    }

    @Override
    public PoolFactory<T> expiry(final Duration expiry) {
        // Empty

        return this;
    }

    @Override
    public PoolFactory<T> maxSize(final int maxSize) {
        // Empty

        return this;
    }

    @Override
    public PoolFactory<T> minSize(final int minSize) {
        // Empty

        return this;
    }

    @Override
    public PoolFactory<T> objectSupplier(final FailableSupplier<T, Exception> objectSupplier) {
        this.objectSupplier = Objects.requireNonNull(objectSupplier, "objectSupplier required");

        return this;
    }
}
