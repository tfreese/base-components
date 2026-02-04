// Created: 04 Feb. 2026
package de.freese.base.core.pool;

import java.util.Objects;

import org.jspecify.annotations.Nullable;

/**
 * @author Thomas Freese
 */
public final class PooledObject<T> implements AutoCloseable {
    private final Pool<T> pool;

    private @Nullable T object;

    PooledObject(final Pool<T> pool, final T object) {
        super();

        this.pool = Objects.requireNonNull(pool, "pool required");
        this.object = Objects.requireNonNull(object, "object required");
    }

    @Override
    public void close() {
        if (object != null) {
            pool.returnObject(object);
        }

        object = null;
    }

    public T getObject() {
        return Objects.requireNonNull(object, "object required");
    }
}
