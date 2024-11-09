// Created: 16.01.2018
package de.freese.base.core.reactive;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <a href="https://medium.com/@olehdokuka/mastering-own-reactive-streams-implementation-part-1-publisher-e8eaf928a78c">mastering-own-reactive-streams</a>
 *
 * @author Thomas Freese
 */
@Execution(ExecutionMode.CONCURRENT)
class TestStreamPublisher {
    static final Executor EXECUTOR = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    static final Supplier<Stream<? extends Integer>> STREAM_SUPPLIER = () -> Stream.of(1, 2, 3, 4, 5, 6);
    private static final Logger LOGGER = LoggerFactory.getLogger(TestStreamPublisher.class);

    /**
     * @author Thomas Freese
     */
    static class MyTestSubscriber<T> implements Subscriber<T> {
        private Subscription subscription;

        @Override
        public void onComplete() {
            LOGGER.info("{}: {}#onComplete", Thread.currentThread().getName(), getClass().getSimpleName());
        }

        @Override
        public void onError(final Throwable t) {
            LOGGER.info("{}: {}#onError", Thread.currentThread().getName(), getClass().getSimpleName());
        }

        @Override
        public void onNext(final T item) {
            LOGGER.info("{}: {}#onNext", Thread.currentThread().getName(), getClass().getSimpleName());

            this.subscription.request(1); // Nächstes Element anfordern.
        }

        @Override
        public void onSubscribe(final Subscription subscription) {
            LOGGER.info("{}: {}#onSubscribe", Thread.currentThread().getName(), getClass().getSimpleName());

            this.subscription = subscription;
            this.subscription.request(1); // Erstes Element anfordern.
            // subscription.request(Long.MAX_VALUE); // Alle Elemente anfordern.
        }
    }

    /**
     * Processor = Subscriber + Publisher
     *
     * @param <T> Typ des Eingangs
     * @param <R> Typ des Ausgangs
     *
     * @author Thomas Freese
     */
    static class MyTransformProcessor<T, R> extends SubmissionPublisher<R> implements Processor<T, R> {
        private final Function<T, R> function;

        private Subscription subscription;

        MyTransformProcessor(final Executor executor, final Function<T, R> function) {
            super(executor, Flow.defaultBufferSize());

            this.function = function;
        }

        MyTransformProcessor(final Function<T, R> function) {
            super();

            this.function = function;
        }

        @Override
        public void onComplete() {
            LOGGER.info("{}: {}#onComplete", Thread.currentThread().getName(), getClass().getSimpleName());

            close();
        }

        @Override
        public void onError(final Throwable throwable) {
            LOGGER.info("{}: {}#onError", Thread.currentThread().getName(), getClass().getSimpleName());

            closeExceptionally(throwable);
        }

        @Override
        public void onNext(final T item) {
            LOGGER.info("{}: {}#onNext", Thread.currentThread().getName(), getClass().getSimpleName());

            submit(this.function.apply(item)); // Dieses Element verarbeiten.
            this.subscription.request(1); // Nächstes Element anfordern.
        }

        @Override
        public void onSubscribe(final Subscription subscription) {
            LOGGER.info("{}: {}#onSubscribe", Thread.currentThread().getName(), getClass().getSimpleName());

            this.subscription = subscription;
            this.subscription.request(1); // Erstes Element anfordern.
            // subscription.request(Long.MAX_VALUE); // Alle Elemente anfordern
        }
    }

    @AfterAll
    static void afterAll() {
        await().pollDelay(Duration.ofMillis(500)).until(() -> true);

        if (EXECUTOR instanceof ExecutorService) {
            ((ExecutorService) EXECUTOR).shutdown();
        }
    }

    @Test
    void testStreamPublisherToSubscriber() {
        final Publisher<Integer> publisher = new StreamPublisher<>(EXECUTOR, STREAM_SUPPLIER);
        // Publisher<Integer> publisher = new StreamPublisher<>(streamSupplier);
        publisher.subscribe(new MyTestSubscriber<>());

        assertTrue(true);
    }

    @Test
    void testSubmissionPublisherToProcessorToSubscriber() {
        // try (SubmissionPublisher<Integer> publisher = new SubmissionPublisher<>(EXECUTOR, Flow.defaultBufferSize()))
        try (SubmissionPublisher<Integer> publisher = new SubmissionPublisher<>()) {
            // close-Methode in MyTransformProcessor#onComplete.
            final var transformProcessor = new MyTransformProcessor<Integer, String>(EXECUTOR, i -> "-" + i + "-");
            // var transformProcessor = new MyTransformProcessor<Integer, String>(i -> "-" + Integer.toString(i) + "-");
            publisher.subscribe(transformProcessor);

            transformProcessor.subscribe(new MyTestSubscriber<>());

            STREAM_SUPPLIER.get().forEach(publisher::submit);

            // publisher.close();
        }

        assertTrue(true);
    }

    @Test
    void testSubmissionPublisherToSubscriber() {
        try (SubmissionPublisher<Integer> publisher = new SubmissionPublisher<>(EXECUTOR, Flow.defaultBufferSize()))
        // try (SubmissionPublisher<Integer> publisher = new SubmissionPublisher<>())
        {
            publisher.subscribe(new MyTestSubscriber<>());

            STREAM_SUPPLIER.get().forEach(publisher::submit);

            // publisher.close();
        }

        assertTrue(true);
    }
}
