/**
 * Created: 07.07.2018
 */

package de.freese.base.pool.unbounded;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Supplier;
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
    private final Consumer<T> activator;

    /**
     *
     */
    private int counterActive = 0;

    /**
     *
     */
    private final Supplier<T> creator;

    /**
    *
    */
    private final ReentrantLock lock = new ReentrantLock(true);

    /**
     *
     */
    private final Queue<T> queue;

    /**
     * Erstellt ein neues {@link UnboundedObjectPool} Object.
     *
     * @param objectFactory {@link ObjectFactory}
     */
    public UnboundedObjectPool(final ObjectFactory<T> objectFactory)
    {
        this(objectFactory::create, objectFactory::activate);
    }

    /**
     * Erstellt ein neues {@link UnboundedObjectPool} Object.
     *
     * @param creator {@link Supplier}
     * @param activator {@link Consumer}
     */
    public UnboundedObjectPool(final Supplier<T> creator, final Consumer<T> activator)
    {
        super();

        this.creator = Objects.requireNonNull(creator, "creator required");
        this.activator = Objects.requireNonNull(activator, "activator required");

        this.queue = new LinkedList<>();
    }

    /**
     * @see de.freese.base.pool.ObjectPool#borrowObject()
     */
    @Override
    public synchronized T borrowObject()
    {
        this.lock.lock();

        try
        {
            T object = getQueue().poll();

            if (object == null)
            {
                object = this.creator.get();
            }

            this.activator.accept(object);
            this.counterActive++;

            return object;
        }
        finally
        {
            this.lock.unlock();
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
     * @return {@link Queue}<T>
     */
    protected Queue<T> getQueue()
    {
        return this.queue;
    }

    /**
     * @see de.freese.base.pool.ObjectPool#returnObject(java.lang.Object)
     */
    @Override
    public synchronized void returnObject(final T object)
    {
        this.lock.lock();

        try
        {
            getQueue().offer(object);
            this.counterActive--;
        }
        finally
        {
            this.lock.unlock();
        }
    }

    /**
     * @see de.freese.base.pool.ObjectPool#shutdown()
     */
    @Override
    public void shutdown()
    {
        this.lock.lock();

        try
        {
            super.shutdown();

            getQueue().clear();
        }
        finally
        {
            this.lock.unlock();
        }
    }
}
