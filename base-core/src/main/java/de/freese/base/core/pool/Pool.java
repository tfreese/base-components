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

    boolean isWrapperFor(Class<?> iFace);

    void returnObject(@Nullable T object);

    <C> C unwrap(Class<C> iFace) throws UnsupportedOperationException;
}
