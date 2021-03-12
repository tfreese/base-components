/**
 * Created: 10.06.2019
 */

package de.freese.base.persistence.jdbc.reactive.flow;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Subscriber} der die Objekte in Blöcken anfordert.
 *
 * @author Thomas Freese
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
    private final AtomicInteger counter;

    /**
     *
     */
    private final List<T> data;

    /**
     *
     */
    private final int fetchSize;

    /**
    *
    */
    private Subscription subscription;

    /**
     * Erstellt ein neues {@link ResultSetSubscriberForFetchSize} Object.
     */
    public ResultSetSubscriberForFetchSize()
    {
        this(ArrayList::new);
    }

    /**
     * Erstellt ein neues {@link ResultSetSubscriberForFetchSize} Object.
     *
     * @param listSupplier {@link Supplier}; default ArrayList::new
     */
    public ResultSetSubscriberForFetchSize(final Supplier<List<T>> listSupplier)
    {
        this(listSupplier, DEFAULT_FETCH_SIZE);
    }

    /**
     * Erstellt ein neues {@link ResultSetSubscriberForFetchSize} Object.
     *
     * @param listSupplier {@link Supplier}; default ArrayList::new
     * @param fetchSize int
     */
    public ResultSetSubscriberForFetchSize(final Supplier<List<T>> listSupplier, final int fetchSize)
    {
        super();

        this.data = listSupplier.get();
        this.fetchSize = fetchSize;
        this.completed = new AtomicBoolean(false);
        this.counter = new AtomicInteger(0);
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

        this.completed.set(true);
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
