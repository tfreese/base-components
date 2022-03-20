// Created: 17.10.2017
package de.freese.base.core.pool;

import java.util.NoSuchElementException;

/**
 * Interface für einen ObjectPool.<br>
 * <a href="https://github.com/EsotericSoftware/kryo/blob/master/src/com/esotericsoftware/kryo/util/Pool.java">Kryo Pool</a>
 *
 * @param <T> Konkreter Typ
 *
 * @author Thomas Freese
 */
public interface ObjectPool<T>
{
    /**
     * Liefert ein Objekt aus dem Pool.
     *
     * @return Object
     *
     * @throws NoSuchElementException, wenn der Pool erschöpft ist
     */
    T borrowObject();

    /**
     * Liefert die Anzahl der aktiven, dem Pool entnommenen, Objekte.
     *
     * @return int
     */
    int getNumActive();

    /**
     * Liefert die Anzahl der zur Verfügung stehenden, im Pool vorhandenen, Objekte.
     *
     * @return int
     */
    int getNumIdle();

    /**
     * Liefert die Anzahl aller durch den Pool verwalteten Objekte,
     *
     * @return int
     */
    default int getTotalSize()
    {
        return getNumActive() + getNumIdle();
    }

    /**
     * Übergibt ein Objekt zurück in den Pool.
     *
     * @param object Object
     */
    void returnObject(T object);

    /**
     * Herunterfahren des Pools, alternativ auch über ShutdownHook.
     */
    void shutdown();
}
