// Created: 10.04.2020
package de.freese.base.core.pool;

import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <a href="https://github.com/EsotericSoftware/kryo/blob/master/src/com/esotericsoftware/kryo/util/Pool.java">Kryo Pool</a>
 *
 * @author Thomas Freese
 */
public abstract class AbstractObjectPool<T> implements ObjectPool<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger("ObjectPool");

    private final Set<T> busy = Collections.synchronizedSet(new HashSet<>());

    private final Queue<T> freeObjects;

    protected AbstractObjectPool() {
        super();

        this.freeObjects = new LinkedBlockingQueue<>(Integer.MAX_VALUE);

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown, "ObjectPool-" + getClass().getSimpleName()));
    }

    @Override
    public T borrowObject() {
        T object = this.freeObjects.poll();

        if (object == null) {
            object = create();
        }

        doActivate(object);

        this.busy.add(object);

        return object;
    }

    @Override
    public int getNumActive() {
        return this.busy.size();
    }

    @Override
    public int getNumIdle() {
        return this.freeObjects.size();
    }

    @Override
    public void returnObject(final T object) {
        if (object == null) {
            return;
        }

        doPassivate(object);

        this.freeObjects.offer(object);

        this.busy.remove(object);
    }

    @Override
    public void shutdown() {
        LOGGER.info("Close {}", this);

        while (!this.freeObjects.isEmpty()) {
            T object = this.freeObjects.poll();

            doDestroy(object);
        }

        for (Iterator<T> iterator = this.busy.iterator(); iterator.hasNext(); ) {
            T object = iterator.next();

            doDestroy(object);

            iterator.remove();
        }
    }

    @Override
    public String toString() {
        String clazzName = null;

        try {
            clazzName = tryDetermineType().getSimpleName();
        }
        catch (Exception ex) {
            T object = borrowObject();

            if (object != null) {
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

    protected abstract T create();

    /**
     * Executed in {@link #borrowObject()}.
     */
    protected void doActivate(final T object) {
        // Empty
    }

    /**
     * Executed in {@link #shutdown()}.
     */
    protected void doDestroy(final T object) {
        // Empty
    }

    /**
     * Executed in {@link #returnObject(Object)}.
     */
    protected void doPassivate(final T object) {
        // Empty
    }

    /**
     * This works only, if the Super-Class is not generic too !
     *
     * <pre>
     * {@code
     * public class MyObjectPool extends AbstractObjectPool<Integer>
     * }
     * </pre>
     */
    @SuppressWarnings("unchecked")
    protected Class<T> tryDetermineType() throws ClassCastException {
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();

        return (Class<T>) parameterizedType.getActualTypeArguments()[0];
    }
}
