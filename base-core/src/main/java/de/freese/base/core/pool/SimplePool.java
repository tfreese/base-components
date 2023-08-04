// Created: 22.06.2021
package de.freese.base.core.pool;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simplest possible Object-Pool.
 *
 * @author Thomas Freese
 */
public abstract class SimplePool<T> implements ObjectPool<T> {

    public static void main(String[] args) {
        try (ObjectPool<Long> pool = new SimplePool<>() {
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

    private final AtomicInteger activeCounter = new AtomicInteger(0);
    
    private final Queue<T> freeObjects = new ConcurrentLinkedQueue<>();

    @Override
    public void close() throws Exception {
        this.freeObjects.clear();

        activeCounter.set(0);
    }

    @Override
    public void free(final T object) {
        if (object == null) {
            return;
        }

        activeCounter.decrementAndGet();

        this.freeObjects.offer(object);
    }

    @Override
    public T get() {
        T object = this.freeObjects.poll();

        activeCounter.incrementAndGet();

        return object != null ? object : create();
    }

    @Override
    public int getNumActive() {
        return activeCounter.get();
    }

    @Override
    public int getNumIdle() {
        return this.freeObjects.size();
    }

    protected abstract T create();
}
