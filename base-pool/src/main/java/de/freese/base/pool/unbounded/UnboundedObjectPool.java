/**
 * Created: 07.07.2018
 */

package de.freese.base.pool.unbounded;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.base.pool.ObjectPool;

/**
 * Einfacher ObjectPool ohne Limit.
 *
 * @author Thomas Freese
 * @param <T> Konkreter Typ
 */
public class UnboundedObjectPool<T> implements ObjectPool<T>
{
    /**
    *
    */
    private static final Logger LOGGER = LoggerFactory.getLogger(UnboundedObjectPool.class);

    /**
     *
     */
    private final Consumer<T> activateFunction;

    /**
     *
     */
    private int counterActive = 0;

    /**
     *
     */
    private final Supplier<T> createFunction;
    /**
     *
     */
    private final Queue<T> queue;

    /**
     * Erstellt ein neues {@link UnboundedObjectPool} Object.
     *
     * @param createFunction {@link Supplier}
     * @param activateFunction {@link Consumer}
     */
    public UnboundedObjectPool(final Supplier<T> createFunction, final Consumer<T> activateFunction)
    {
        super();

        this.queue = new LinkedList<>();

        this.createFunction = Objects.requireNonNull(createFunction, "createFunction required");
        this.activateFunction = Objects.requireNonNull(activateFunction, "activateFunction required");

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    /**
     * @see de.freese.base.pool.ObjectPool#borrowObject()
     */
    @Override
    public synchronized T borrowObject()
    {
        T object = getQueue().poll();

        if (object == null)
        {
            object = getCreateFunction().get();
        }

        getActivateFunction().accept(object);
        this.counterActive++;

        return object;
    }

    /**
     * @return {@link Consumer}
     */
    protected Consumer<T> getActivateFunction()
    {
        return this.activateFunction;
    }

    /**
     * @return {@link Supplier}
     */
    protected Supplier<T> getCreateFunction()
    {
        return this.createFunction;
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return LOGGER;
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
        getQueue().offer(object);
        this.counterActive--;
    }

    /**
     * @see de.freese.base.pool.ObjectPool#shutdown()
     */
    @Override
    public void shutdown()
    {
        T object = getQueue().peek();

        if (object == null)
        {
            object = this.createFunction.get();
        }

        String objectClassName = object.getClass().getSimpleName();
        getLogger().info("Close Pool<{}> with {} idle and {} aktive Objects", objectClassName, getNumIdle(), getNumActive());

        getQueue().clear();
        // while (getQueue().size() > 0)
        // {
        // T object = getQueue().poll();
        //
        // if (object == null)
        // {
        // continue;
        // }
        //
        // // Object destroy
        // }
    }
}
