/**
 * Created: 10.06.2019
 */

package de.freese.base.core.reactive;

import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Thomas Freese
 * @param <T> Entity-Type
 */
class StreamSubscription<T> implements Subscription
{
    /**
     *
     */
    private final AtomicLong demand = new AtomicLong();

    /**
     *
     */
    private final AtomicReference<Throwable> error = new AtomicReference<>();

    /**
     *
     */
    private final Executor executor;

    /**
     *
     */
    private final AtomicBoolean isTerminated = new AtomicBoolean(false);

    /**
     *
     */
    private final Iterator<? extends T> iterator;

    /**
     *
     */
    private final Subscriber<? super T> subscriber;

    /**
     * Erzeugt eine neue Instanz von {@link StreamSubscription}.
     *
     * @param executor {@link Executor}
     * @param iterator {@link Iterator}
     * @param subscriber {@link Subscriber}
     */
    StreamSubscription(final Executor executor, final Iterator<? extends T> iterator, final Subscriber<? super T> subscriber)
    {
        super();

        this.executor = Objects.requireNonNull(executor, "executor required");
        this.iterator = Objects.requireNonNull(iterator, "iterator required");
        this.subscriber = Objects.requireNonNull(subscriber, "subscriber required");
    }

    /**
     * @see java.util.concurrent.Flow.Subscription#cancel()
     */
    @Override
    public void cancel()
    {
        terminate();
    }

    /**
     *
     */
    void doOnSubscribed()
    {
        Throwable throwable = this.error.get();

        if ((throwable != null) && !terminate())
        {
            getExecutor().execute(() -> this.subscriber.onError(throwable));
        }
    }

    /**
     * @return {@link Executor}
     */
    private Executor getExecutor()
    {
        return this.executor;
    }

    /**
     * @return boolean
     */
    private boolean isTerminated()
    {
        return this.isTerminated.get();
    }

    /**
     * @see java.util.concurrent.Flow.Subscription#request(long)
     */
    @Override
    public void request(final long n)
    {
        if ((n <= 0) && !terminate())
        {
            getExecutor().execute(() -> this.subscriber.onError(new IllegalArgumentException("negative subscription request")));

            return;
        }

        for (;;)
        {
            long currentDemand = this.demand.getAcquire(); // >= Java9
            // long currentDemand = this.demand.get(); // <= Java8

            if (currentDemand == Long.MAX_VALUE)
            {
                return;
            }

            long adjustedDemand = currentDemand + n;

            if (adjustedDemand < 0L)
            {
                adjustedDemand = Long.MAX_VALUE;
            }

            if (this.demand.compareAndSet(currentDemand, adjustedDemand))
            {
                if (currentDemand > 0)
                {
                    return;
                }

                break;
            }
        }

        for (; (this.demand.get() > 0) && this.iterator.hasNext() && !isTerminated(); this.demand.decrementAndGet())
        {
            try
            {
                getExecutor().execute(() -> this.subscriber.onNext(this.iterator.next()));
            }
            catch (Throwable e)
            {
                if (!terminate())
                {
                    getExecutor().execute(() -> this.subscriber.onError(e));
                }
            }
        }

        if (!this.iterator.hasNext() && !terminate())
        {
            getExecutor().execute(() -> this.subscriber.onComplete());
        }
    }

    /**
     * @return boolean
     */
    private boolean terminate()
    {
        return this.isTerminated.getAndSet(true);
    }
}