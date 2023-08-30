// Created: 30.08.23
package de.freese.base.core.pool;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author Thomas Freese
 */
public final class ObjectPool<T> implements AutoCloseable {

    private final Set<T> busy = Collections.synchronizedSet(new HashSet<>());

    private final Supplier<T> creator;

    private final Consumer<T> doOnClose;

    private final Queue<T> queue = new ConcurrentLinkedQueue<>();

    public ObjectPool(Supplier<T> creator) {
        this(creator, Objects::nonNull);
    }

    public ObjectPool(Supplier<T> creator, Consumer<T> doOnClose) {
        super();

        this.creator = Objects.requireNonNull(creator, "creator required");
        this.doOnClose = Objects.requireNonNull(doOnClose, "doOnClose required");
    }

    @Override
    public void close() throws Exception {
        queue.forEach(doOnClose);
        queue.clear();

        busy.forEach(doOnClose);
        busy.clear();
    }

    public void free(T object) {
        this.queue.offer(object);
        this.busy.remove(object);
    }

    public T get() {
        T object = queue.poll();

        if (object == null) {
            object = creator.get();
        }

        busy.add(object);

        return object;
    }

    public int getNumActive() {
        return busy.size();
    }

    public int getNumIdle() {
        return queue.size();
    }

    public int getTotalSize() {
        return getNumActive() + getNumIdle();
    }
}
