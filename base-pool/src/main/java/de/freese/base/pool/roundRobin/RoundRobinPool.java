/**
 * Created: 03.07.2011
 */
package de.freese.base.pool.roundRobin;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import de.freese.base.pool.AbstractObjectPool;
import de.freese.base.pool.factory.ObjectFactory;

/**
 * Ein stark vereinfachter ObjektPool.<br>
 * Die Objekte werden nicht wie bei einem herkömmlichen Pool für die Verwendung gesperrt,<br>
 * sondern im Round-Robin Verfahren bereitgestellt und erst erzeugt, wenn diese benötigt werden.<br>
 * Die Default Grösse des Pools beträgt <code>Runtime.availableProcessors() + 1</code>.<br>
 *
 * @author Thomas Freese
 * @param <T> Konkreter Objekttyp
 */
public class RoundRobinPool<T> extends AbstractObjectPool<T>
{
    /**
     *
     */
    public static final int DEFAULT_SIZE = Runtime.getRuntime().availableProcessors() + 1;

    /**
    *
    */
    private int index = 0;

    /**
     *
     */
    private int size = 0;

    /**
     * Erstellt ein neues {@link RoundRobinPool} Object.
     *
     * @param size int
     * @param objectFactory {@link ObjectFactory}
     */
    public RoundRobinPool(final int size, final ObjectFactory<T> objectFactory)
    {
        super(objectFactory);

        if (size <= 0)
        {
            throw new IllegalArgumentException("size must be a positive number");
        }

        this.size = size;

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
            if (getTotalSize() == 0)
            {
                throw new NoSuchElementException("pool exhausted");
            }
            else if (getNumActive() < getTotalSize())
            {
                getQueue().add(getObjectFactory().create());
            }

            T object = getQueue().get(this.index++);

            if (this.index == getTotalSize())
            {
                this.index = 0;
            }

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
        return getQueue().size();
    }

    /**
     * @see de.freese.base.pool.ObjectPool#getNumIdle()
     */
    @Override
    public int getNumIdle()
    {
        return 0;
    }

    /**
     * @see de.freese.base.pool.AbstractObjectPool#getQueue()
     */
    @Override
    protected LinkedList<T> getQueue()
    {
        return (LinkedList<T>) super.getQueue();
    }

    /**
     * @see de.freese.base.pool.ObjectPool#getTotalSize()
     */
    @Override
    public int getTotalSize()
    {
        return this.size;
    }

    /**
     * @see de.freese.base.pool.ObjectPool#returnObject(java.lang.Object)
     */
    @Override
    public void returnObject(final T object)
    {
        // NOOP
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

            this.size = 0;
        }
        finally
        {
            getLock().unlock();
        }
    }
}
