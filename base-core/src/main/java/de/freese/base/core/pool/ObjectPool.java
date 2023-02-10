// Created: 17.10.2017
package de.freese.base.core.pool;

import java.util.NoSuchElementException;

/**
 * <a href="https://github.com/EsotericSoftware/kryo/blob/master/src/com/esotericsoftware/kryo/util/Pool.java">Kryo Pool</a>
 *
 * @author Thomas Freese
 */
public interface ObjectPool<T> {
    /**
     * @throws NoSuchElementException if Pool is depleted
     */
    T borrowObject();

    int getNumActive();

    int getNumIdle();

    default int getTotalSize() {
        return getNumActive() + getNumIdle();
    }

    void returnObject(T object);

    void shutdown();
}
