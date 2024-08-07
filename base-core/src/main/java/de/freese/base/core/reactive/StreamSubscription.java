// Created: 10.06.2019
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
 */
class StreamSubscription<T> implements Subscription {
    private final AtomicLong demand = new AtomicLong();
    private final AtomicReference<Throwable> error = new AtomicReference<>();
    private final Executor executor;
    private final AtomicBoolean isTerminated = new AtomicBoolean(false);
    private final Iterator<? extends T> iterator;
    private final Subscriber<? super T> subscriber;

    StreamSubscription(final Executor executor, final Iterator<? extends T> iterator, final Subscriber<? super T> subscriber) {
        super();

        this.executor = Objects.requireNonNull(executor, "executor required");
        this.iterator = Objects.requireNonNull(iterator, "iterator required");
        this.subscriber = Objects.requireNonNull(subscriber, "subscriber required");
    }

    @Override
    public void cancel() {
        terminate();
    }

    @Override
    public void request(final long n) {
        if (n <= 0 && !terminate()) {
            getExecutor().execute(() -> this.subscriber.onError(new IllegalArgumentException("negative subscription request")));

            return;
        }

        for (; ; ) {
            final long currentDemand = this.demand.getAcquire(); // >= Java9
            // final long currentDemand = this.demand.get(); // <= Java8

            if (currentDemand == Long.MAX_VALUE) {
                return;
            }

            long adjustedDemand = currentDemand + n;

            if (adjustedDemand < 0L) {
                adjustedDemand = Long.MAX_VALUE;
            }

            if (this.demand.compareAndSet(currentDemand, adjustedDemand)) {
                if (currentDemand > 0) {
                    return;
                }

                break;
            }
        }

        for (; this.demand.get() > 0 && this.iterator.hasNext() && !isTerminated(); this.demand.decrementAndGet()) {
            try {
                getExecutor().execute(() -> this.subscriber.onNext(this.iterator.next()));
            }
            catch (Exception ex) {
                if (!terminate()) {
                    getExecutor().execute(() -> this.subscriber.onError(ex));
                }
            }
        }

        if (!this.iterator.hasNext() && !terminate()) {
            getExecutor().execute(this.subscriber::onComplete);
        }
    }

    void doOnSubscribed() {
        final Throwable throwable = this.error.get();

        if (throwable != null && !terminate()) {
            getExecutor().execute(() -> this.subscriber.onError(throwable));
        }
    }

    private Executor getExecutor() {
        return this.executor;
    }

    private boolean isTerminated() {
        return this.isTerminated.get();
    }

    private boolean terminate() {
        return this.isTerminated.getAndSet(true);
    }
}
