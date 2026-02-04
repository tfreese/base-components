// Created: 04 Feb. 2026
package de.freese.base.core.pool;

import org.jspecify.annotations.Nullable;

/**
 * @author Thomas Freese
 */
public interface Pool<T> {
    void close();

    T getObject() throws Exception;

    default PooledObject<T> getPooledObject() throws Exception {
        return new PooledObject<>(this, getObject());
    }

    boolean isWrapperFor(final Class<?> iFace);

    void returnObject(@Nullable final T object);

    <C> C unwrap(final Class<C> iFace) throws UnsupportedOperationException;
}
