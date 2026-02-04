// Created: 04 Feb. 2026
package de.freese.base.core.pool;

import java.time.Duration;

import org.apache.commons.lang3.function.FailableSupplier;

/**
 * @author Thomas Freese
 */
public interface PoolFactory<T> {
    Pool<T> build();

    PoolFactory<T> expiry(final Duration expiry);

    PoolFactory<T> maxSize(final int maxSize);

    PoolFactory<T> minSize(final int minSize);

    PoolFactory<T> objectSupplier(final FailableSupplier<T, Exception> objectSupplier);
}
