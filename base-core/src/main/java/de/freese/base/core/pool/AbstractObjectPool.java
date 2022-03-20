// Created: 10.04.2020
package de.freese.base.core.pool;

import java.lang.reflect.ParameterizedType;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basis-Implementierung eines {@link ObjectPool}s.<br>
 * <a href="https://github.com/EsotericSoftware/kryo/blob/master/src/com/esotericsoftware/kryo/util/Pool.java">Kryo Pool</a>
 *
 * @param <T> Type
 *
 * @author Thomas Freese
 */
public abstract class AbstractObjectPool<T> implements ObjectPool<T>
{
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger("ObjectPool");
    /**
     *
     */
    private final Set<T> busy = new HashSet<>();
    /**
     *
     */
    private final Queue<T> freeObjects;

    /**
     * Erstellt ein neues {@link AbstractObjectPool} Object.
     */
    protected AbstractObjectPool()
    {
        super();

        this.freeObjects = new LinkedBlockingQueue<>(Integer.MAX_VALUE);

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown, "ObjectPool-" + getClass().getSimpleName()));
    }

    /**
     * @see de.freese.base.core.pool.ObjectPool#borrowObject()
     */
    @Override
    public T borrowObject()
    {
        T object = this.freeObjects.poll();

        if (object == null)
        {
            object = create();
        }

        doActivate(object);

        this.busy.add(object);

        return object;
    }

    /**
     * @see de.freese.base.core.pool.ObjectPool#getNumActive()
     */
    @Override
    public int getNumActive()
    {
        return this.busy.size();
    }

    /**
     * @see de.freese.base.core.pool.ObjectPool#getNumIdle()
     */
    @Override
    public int getNumIdle()
    {
        return this.freeObjects.size();
    }

    /**
     * @see de.freese.base.core.pool.ObjectPool#returnObject(java.lang.Object)
     */
    @Override
    public void returnObject(final T object)
    {
        if (object == null)
        {
            return;
        }

        doPassivate(object);

        this.freeObjects.offer(object);

        this.busy.remove(object);
    }

    /**
     * @see de.freese.base.core.pool.ObjectPool#shutdown()
     */
    @Override
    public void shutdown()
    {
        LOGGER.info("Close {}", this);

        while (!this.freeObjects.isEmpty())
        {
            T object = this.freeObjects.poll();

            doDestroy(object);
        }

        for (Iterator<T> iterator = this.busy.iterator(); iterator.hasNext(); )
        {
            T object = iterator.next();

            doDestroy(object);

            iterator.remove();
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        String clazzName = null;

        try
        {
            clazzName = tryDetermineObjectClazz().getSimpleName();
        }
        catch (Exception ex)
        {
            T object = borrowObject();

            if (object != null)
            {
                clazzName = object.getClass().getSimpleName();

                returnObject(object);
            }
        }

        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName());
        builder.append("<").append(clazzName).append(">");
        builder.append(": ");
        builder.append("idle = ").append(getNumIdle());
        builder.append(", active = ").append(getNumActive());

        return builder.toString();
    }

    /**
     * @return Object
     */
    protected abstract T create();

    /**
     * Wird in {@link #borrowObject()} ausgeführt.
     *
     * @param object Object
     */
    protected void doActivate(final T object)
    {
        // Empty
    }

    /**
     * Wird in {@link #shutdown()} ausgeführt.
     *
     * @param object Object
     */
    protected void doDestroy(final T object)
    {
        // Empty
    }

    /**
     * Wird in {@link #returnObject(Object)} ausgeführt.
     *
     * @param object Object
     */
    protected void doPassivate(final T object)
    {
        // Empty
    }

    /**
     * Das hier funktioniert nur, wenn die erbende Klasse nicht auch generisch ist !<br>
     * Z.B.:
     *
     * <pre>
     * {@code
     * public class MyObjectPool extends AbstractObjectPool<Integer>
     * }
     * </pre>
     *
     * <br>
     *
     * @return Class
     *
     * @throws ClassCastException Falls was schief geht.
     */
    @SuppressWarnings("unchecked")
    protected Class<T> tryDetermineObjectClazz() throws ClassCastException
    {
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();

        return (Class<T>) parameterizedType.getActualTypeArguments()[0];
    }
}
