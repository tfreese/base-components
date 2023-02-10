// Created: 10.06.2019
package de.freese.base.persistence.jdbc.reactive.flow;

import java.util.Objects;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Subscriber} der alle Objekte auf einmal anfordert.
 *
 * @param <T> Entity-Type
 *
 * @author Thomas Freese
 */
public class ResultSetSubscriberForAll<T> implements Subscriber<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResultSetSubscriberForAll.class);

    private final Consumer<T> consumer;

    public ResultSetSubscriberForAll(final Consumer<T> consumer) {
        super();

        this.consumer = Objects.requireNonNull(consumer, "consumer required");
    }

    /**
     * @see java.util.concurrent.Flow.Subscriber#onComplete()
     */
    @Override
    public void onComplete() {
        LOGGER.debug("onComplete");
    }

    /**
     * @see java.util.concurrent.Flow.Subscriber#onError(java.lang.Throwable)
     */
    @Override
    public void onError(final Throwable throwable) {
        LOGGER.error(throwable.getMessage(), throwable);

        // Wird bereits in der ResultSetSubscription verarbeitet.
        // this.subscription.cancel();
    }

    /**
     * @see de.freese.base.persistence.jdbc.reactive.flow.ResultSetSubscriberForEachObject#onNext(java.lang.Object)
     */
    @Override
    public void onNext(final T item) {
        LOGGER.debug("onNext: {}", item);

        this.consumer.accept(item);
    }

    /**
     * @see de.freese.base.persistence.jdbc.reactive.flow.ResultSetSubscriberForEachObject#onSubscribe(java.util.concurrent.Flow.Subscription)
     */
    @Override
    public void onSubscribe(final Subscription subscription) {
        // Alle Elemente anfordern, Gefahr durch OutOfMemory.
        subscription.request(Long.MAX_VALUE);
    }
}
