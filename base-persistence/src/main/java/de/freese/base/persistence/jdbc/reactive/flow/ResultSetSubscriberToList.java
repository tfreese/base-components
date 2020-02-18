/**
 * Created: 10.06.2019
 */

package de.freese.base.persistence.jdbc.reactive.flow;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Flow.Subscription;
import java.util.function.Supplier;

/**
 * @author Thomas Freese
 * @param <T> Entity-Type
 */
public class ResultSetSubscriberToList<T> extends ResultSetSubscriber<T>
{
    /**
     *
     */
    private final List<T> rows;

    /**
     * Erstellt ein neues {@link ResultSetSubscriberToList} Object.
     */
    public ResultSetSubscriberToList()
    {
        this(ArrayList::new);
    }

    /**
     * Erstellt ein neues {@link ResultSetSubscriberToList} Object.
     *
     * @param listSupplier {@link Supplier}; default ArrayList::new
     */
    public ResultSetSubscriberToList(final Supplier<List<T>> listSupplier)
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
     * @see de.freese.base.persistence.jdbc.reactive.flow.ResultSetSubscriber#onNext(java.lang.Object)
     */
    @Override
    public void onNext(final T item)
    {
        this.rows.add(item);
    }

    /**
     * @see de.freese.base.persistence.jdbc.reactive.flow.ResultSetSubscriber#onSubscribe(java.util.concurrent.Flow.Subscription)
     */
    @Override
    public void onSubscribe(final Subscription subscription)
    {
        setSubscription(subscription);
        getSubscription().request(Long.MAX_VALUE); // Alle Elemente anfordern.
    }
}
