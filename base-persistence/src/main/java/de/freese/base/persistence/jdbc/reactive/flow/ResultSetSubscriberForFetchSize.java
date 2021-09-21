// Created: 10.06.2019
package de.freese.base.persistence.jdbc.reactive.flow;

import java.util.Objects;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Subscriber} der die Objekte in Blöcken anfordert.
 *
 * @author Thomas Freese
 *
 * @param <T> Entity-Type
 */
public class ResultSetSubscriberForFetchSize<T> implements Subscriber<T>
{
    /**
    *
    */
    private static final int DEFAULT_FETCH_SIZE = 100;
    /**
    *
    */
    private static final Logger LOGGER = LoggerFactory.getLogger(ResultSetSubscriberForFetchSize.class);

    /**
     *
     */
    private final AtomicBoolean completed;
    /**
     *
     */
    private final Consumer<T> consumer;
    /**
     *
     */
    private final AtomicInteger counter;
    /**
     *
     */
    private final int fetchSize;

    /**
    *
    */
    private Subscription subscription;

    /**
     * Erstellt ein neues {@link ResultSetSubscriberForFetchSize} Object.<br>
     * Default fetchSize = 100
     *
     * @param consumer {@link Consumer}
     */
    public ResultSetSubscriberForFetchSize(final Consumer<T> consumer)
    {
        this(consumer, DEFAULT_FETCH_SIZE);
    }

    /**
     * Erstellt ein neues {@link ResultSetSubscriberForFetchSize} Object.
     *
     * @param consumer {@link Consumer}
     * @param fetchSize int
     */
    public ResultSetSubscriberForFetchSize(final Consumer<T> consumer, final int fetchSize)
    {
        super();

        this.consumer = Objects.requireNonNull(consumer, "consumer required");
        this.fetchSize = fetchSize;

        this.completed = new AtomicBoolean(false);
        this.counter = new AtomicInteger(0);
    }

    /**
     * @see java.util.concurrent.Flow.Subscriber#onComplete()
     */
    @Override
    public void onComplete()
    {
        LOGGER.debug("onComplete");

        this.completed.set(true);
    }

    /**
     * @see java.util.concurrent.Flow.Subscriber#onError(java.lang.Throwable)
     */
    @Override
    public void onError(final Throwable throwable)
    {
        throwable.printStackTrace();

        // Wird bereits in der ResultSetSubscription verarbeitet.
        // this.subscription.cancel();
    }

    /**
     * @see de.freese.base.persistence.jdbc.reactive.flow.ResultSetSubscriberForEachObject#onNext(java.lang.Object)
     */
    @Override
    public void onNext(final T item)
    {
        LOGGER.debug("onNext: {}", item);

        this.consumer.accept(item);

        this.counter.incrementAndGet();

        if (this.counter.get() > (this.fetchSize - 1))
        {
            this.counter.set(0);

            // Die nächsten n-Elemente anfordern.
            this.subscription.request(this.completed.get() ? 0 : this.fetchSize);
        }
    }

    /**
     * @see de.freese.base.persistence.jdbc.reactive.flow.ResultSetSubscriberForEachObject#onSubscribe(java.util.concurrent.Flow.Subscription)
     */
    @Override
    public void onSubscribe(final Subscription subscription)
    {
        this.subscription = subscription;

        // Die ersten n-Elemente anfordern.
        subscription.request(this.fetchSize);
    }
}
