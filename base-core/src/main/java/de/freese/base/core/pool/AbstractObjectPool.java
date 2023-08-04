// Created: 10.04.2020
package de.freese.base.core.pool;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Thomas Freese
 */
public abstract class AbstractObjectPool<T> extends AbstractPool<T> {

    public static void main(String[] args) {
        try (ObjectPool<Long> pool = new AbstractObjectPool<>() {
            @Override
            protected Long create() {
                return System.nanoTime();
            }
        }) {
            for (int i = 0; i < 10; i++) {
                Long value = pool.get();
                System.out.println(value);
                pool.free(value);
            }
        }
        catch (Exception ex) {
            // Ignore
        }
    }

    private final Set<T> busy = Collections.synchronizedSet(new HashSet<>());

    private final Queue<T> freeObjects = new ConcurrentLinkedQueue<>();

    protected AbstractObjectPool() {
        super();

        //        Runtime.getRuntime().addShutdownHook(new Thread(this::close, "ObjectPool-" + getClass().getSimpleName()));
    }

    @Override
    public int getNumActive() {
        return this.busy.size();
    }

    @Override
    public int getNumIdle() {
        return this.freeObjects.size();
    }

    protected abstract T create();

    /**
     * Executed in {@link #get()}.
     */
    protected void doActivate(final T object) {
        // Empty
    }

    @Override
    protected void doClose() {
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

    /**
     * Executed in {@link #close()}.
     */
    protected void doDestroy(final T object) {
        // Empty
    }

    @Override
    protected void doFree(final T object) {
        doPassivate(object);

        this.freeObjects.offer(object);

        this.busy.remove(object);
    }

    @Override
    protected T doGet() {
        T object = this.freeObjects.poll();

        if (object == null) {
            object = create();
        }

        doActivate(object);

        this.busy.add(object);

        return object;
    }

    /**
     * Executed in {@link #free(Object)}.
     */
    protected void doPassivate(final T object) {
        // Empty
    }
}
