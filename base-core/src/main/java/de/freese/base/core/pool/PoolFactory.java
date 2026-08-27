package de.freese.base.core.pool;

import java.time.Duration;

import org.apache.commons.lang3.function.FailableSupplier;

/**
 * @author Thomas Freese
 * @since 04.02.2026
 */
public interface PoolFactory<T> {
    Pool<T> build();

    PoolFactory<T> expiry(Duration expiry);

    PoolFactory<T> maxSize(int maxSize);

    PoolFactory<T> minSize(int minSize);

    PoolFactory<T> objectSupplier(FailableSupplier<T, Exception> objectSupplier);
}
