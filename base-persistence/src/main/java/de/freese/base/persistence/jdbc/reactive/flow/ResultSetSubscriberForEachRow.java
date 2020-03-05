/**
 * Created: 10.06.2019
 */

package de.freese.base.persistence.jdbc.reactive.flow;

import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 * @param <T> Entity-Type
 */
public class ResultSetSubscriberForEachRow<T> implements Subscriber<T>
{
    /**
    *
    */
    private static final Logger LOGGER = LoggerFactory.getLogger(ResultSetSubscriberForEachRow.class);

    /**
    *
    */
    private Subscription subscription = null;

    /**
     * Erstellt ein neues {@link ResultSetSubscriberForEachRow} Object.
     */
    public ResultSetSubscriberForEachRow()
    {
        super();
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
     * @see java.util.concurrent.Flow.Subscriber#onNext(java.lang.Object)
     */
    @Override
    public void onNext(final T item)
    {
        LOGGER.debug("onNext: {}", item);

        this.subscription.request(1); // Nächstes Element anfordern.
    }

    /**
     * @see java.util.concurrent.Flow.Subscriber#onSubscribe(java.util.concurrent.Flow.Subscription)
     */
    @Override
    public void onSubscribe(final Subscription subscription)
    {
        this.subscription = subscription;
        this.subscription.request(1); // Erstes Element anfordern.
    }
}
