// Created: 16.01.2018
package de.freese.base.core.reactive;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Kapselt einen {@link Stream} in einem {@link Publisher}.<br>
 * Siehe: StreamPublisherTest.
 *
 * @see <a href= "https://medium.com/@olehdokuka/mastering-own-reactive-streams-implementation-part-1-publisher-e8eaf928a78c">mastering-own-reactive-streams</a>
 * @author Thomas Freese
 * @param <T> Entity-Type
 */
public class StreamPublisher<T> implements Publisher<T>
{
    /**
    *
    */
    private final Executor executor;

    /**
     *
     */
    private final Supplier<Stream<? extends T>> streamSupplier;

    /**
     * Erzeugt eine neue Instanz von {@link StreamPublisher}.
     *
     * @param executor {@link Executor}
     * @param streamSupplier {@link Supplier}
     */
    public StreamPublisher(final Executor executor, final Supplier<Stream<? extends T>> streamSupplier)
    {
        super();

        this.executor = Objects.requireNonNull(executor, "executor required");
        this.streamSupplier = Objects.requireNonNull(streamSupplier, "streamSupplier required");
    }

    /**
     * Erzeugt eine neue Instanz von {@link StreamPublisher}.
     *
     * @param streamSupplier {@link Supplier}
     */
    public StreamPublisher(final Supplier<Stream<? extends T>> streamSupplier)
    {
        this(ForkJoinPool.commonPool(), streamSupplier);
    }

    /**
     * @return {@link Executor}
     */
    private Executor getExecutor()
    {
        return this.executor;
    }

    /**
     * @see java.util.concurrent.Flow.Publisher#subscribe(java.util.concurrent.Flow.Subscriber)
     */
    @Override
    public void subscribe(final Subscriber<? super T> subscriber)
    {
        StreamSubscription<T> subscription = new StreamSubscription<>(getExecutor(), this.streamSupplier.get().iterator(), subscriber);

        getExecutor().execute(() -> {
            subscriber.onSubscribe(subscription);
            subscription.doOnSubscribed();
        });
    }
}
