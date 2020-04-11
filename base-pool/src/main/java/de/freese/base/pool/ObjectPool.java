/**
 * Created: 17.10.2017
 */

package de.freese.base.pool;

import java.util.NoSuchElementException;

/**
 * Interface zur Kapselung eines ObjectPools.<br>
 *
 * @author Thomas Freese
 * @param <T> Konkreter Typ
 */
public interface ObjectPool<T>
{
    /**
     * Liefert ein Objekt aus dem Pool.
     *
     * @return Object
     * @throws NoSuchElementException wenn der Pool erschöpft ist
     */
    public T borrowObject();

    /**
     * Liefert die Anzahl der aktiven, dem Pool entnommenen, Objekte.
     *
     * @return int
     */
    public int getNumActive();

    /**
     * Liefert die Anzahl der zur Verfügung stehenden, im Pool vorhandenen, Objekte.
     *
     * @return int
     */
    public int getNumIdle();

    /**
     * Liefert die Anzahl aller durch den Pool verwalteten Objekte,
     *
     * @return int
     */
    public default int getTotalSize()
    {
        return getNumActive() + getNumIdle();
    }

    /**
     * Übergibt ein Objekt zurück in den Pool.
     *
     * @param object Object
     */
    public void returnObject(T object);

    /**
     * Herunterfahren des Pools, alternativ auch über ShutdownHook.
     */
    public void shutdown();
}
