/**
 * Created: 07.07.2018
 */

package de.freese.base.pool.unbounded;

import java.util.LinkedList;
import de.freese.base.pool.AbstractObjectPool;
import de.freese.base.pool.factory.ObjectFactory;

/**
 * Einfacher ObjectPool ohne Limit.
 *
 * @author Thomas Freese
 * @param <T> Konkreter Typ
 */
public class UnboundedObjectPool<T> extends AbstractObjectPool<T>
{
    /**
     *
     */
    private int counterActive = 0;

    /**
     * Erstellt ein neues {@link UnboundedObjectPool} Object.
     *
     * @param objectFactory {@link ObjectFactory}
     */
    public UnboundedObjectPool(final ObjectFactory<T> objectFactory)
    {
        super(objectFactory);

        setQueue(new LinkedList<>());
    }

    /**
     * @see de.freese.base.pool.ObjectPool#borrowObject()
     */
    @Override
    public T borrowObject()
    {
        getLock().lock();

        try
        {
            T object = getQueue().poll();

            if (object == null)
            {
                object = getObjectFactory().create();
            }

            getObjectFactory().activate(object);
            this.counterActive++;

            return object;
        }
        finally
        {
            getLock().unlock();
        }
    }

    /**
     * @see de.freese.base.pool.ObjectPool#getNumActive()
     */
    @Override
    public int getNumActive()
    {
        return this.counterActive;
    }

    /**
     * @see de.freese.base.pool.ObjectPool#getNumIdle()
     */
    @Override
    public int getNumIdle()
    {
        return getQueue().size();
    }

    /**
     * @see de.freese.base.pool.ObjectPool#returnObject(java.lang.Object)
     */
    @Override
    public void returnObject(final T object)
    {
        if (object == null)
        {
            return;
        }

        getLock().lock();

        try
        {
            getObjectFactory().passivate(object);

            getQueue().offer(object);
            this.counterActive--;
        }
        finally
        {
            getLock().unlock();
        }
    }

    /**
     * @see de.freese.base.pool.ObjectPool#shutdown()
     */
    @Override
    public void shutdown()
    {
        getLock().lock();

        try
        {
            super.shutdown();

            this.counterActive = 0;
        }
        finally
        {
            getLock().unlock();
        }
    }
}
