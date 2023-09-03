// Created: 29.08.23
package de.freese.base.core.pool;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Thomas Freese
 */
public abstract class AbstractObjectPool<T> {

    private final Queue<T> idleObjects = new ConcurrentLinkedQueue<>();

    public void free(final T object) {
        if (object == null) {
            return;
        }

        getIdleObjects().offer(object);
    }

    public T get() {
        T object = getIdleObjects().poll();

        return object != null ? object : create();
    }

    protected abstract T create();

    protected Queue<T> getIdleObjects() {
        return idleObjects;
    }
}
