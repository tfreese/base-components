/**
 * Created: 06.02.2014
 */
package de.freese.base.pool.simple;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
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
    private final Queue<T> idle = new LinkedList<>();

    /**
     *
     */
    private final ReentrantLock lock = new ReentrantLock(true);

    /**
     *
     */
    private int maxSize = 0;

    /**
     *
     */
    private final ObjectFactory<T> objectFactory;

    /**
     * Erstellt ein neues {@link SimpleObjectPool} Object.
     *
     * @param coreSize int
     * @param maxSize int
     * @param objectFactory {@link ObjectFactory}
     */
    public SimpleObjectPool(final int coreSize, final int maxSize, final ObjectFactory<T> objectFactory)
    {
        super();

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

        this.objectFactory = Objects.requireNonNull(objectFactory, "objectFactory required");

        // Populate
        IntStream.range(0, this.coreSize).mapToObj(i -> createObject()).forEach(this.idle::offer);
    }

    /**
     * @see de.freese.base.pool.ObjectPool#borrowObject()
     */
    @Override
    public T borrowObject()
    {
        this.lock.lock();

        try
        {
            T object = null;

            while (object == null)
            {
                object = this.idle.poll();

                if ((object == null) && (getTotalSize() < this.maxSize))
                {
                    object = createObject();
                }
                else
                {
                    if (!getObjectFactory().validate(object))
                    {
                        getObjectFactory().destroy(object);
                        object = null;
                        continue;
                    }
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
            this.lock.unlock();
        }
    }

    /**
     * Erzeugt ein Objekt.
     *
     * @return Object
     */
    private T createObject()
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
        return this.idle.size();
    }

    /**
     * @return {@link ObjectFactory}
     */
    protected ObjectFactory<T> getObjectFactory()
    {
        return this.objectFactory;
    }

    /**
     * @see de.freese.base.pool.ObjectPool#returnObject(java.lang.Object)
     */
    @Override
    public void returnObject(final T object)
    {
        this.lock.lock();

        try
        {
            getObjectFactory().passivate(object);

            this.busy.remove(object);
            this.idle.offer(object);
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

            this.idle.addAll(this.busy);
            this.busy.clear();

            while (this.idle.size() > 0)
            {
                T object = this.idle.poll();

                if (object == null)
                {
                    continue;
                }

                getObjectFactory().destroy(object);
            }

            this.coreSize = 0;
            this.maxSize = 0;
        }
        finally
        {
            this.lock.unlock();
        }
    }
}
