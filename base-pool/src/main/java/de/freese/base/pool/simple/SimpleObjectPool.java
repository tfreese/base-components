/**
 * Created: 06.02.2014
 */
package de.freese.base.pool.simple;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.IntStream;
import de.freese.base.pool.AbstractObjectPool;
import de.freese.base.pool.factory.ObjectFactory;

/**
 * Einfache Implementierung eines ObjektPools.<br>
 * Verhalten<br>
 * borrowObject: Validate true -> Activate -> return<br>
 * borrowObject: Validate false -> Destroy -> create new<br>
 * returnObject: Passivate
 *
 * @author Thomas Freese
 * @param <T> Konkreter Typ
 */
public class SimpleObjectPool<T> extends AbstractObjectPool<T>
{
    /**
     *
     */
    private final Set<T> busy = new HashSet<>();

    /**
     *
     */
    private int coreSize = 0;

    /**
     *
     */
    private int maxSize = 0;

    /**
     * Erstellt ein neues {@link SimpleObjectPool} Object.
     *
     * @param coreSize int
     * @param maxSize int
     * @param objectFactory {@link ObjectFactory}
     */
    public SimpleObjectPool(final int coreSize, final int maxSize, final ObjectFactory<T> objectFactory)
    {
        super(objectFactory);

        if (coreSize <= 0)
        {
            throw new IllegalArgumentException("coreSize must be a positive number");
        }

        this.coreSize = coreSize;

        if (maxSize <= 0)
        {
            throw new IllegalArgumentException("maxSize must be a positive number");
        }

        this.maxSize = maxSize;

        setQueue(new LinkedList<>());

        // Populate
        IntStream.range(0, this.coreSize).mapToObj(i -> createObject()).forEach(getQueue()::offer);
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

            T object = null;

            while (object == null)
            {
                object = getQueue().poll();

                if ((object == null) && (getTotalSize() < this.maxSize))
                {
                    object = createObject();
                }
                else if (!getObjectFactory().validate(object))
                {
                    getObjectFactory().destroy(object);
                    object = null;
                    continue;
                }

                if (object == null)
                {
                    throw new NoSuchElementException("pool exhausted");
                }

                getObjectFactory().activate(object);

                this.busy.add(object);
            }

            return object;
        }
        finally

        {
            getLock().unlock();
        }
    }

    /**
     * Erzeugt ein Objekt.
     *
     * @return Object
     */
    protected T createObject()
    {
        T object = null;

        while (object == null)
        {
            object = getObjectFactory().create();

            if (!getObjectFactory().validate(object))
            {
                getObjectFactory().destroy(object);
                object = null;
                continue;
            }
        }

        return object;
    }

    /**
     * @see de.freese.base.pool.ObjectPool#getNumActive()
     */
    @Override
    public int getNumActive()
    {
        return this.busy.size();
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

            this.busy.remove(object);
            getQueue().offer(object);
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
            getQueue().addAll(this.busy);
            this.busy.clear();

            super.shutdown();

            this.coreSize = 0;
            this.maxSize = 0;
        }
        finally
        {
            getLock().unlock();
        }
    }
}
