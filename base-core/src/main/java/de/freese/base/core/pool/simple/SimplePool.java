// Created: 29.08.23
package de.freese.base.core.pool.simple;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.lang3.function.FailableSupplier;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.base.core.pool.Pool;

/**
 * @author Thomas Freese
 */
class SimplePool<T> implements Pool<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimplePool.class);

    private final Queue<T> idleObjects = new ConcurrentLinkedQueue<>();

    private final FailableSupplier<T, Exception> objectSupplier;

    SimplePool(final FailableSupplier<T, Exception> objectSupplier) {
        super();

        this.objectSupplier = Objects.requireNonNull(objectSupplier, "objectSupplier required");
    }

    @Override
    public void close() {
        final String clazzName = idleObjects.stream()
                .filter(Objects::nonNull)
                .findFirst()
                .map(Object::getClass)
                .map(Class::getName)
                .orElse("unknown");

        LOGGER.info("Closing Pool: {}, Size={}",
                clazzName,
                idleObjects.size()
        );

        idleObjects.clear();
    }

    @Override
    public T getObject() throws Exception {
        final T object = idleObjects.poll();

        return object != null ? object : objectSupplier.get();
    }

    @Override
    public boolean isWrapperFor(final Class<?> iFace) {
        return iFace.isInstance(idleObjects);
    }

    @Override
    public void returnObject(@Nullable final T object) {
        if (object == null) {
            return;
        }

        idleObjects.offer(object);
    }

    @Override
    public <C> C unwrap(final Class<C> iFace) throws UnsupportedOperationException {
        if (iFace.isInstance(idleObjects)) {
            return iFace.cast(idleObjects);
        }

        throw new UnsupportedOperationException("Wrapped pool is not an instance of " + iFace);
    }
}
