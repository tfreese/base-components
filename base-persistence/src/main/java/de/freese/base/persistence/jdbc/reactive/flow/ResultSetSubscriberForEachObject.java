// Created: 10.06.2019
package de.freese.base.persistence.jdbc.reactive.flow;

import java.util.Objects;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Subscriber} fetching single Elements.
 *
 * @author Thomas Freese
 */
public class ResultSetSubscriberForEachObject<T> implements Subscriber<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResultSetSubscriberForEachObject.class);

    private final Consumer<T> consumer;

    private Subscription subscription;

    public ResultSetSubscriberForEachObject(final Consumer<T> consumer) {
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

        // Fetch next Element.
        this.subscription.request(1);
    }

    @Override
    public void onSubscribe(final Subscription subscription) {
        this.subscription = subscription;

        // Fetch first Element.
        this.subscription.request(1);
    }
}
