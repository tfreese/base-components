/**
 * Created: 03.07.2011
 */
package de.freese.base.pool.roundRobin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Supplier;
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
    private Supplier<T> creator = null;

    /**
     *
     */
    private Consumer<T> destroyer = null;

    /**
    *
    */
    private int index = 0;

    /**
    *
    */
    private final ReentrantLock lock = new ReentrantLock(true);

    /**
     *
     */
    private final List<T> queue;

    /**
     *
     */
    private int size = 5;

    /**
     * Erstellt ein neues {@link RoundRobinPool} Object.
     *
     * @param size int
     * @param objectFactory {@link ObjectFactory}
     */
    public RoundRobinPool(final int size, final ObjectFactory<T> objectFactory)
    {
        this(size, objectFactory::create, objectFactory::destroy);
    }

    /**
     * Erstellt ein neues {@link RoundRobinPool} Object.
     *
     * @param size int
     * @param creator {@link Supplier}
     * @param destroyer {@link Consumer}; optional
     */
    public RoundRobinPool(final int size, final Supplier<T> creator, final Consumer<T> destroyer)
    {
        super();

        if (size <= 0)
        {
            throw new IllegalArgumentException("size must be a positive number");
        }

        this.size = size;

        this.creator = Objects.requireNonNull(creator, "creator required");

        this.destroyer = destroyer != null ? destroyer : obj -> {
        };

        this.queue = new ArrayList<>(size);
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
            if (getTotalSize() == 0)
            {
                throw new NoSuchElementException("pool exhausted");
            }
            else if (getNumActive() < getTotalSize())
            {
                getQueue().add(this.creator.get());
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
            this.lock.unlock();
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
     * @return {@link List}
     */
    protected List<T> getQueue()
    {
        return this.queue;
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
        this.lock.lock();

        try
        {
            super.shutdown();

            for (Iterator<T> iterator = getQueue().iterator(); iterator.hasNext();)
            {
                T object = iterator.next();

                if (object == null)
                {
                    continue;
                }

                this.destroyer.accept(object);

                iterator.remove();
            }

            this.size = 0;
        }
        finally
        {
            this.lock.unlock();
        }
    }
}
