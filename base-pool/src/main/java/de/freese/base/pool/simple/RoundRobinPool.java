/**
 * Created: 03.07.2011
 */
package de.freese.base.pool.simple;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.base.pool.ObjectPool;

/**
 * Ein stark vereinfachter ObjektPool.<br>
 * Die Objekte werden nicht wie bei einem herkömmlichen Pool für die Verwendung gesperrt,<br>
 * sondern im Round-Robin Verfahren bereitgestellt und erst erzeugt, wenn diese benötigt werden.<br>
 * Die Default Grösse des Pools beträgt <code>Runtime.availableProcessors() + 1</code>.<br>
 *
 * @author Thomas Freese
 * @param <T> Konkreter Objekttyp
 */
public class RoundRobinPool<T> implements ObjectPool<T>
{
    /**
     *
     */
    public static final int DEFAULT_SIZE = Runtime.getRuntime().availableProcessors() + 1;

    /**
    *
    */
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleObjectPool.class);

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
     * @param creator {@link Supplier}
     * @param destroyer {@link Consumer}; optional
     */
    public RoundRobinPool(final Supplier<T> creator, final Consumer<T> destroyer)
    {
        this(creator, destroyer, DEFAULT_SIZE);
    }

    /**
     * Erstellt ein neues {@link RoundRobinPool} Object.
     *
     * @param creator {@link Supplier}
     * @param destroyer {@link Consumer}; optional
     * @param size int
     */
    public RoundRobinPool(final Supplier<T> creator, final Consumer<T> destroyer, final int size)
    {
        super();

        Objects.requireNonNull(creator, "creator required");

        if (size <= 0)
        {
            throw new IllegalArgumentException("size: " + size + " (expected: positive integer)");
        }

        this.creator = creator;
        this.destroyer = destroyer != null ? destroyer : obj ->
        {
        };
        this.size = size;
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
            String objectClass = getQueue().get(0).getClass().getSimpleName();
            LOGGER.info("Close Pool<{}> with {} idle and {} aktive Objects", objectClass, getNumIdle(), getNumActive());

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

    /**
     * @return {@link List}
     */
    protected List<T> getQueue()
    {
        return this.queue;
    }
}
