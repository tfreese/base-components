/**
 * Created: 10.06.2019
 */

package de.freese.base.persistence.jdbc.reactive.flow;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Subscriber} der jedes Objekt einzeln anfordert.
 *
 * @author Thomas Freese
 * @param <T> Entity-Type
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
    private final List<T> data;

    /**
    *
    */
    private Subscription subscription = null;

    /**
     * Erstellt ein neues {@link ResultSetSubscriberForEachObject} Object.
     */
    public ResultSetSubscriberForEachObject()
    {
        this(ArrayList::new);
    }

    /**
     * Erstellt ein neues {@link ResultSetSubscriberForEachObject} Object.
     *
     * @param listSupplier {@link Supplier}; default ArrayList::new
     */
    public ResultSetSubscriberForEachObject(final Supplier<List<T>> listSupplier)
    {
        super();

        this.data = listSupplier.get();
    }

    /**
     * @return {@link List}<T>
     */
    public List<T> getData()
    {
        return this.data;
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
        throwable.printStackTrace();

        // Wird bereits im ResultSetSubscription verarbeitet.
        // this.subscription.cancel();
    }

    /**
     * @see java.util.concurrent.Flow.Subscriber#onNext(java.lang.Object)
     */
    @Override
    public void onNext(final T item)
    {
        LOGGER.debug("onNext: {}", item);

        this.data.add(item);

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
