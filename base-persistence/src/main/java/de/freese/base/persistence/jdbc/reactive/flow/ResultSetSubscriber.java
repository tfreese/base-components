/**
 * Created: 10.06.2019
 */

package de.freese.base.persistence.jdbc.reactive.flow;

import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

/**
 * @author Thomas Freese
 * @param <T> Entity-Type
 */
public class ResultSetSubscriber<T> implements Subscriber<T>
{
    /**
    *
    */
    private Subscription subscription = null;

    /**
     * Erstellt ein neues {@link ResultSetSubscriber} Object.
     */
    public ResultSetSubscriber()
    {
        super();
    }

    /**
     * @return {@link Subscription}
     */
    protected Subscription getSubscription()
    {
        return this.subscription;
    }

    /**
     * @see java.util.concurrent.Flow.Subscriber#onComplete()
     */
    @Override
    public void onComplete()
    {
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
        System.out.println("#onNext: overwrite me ! " + item.toString());
        getSubscription().request(1); // Nächstes Element anfordern.
    }

    /**
     * @see java.util.concurrent.Flow.Subscriber#onSubscribe(java.util.concurrent.Flow.Subscription)
     */
    @Override
    public void onSubscribe(final Subscription subscription)
    {
        this.subscription = subscription;
        this.subscription.request(1); // Erstes Element anfordern.
        // this.subscription.request(Long.MAX_VALUE); // Alle Elemente anfordern.
    }

    /**
     * @param subscription {@link Subscription}
     */
    protected void setSubscription(final Subscription subscription)
    {
        this.subscription = subscription;
    }
}
