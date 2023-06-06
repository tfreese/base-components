// Created: 10.06.2019
package de.freese.base.persistence.jdbc.reactive.flow;

import java.util.Objects;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Subscriber} fetches all Elements.
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

    @Override
    public void onComplete() {
        LOGGER.debug("onComplete");
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
    }

    @Override
    public void onSubscribe(final Subscription subscription) {
        // Fetch all Elements, OutOfMemory possible.
        subscription.request(Long.MAX_VALUE);
    }
}
