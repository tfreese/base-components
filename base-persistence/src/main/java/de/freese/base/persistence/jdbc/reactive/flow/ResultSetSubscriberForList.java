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
 * @author Thomas Freese
 * @param <T> Entity-Type
 */
public class ResultSetSubscriberForList<T> implements Subscriber<T>
{
    /**
    *
    */
    private static final Logger LOGGER = LoggerFactory.getLogger(ResultSetSubscriberForList.class);

    /**
     *
     */
    private final List<T> rows;

    /**
     * Erstellt ein neues {@link ResultSetSubscriberForList} Object.
     */
    public ResultSetSubscriberForList()
    {
        this(ArrayList::new);
    }

    /**
     * Erstellt ein neues {@link ResultSetSubscriberForList} Object.
     *
     * @param listSupplier {@link Supplier}; default ArrayList::new
     */
    public ResultSetSubscriberForList(final Supplier<List<T>> listSupplier)
    {
        super();

        this.rows = listSupplier.get();
    }

    /**
     * @return {@link List}<T>
     */
    public List<T> getRows()
    {
        return this.rows;
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
    }

    /**
     * @see de.freese.base.persistence.jdbc.reactive.flow.ResultSetSubscriberForEachRow#onNext(java.lang.Object)
     */
    @Override
    public void onNext(final T item)
    {
        this.rows.add(item);
    }

    /**
     * @see de.freese.base.persistence.jdbc.reactive.flow.ResultSetSubscriberForEachRow#onSubscribe(java.util.concurrent.Flow.Subscription)
     */
    @Override
    public void onSubscribe(final Subscription subscription)
    {
        subscription.request(Long.MAX_VALUE); // Alle Elemente anfordern.
    }
}
