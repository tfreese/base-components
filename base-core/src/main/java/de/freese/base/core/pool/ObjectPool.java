// Created: 30.08.23
package de.freese.base.core.pool;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author Thomas Freese
 */
public final class ObjectPool<T> extends AbstractObjectPool<T> implements AutoCloseable {

    /**
     * CopyOnWriteArraySet
     */
    private final Set<T> busyObjects = ConcurrentHashMap.newKeySet();
    private final Map<T, Long> creationTimestamps = new ConcurrentHashMap<>();
    private final Supplier<T> creator;
    private final Consumer<T> doOnClose;
    private long expirationDuration = -1;

    public ObjectPool(Supplier<T> creator) {
        this(creator, Objects::nonNull);
    }

    public ObjectPool(Supplier<T> creator, Consumer<T> doOnClose) {
        super();

        this.creator = Objects.requireNonNull(creator, "creator required");
        this.doOnClose = Objects.requireNonNull(doOnClose, "doOnClose required");
    }

    @Override
    public void close() {
        getIdleObjects().forEach(doOnClose);
        getIdleObjects().clear();

        busyObjects.forEach(doOnClose);
        busyObjects.clear();
    }

    @Override
    public void free(T object) {
        super.free(object);

        if (object == null) {
            return;
        }

        busyObjects.remove(object);
    }

    @Override
    public T get() {
        T object = getIdleObjects().poll();

        if (object != null && expirationDuration > 0) {
            Long creationTimestamp = creationTimestamps.getOrDefault(object, 0L);
            long expiryTimestamp = creationTimestamp + expirationDuration;

            if (expiryTimestamp < System.currentTimeMillis()) {
                creationTimestamps.remove(object);
                busyObjects.remove(object);
                doOnClose.accept(object);
                object = null;
            }
        }

        if (object == null) {
            object = create();
        }

        busyObjects.add(object);

        return object;
    }

    public int getNumActive() {
        return busyObjects.size();
    }

    public int getNumIdle() {
        return getIdleObjects().size();
    }

    public int getTotalSize() {
        return getNumActive() + getNumIdle();
    }

    public void setExpirationDuration(Duration duration) {
        this.expirationDuration = duration.toMillis();
    }

    @Override
    protected T create() {
        T object = creator.get();

        creationTimestamps.put(object, System.currentTimeMillis());

        return object;
    }
}
