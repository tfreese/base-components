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
 * {@link Subscriber} fetching the Element by Blocks.
 *
 * @author Thomas Freese
 */
public class ResultSetSubscriberForFetchSize<T> implements Subscriber<T> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ResultSetSubscriberForFetchSize.class);

    private final AtomicBoolean completed;

    private final Consumer<T> consumer;

    private final AtomicInteger counter;

    private final int fetchSize;

    private Subscription subscription;

    public ResultSetSubscriberForFetchSize(final Consumer<T> consumer, final int fetchSize) {
        super();

        this.consumer = Objects.requireNonNull(consumer, "consumer required");
        this.fetchSize = fetchSize;

        this.completed = new AtomicBoolean(false);
        this.counter = new AtomicInteger(0);
    }

    @Override
    public void onComplete() {
        LOGGER.debug("onComplete");

        this.completed.set(true);
    }

    @Override
    public void onError(final Throwable throwable) {
        LOGGER.error(throwable.getMessage(), throwable);

        // Handled in ResultSetSubscription.
        // this.subscription.cancel();
    }

    @Override
    public void onNext(final T item) {
        LOGGER.debug("onNext: {}", item);

        this.consumer.accept(item);

        this.counter.incrementAndGet();

        if (this.counter.get() > (this.fetchSize - 1)) {
            this.counter.set(0);

            // Fetch the next n Elements.
            this.subscription.request(this.completed.get() ? 0 : this.fetchSize);
        }
    }

    @Override
    public void onSubscribe(final Subscription subscription) {
        this.subscription = subscription;

        // Fetch the first n Elements.
        subscription.request(this.fetchSize);
    }
}
