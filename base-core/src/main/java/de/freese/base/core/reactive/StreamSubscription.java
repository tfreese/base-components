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
            getExecutor().execute(() -> subscriber.onError(new IllegalArgumentException("negative subscription request")));

            return;
        }

        for (; ; ) {
            final long currentDemand = demand.getAcquire(); // >= Java9
            // final long currentDemand = demand.get(); // <= Java8

            if (currentDemand == Long.MAX_VALUE) {
                return;
            }

            long adjustedDemand = currentDemand + n;

            if (adjustedDemand < 0L) {
                adjustedDemand = Long.MAX_VALUE;
            }

            if (demand.compareAndSet(currentDemand, adjustedDemand)) {
                if (currentDemand > 0) {
                    return;
                }

                break;
            }
        }

        for (; demand.get() > 0 && iterator.hasNext() && !isTerminated(); demand.decrementAndGet()) {
            try {
                getExecutor().execute(() -> subscriber.onNext(iterator.next()));
            }
            catch (Exception ex) {
                if (!terminate()) {
                    getExecutor().execute(() -> subscriber.onError(ex));
                }
            }
        }

        if (!iterator.hasNext() && !terminate()) {
            getExecutor().execute(subscriber::onComplete);
        }
    }

    void doOnSubscribed() {
        final Throwable throwable = error.get();

        if (throwable != null && !terminate()) {
            getExecutor().execute(() -> subscriber.onError(throwable));
        }
    }

    private Executor getExecutor() {
        return executor;
    }

    private boolean isTerminated() {
        return isTerminated.get();
    }

    private boolean terminate() {
        return isTerminated.getAndSet(true);
    }
}
