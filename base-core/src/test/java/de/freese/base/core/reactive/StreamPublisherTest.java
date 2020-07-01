// Created: 16.01.2018
package de.freese.base.core.reactive;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Processor;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * @see <a href="https://medium.com/@olehdokuka/mastering-own-reactive-streams-implementation-part-1-publisher-e8eaf928a78c">mastering-own-reactive-streams</a>
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
class StreamPublisherTest
{
    /**
     * @author Thomas Freese
     * @param <T> Typ der Entity
     */
    class MyTestSubscriber<T> implements Subscriber<T>
    {
        /**
         *
         */
        private Subscription subscription = null;

        /**
         * @see java.util.concurrent.Flow.Subscriber#onComplete()
         */
        @Override
        public void onComplete()
        {
            System.out.println(Thread.currentThread().getName() + ": " + getClass().getSimpleName() + "#onComplete");
        }

        /**
         * @see java.util.concurrent.Flow.Subscriber#onError(java.lang.Throwable)
         */
        @Override
        public void onError(final Throwable t)
        {
            System.out.println(Thread.currentThread().getName() + ": " + getClass().getSimpleName() + "#onError: " + t.getMessage());
        }

        /**
         * @see java.util.concurrent.Flow.Subscriber#onNext(java.lang.Object)
         */
        @Override
        public void onNext(final T item)
        {
            System.out.println(Thread.currentThread().getName() + ": " + getClass().getSimpleName() + "#onNext: " + item);

            this.subscription.request(1); // Nächstes Element anfordern.
        }

        /**
         * @see java.util.concurrent.Flow.Subscriber#onSubscribe(java.util.concurrent.Flow.Subscription)
         */
        @Override
        public void onSubscribe(final Subscription subscription)
        {
            System.out.println(Thread.currentThread().getName() + ": " + getClass().getSimpleName() + "#onSubscribe");

            this.subscription = subscription;
            this.subscription.request(1); // Erstes Element anfordern.
            // subscription.request(Long.MAX_VALUE); // Alle Elemente anfordern.
        }
    }

    /**
     * Processor = Subscriber + Publisher
     *
     * @author Thomas Freese
     * @param <T> Typ der Eingangs
     * @param <R> Typ des Ausgangs
     */
    class MyTransformProcessor<T, R> extends SubmissionPublisher<R> implements Processor<T, R>
    {
        /**
         *
         */
        private final Function<T, R> function;

        /**
         *
         */
        private Subscription subscription = null;

        /**
         * Erstellt ein neues {@link MyTransformProcessor} Object.
         *
         * @param executor {@link Executor}
         * @param function {@link Function}
         */
        MyTransformProcessor(final Executor executor, final Function<T, R> function)
        {
            super(executor, Flow.defaultBufferSize());

            this.function = function;
        }

        /**
         * Erstellt ein neues {@link MyTransformProcessor} Object.
         *
         * @param function {@link Function}
         */
        MyTransformProcessor(final Function<T, R> function)
        {
            super();

            this.function = function;
        }

        /**
         * @see java.util.concurrent.Flow.Subscriber#onComplete()
         */
        @Override
        public void onComplete()
        {
            System.out.println(Thread.currentThread().getName() + ": " + getClass().getSimpleName() + "#onComplete");

            close();
        }

        /**
         * @see java.util.concurrent.Flow.Subscriber#onError(java.lang.Throwable)
         */
        @Override
        public void onError(final Throwable throwable)
        {
            System.out.println(Thread.currentThread().getName() + ": " + getClass().getSimpleName() + "#onError: " + throwable.getMessage());

            closeExceptionally(throwable);
        }

        /**
         * @see java.util.concurrent.Flow.Subscriber#onNext(java.lang.Object)
         */
        @Override
        public void onNext(final T item)
        {
            System.out.println(Thread.currentThread().getName() + ": " + getClass().getSimpleName() + "#onSubscribe: " + item);

            submit(this.function.apply(item)); // Dieses Element verarbeiten.
            this.subscription.request(1); // Nächstes Element anfordern.
        }

        /**
         * @see java.util.concurrent.Flow.Subscriber#onSubscribe(java.util.concurrent.Flow.Subscription)
         */
        @Override
        public void onSubscribe(final Subscription subscription)
        {
            System.out.println(Thread.currentThread().getName() + ": " + getClass().getSimpleName() + "#onSubscribe");

            this.subscription = subscription;
            this.subscription.request(1); // Erstes Element anfordern.
            // subscription.request(Long.MAX_VALUE); // Alle Elemente anfordern
        }
    }

    /**
     *
     */
    final static Executor EXECUTOR = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    /**
     *
     */
    static final Supplier<Stream<? extends Integer>> STREAM_SUPPLIER = () -> Stream.of(1, 2, 3, 4, 5, 6);

    /**
     *
     */
    @AfterAll
    static void afterAll()
    {
        if (EXECUTOR instanceof ExecutorService)
        {
            ((ExecutorService) EXECUTOR).shutdown();
        }
    }

    /**
    *
    */
    @Test
    void test010StreamPublisherToSubscriber()
    {
        var streamSupplier = STREAM_SUPPLIER;

        Publisher<Integer> publisher = new StreamPublisher<>(EXECUTOR, streamSupplier);
        // Publisher<Integer> publisher = new StreamPublisher<>(streamSupplier);
        publisher.subscribe(new MyTestSubscriber<>());

        System.out.println();

        assertTrue(true);
    }

    /**
     *
     */
    @Test
    void test020SubmissionPublisherToSubscriber()
    {
        var streamSupplier = STREAM_SUPPLIER;

        try (SubmissionPublisher<Integer> publisher = new SubmissionPublisher<>(EXECUTOR, Flow.defaultBufferSize()))
        // try (SubmissionPublisher<Integer> publisher = new SubmissionPublisher<>())
        {
            publisher.subscribe(new MyTestSubscriber<>());

            streamSupplier.get().forEach(publisher::submit);

            // publisher.close();
        }

        System.out.println();

        assertTrue(true);
    }

    /**
    *
    */
    @Test
    void test030SubmissionPublisherToProcessorToSubscriber()
    {
        var streamSupplier = STREAM_SUPPLIER;

        // try (SubmissionPublisher<Integer> publisher = new SubmissionPublisher<>(EXECUTOR, Flow.defaultBufferSize()))
        try (SubmissionPublisher<Integer> publisher = new SubmissionPublisher<>())
        {
            @SuppressWarnings("resource")
            // close-Methode in MyTransformProcessor#onComplete.
            var transformProcessor = new MyTransformProcessor<Integer, String>(EXECUTOR, i -> "-" + Integer.toString(i) + "-");
            // var transformProcessor = new MyTransformProcessor<Integer, String>(i -> "-" + Integer.toString(i) + "-");
            publisher.subscribe(transformProcessor);

            transformProcessor.subscribe(new MyTestSubscriber<>());

            streamSupplier.get().forEach(publisher::submit);

            // publisher.close();
        }

        System.out.println();

        assertTrue(true);
    }
}
