// Created: 30.08.23
package de.freese.base.core.pool;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author Thomas Freese
 */
public final class RoundRobinPool<T> implements AutoCloseable {

    // private static final AtomicIntegerFieldUpdater<RoundRobinPool> NEXT_INDEX = AtomicIntegerFieldUpdater.newUpdater(RoundRobinPool.class, "nextIndex");

    private final Consumer<T> doOnClose;
    private final List<T> queue;

    private volatile int nextIndex;

    public RoundRobinPool(final int size, final Supplier<T> creator) {
        this(size, creator, v -> {
        });
    }

    public RoundRobinPool(final int size, final Supplier<T> creator, final Consumer<T> doOnClose) {
        super();

        if (size < 1) {
            throw new IllegalArgumentException("size must > 0: " + size);
        }

        Objects.requireNonNull(creator, "creator required");
        Objects.requireNonNull(doOnClose, "doOnClose required");

        queue = new ArrayList<>(size);
        this.doOnClose = doOnClose;

        for (int i = 0; i < size; i++) {
            queue.add(creator.get());
        }
    }

    @Override
    public void close() {
        queue.forEach(doOnClose);
        queue.clear();
    }

    public synchronized T get() {
        // if (NEXT_INDEX.get(this) == queue.size()) {
        //     NEXT_INDEX.set(this, 0);
        // }
        //
        // final T object = queue.get(NEXT_INDEX.get(this));
        //
        // NEXT_INDEX.incrementAndGet(this);

        if (nextIndex == queue.size()) {
            nextIndex = 0;
        }

        final T object = queue.get(nextIndex);

        nextIndex += 1;

        return object;
    }
}
