// Created: 03.07.2011
package de.freese.base.core.pool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * Ein stark vereinfachter ObjektPool.<br>
 * Die Objekte werden nicht wie bei einem herkömmlichen Pool für die Verwendung gesperrt,<br>
 * sondern im Round-Robin Verfahren bereitgestellt und erst erzeugt, wenn diese benötigt werden.<br>
 *
 * @author Thomas Freese
 */
public abstract class AbstractRoundRobinPool<T> extends AbstractPool<T> {

    @SuppressWarnings("rawtypes")
    private static final AtomicIntegerFieldUpdater<AbstractRoundRobinPool> NEXT_INDEX = AtomicIntegerFieldUpdater.newUpdater(AbstractRoundRobinPool.class, "nextIndex");

    public static void main(String[] args) {
        try (ObjectPool<Long> pool = new AbstractRoundRobinPool<>(10) {
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
            ex.printStackTrace();
        }
    }

    private final int maxSize;

    private final List<T> queue;

    private volatile int nextIndex;

    protected AbstractRoundRobinPool(final int maxSize) {
        super();

        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize must be a positive number: " + maxSize);
        }

        this.maxSize = maxSize;

        this.queue = new ArrayList<>(maxSize);
    }

    @Override
    public int getNumActive() {
        return this.queue.size();
    }

    @Override
    public int getNumIdle() {
        return 0;
    }

    @Override
    public int getTotalSize() {
        return this.maxSize;
    }

    protected abstract T create();

    @Override
    protected void doClose() {
        this.nextIndex = 0;
        this.queue.clear();
    }

    @Override
    protected void doFree(final T object) {
        // Empty
    }

    @Override
    protected T doGet() {
        if (getNumActive() < getTotalSize()) {
            this.queue.add(create());
        }

        T object = this.queue.get(NEXT_INDEX.get(this));

        NEXT_INDEX.incrementAndGet(this);

        if (this.nextIndex == getNumActive()) {
            NEXT_INDEX.set(this, 0);
        }

        // int length = this.queue.size();
        //
        // int indexToUse = Math.abs(NEXT_INDEX.getAndIncrement(this) % length);
        //
        // T object = this.queue.get(indexToUse);

        return object;
    }
}
