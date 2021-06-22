/**
 * Created: 03.07.2011
 */
package de.freese.base.core.pool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ein stark vereinfachter ObjektPool.<br>
 * Die Objekte werden nicht wie bei einem herkömmlichen Pool für die Verwendung gesperrt,<br>
 * sondern im Round-Robin Verfahren bereitgestellt und erst erzeugt, wenn diese benötigt werden.<br>
 * Die Default Grösse des Pools beträgt <code>Runtime.availableProcessors() + 1</code>.<br>
 *
 * @author Thomas Freese
 * @param <T> Konkreter Objekttyp
 */
public abstract class AbstractRoundRobinPool<T> implements ObjectPool<T>
{
    /**
    *
    */
    private static final Logger LOGGER = LoggerFactory.getLogger("RoundRobinPool");

    /**
    *
    */
    @SuppressWarnings(
    {
            "unused", "rawtypes"
    })
    private static final AtomicIntegerFieldUpdater<AbstractRoundRobinPool> NEXT_INDEX =
            AtomicIntegerFieldUpdater.newUpdater(AbstractRoundRobinPool.class, "nextIndex");

    /**
    *
    */
    private volatile int nextIndex;

    /**
     *
     */
    private final List<T> queue;

    /**
     * Erstellt ein neues {@link AbstractRoundRobinPool} Object.
     *
     * @param size int
     */
    protected AbstractRoundRobinPool(final int size)
    {
        super();

        if (size <= 0)
        {
            throw new IllegalArgumentException("size must be a positive number");
        }

        this.queue = new ArrayList<>(size);
    }

    /**
     * @see de.freese.base.core.pool.ObjectPool#borrowObject()
     */
    @Override
    public T borrowObject()
    {
        if (getNumActive() < getTotalSize())
        {
            this.queue.add(create());
        }

        T object = this.queue.get(this.nextIndex);

        this.nextIndex++;

        if (this.nextIndex == getNumActive())
        {
            this.nextIndex = 0;
        }

        // int length = this.queue.size();
        //
        // int indexToUse = Math.abs(NEXT_INDEX.getAndIncrement(this) % length);
        //
        // T object = this.queue.get(indexToUse);

        return object;
    }

    /**
     * @return Object
     */
    protected abstract T create();

    /**
     * @see de.freese.base.core.pool.ObjectPool#getNumActive()
     */
    @Override
    public int getNumActive()
    {
        return this.queue.size();
    }

    /**
     * @see de.freese.base.core.pool.ObjectPool#getNumIdle()
     */
    @Override
    public int getNumIdle()
    {
        return 0;
    }

    /**
     * @see de.freese.base.core.pool.ObjectPool#returnObject(java.lang.Object)
     */
    @Override
    public void returnObject(final T object)
    {
        // Empty
    }

    /**
     * @see de.freese.base.core.pool.ObjectPool#shutdown()
     */
    @Override
    public void shutdown()
    {
        LOGGER.info("Close {}", this);

        this.nextIndex = 0;
        this.queue.clear();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        String clazzName = borrowObject().getClass().getSimpleName();

        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName());
        builder.append("<").append(clazzName).append(">");
        builder.append(": ");
        builder.append("active = ").append(getNumActive());

        return builder.toString();
    }
}
