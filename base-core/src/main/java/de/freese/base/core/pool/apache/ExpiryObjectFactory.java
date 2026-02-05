// Created: 05 Feb. 2026
package de.freese.base.core.pool.apache;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

import org.apache.commons.lang3.function.FailableSupplier;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
final class ExpiryObjectFactory<T> extends BasePooledObjectFactory<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExpiryObjectFactory.class);

    private final Duration expiry;
    private final FailableSupplier<T, Exception> objectSupplier;
    private final Clock systemClock = Clock.systemUTC();

    ExpiryObjectFactory(final Duration expiry, final FailableSupplier<T, Exception> objectSupplier) {
        super();

        this.expiry = Objects.requireNonNull(expiry, "expiry required");
        this.objectSupplier = Objects.requireNonNull(objectSupplier, "objectSupplier required");
    }

    @Override
    public T create() throws Exception {
        final T object = objectSupplier.get();

        LOGGER.debug("Object created for pool: {} / {}", object.getClass().getName(), object);

        return object;
    }

    @Override
    public void destroyObject(final PooledObject<T> p) throws Exception {
        final T object = p.getObject();

        if (object instanceof final AutoCloseable ac) {
            LOGGER.debug("Closing object from pool: {}", object.getClass().getName());

            ac.close();
        }
    }

    @Override
    public boolean validateObject(final PooledObject<T> p) {
        final Instant expiryInstant = systemClock.instant().minusNanos(expiry.toNanos());

        final boolean isExpired = p.getCreateInstant().isBefore(expiryInstant);

        if (isExpired) {
            LOGGER.debug("Evicting object from pool: {}", p);
        }

        return !isExpired;
    }

    @Override
    public PooledObject<T> wrap(final T obj) {
        return new DefaultPooledObject<>(obj);
    }
}
