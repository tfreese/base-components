// Created: 10.06.2019
package de.freese.base.persistence.jdbc.reactive.flow;

import java.util.Objects;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Subscriber} der jedes Objekt einzeln anfordert.
 *
 * @param <T> Entity-Type
 *
 * @author Thomas Freese
 */
public class ResultSetSubscriberForEachObject<T> implements Subscriber<T>
{
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ResultSetSubscriberForEachObject.class);
    /**
     *
     */
    private final Consumer<T> consumer;
    /**
     *
     */
    private Subscription subscription;

    /**
     * Erstellt ein neues {@link ResultSetSubscriberForEachObject} Object.
     *
     * @param consumer {@link Consumer}
     */
    public ResultSetSubscriberForEachObject(final Consumer<T> consumer)
    {
        super();

        this.consumer = Objects.requireNonNull(consumer, "consumer required");
    }

    /**
     * @see java.util.concurrent.Flow.Subscriber#onComplete()
     */
    @Override
    public void onComplete()
    {
        LOGGER.debug("onComplete");
    }

    /**
     * @see java.util.concurrent.Flow.Subscriber#onError(java.lang.Throwable)
     */
    @Override
    public void onError(final Throwable throwable)
    {
        LOGGER.error(throwable.getMessage(), throwable);

        // Wird bereits in der ResultSetSubscription verarbeitet..
        // this.subscription.cancel();
    }

    /**
     * @see java.util.concurrent.Flow.Subscriber#onNext(java.lang.Object)
     */
    @Override
    public void onNext(final T item)
    {
        LOGGER.debug("onNext: {}", item);

        this.consumer.accept(item);

        // Nächstes Element anfordern.
        this.subscription.request(1);
    }

    /**
     * @see java.util.concurrent.Flow.Subscriber#onSubscribe(java.util.concurrent.Flow.Subscription)
     */
    @Override
    public void onSubscribe(final Subscription subscription)
    {
        this.subscription = subscription;

        // Erstes Element anfordern.
        this.subscription.request(1);
    }
}
