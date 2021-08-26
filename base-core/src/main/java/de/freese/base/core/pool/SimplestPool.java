// Created: 22.06.2021
package de.freese.base.core.pool;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Simplest possible Object-Pool.
 *
 * @author Thomas Freese
 *
 * @param <T> Type
 */
public abstract class SimplestPool<T>
{
    /**
    *
    */
    private final Queue<T> freeObjects = new LinkedBlockingQueue<>(Integer.MAX_VALUE);

    /**
     *
     */
    public void clear()
    {
        this.freeObjects.clear();
    }

    /**
     * @return Object
     */
    protected abstract T create();

    /**
     * @param object Object
     */
    public void free(final T object)
    {
        if (object == null)
        {
            return;
        }

        this.freeObjects.offer(object);
    }

    /**
     * @return Object
     */
    public T get()
    {
        T object = this.freeObjects.poll();

        return object != null ? object : create();
    }

}
