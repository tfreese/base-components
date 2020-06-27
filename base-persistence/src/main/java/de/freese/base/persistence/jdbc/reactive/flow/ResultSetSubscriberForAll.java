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
 * {@link Subscriber} der alle Objekte auf einmal anfordert.
 *
 * @author Thomas Freese
 * @param <T> Entity-Type
 */
public class ResultSetSubscriberForAll<T> implements Subscriber<T>
{
    /**
    *
    */
    private static final Logger LOGGER = LoggerFactory.getLogger(ResultSetSubscriberForAll.class);

    /**
     *
     */
    private final List<T> data;

    /**
     * Erstellt ein neues {@link ResultSetSubscriberForAll} Object.
     */
    public ResultSetSubscriberForAll()
    {
        this(ArrayList::new);
    }

    /**
     * Erstellt ein neues {@link ResultSetSubscriberForAll} Object.
     *
     * @param listSupplier {@link Supplier}; default ArrayList::new
     */
    public ResultSetSubscriberForAll(final Supplier<List<T>> listSupplier)
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
     * @see de.freese.base.persistence.jdbc.reactive.flow.ResultSetSubscriberForEachObject#onNext(java.lang.Object)
     */
    @Override
    public void onNext(final T item)
    {
        LOGGER.debug("onNext: {}", item);

        this.data.add(item);
    }

    /**
     * @see de.freese.base.persistence.jdbc.reactive.flow.ResultSetSubscriberForEachObject#onSubscribe(java.util.concurrent.Flow.Subscription)
     */
    @Override
    public void onSubscribe(final Subscription subscription)
    {
        // Alle Elemente anfordern, Gefahr durch OutOfMemory.
        subscription.request(Long.MAX_VALUE);
    }
}
