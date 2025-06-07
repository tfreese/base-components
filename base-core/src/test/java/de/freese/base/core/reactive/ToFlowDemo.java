// Created: 25 Juli 2024
package de.freese.base.core.reactive;

import java.util.Spliterator;
import java.util.concurrent.Flow;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;

/**
 * @author Thomas Freese
 */
public final class ToFlowDemo {
    private static final Logger LOGGER = LoggerFactory.getLogger(ToFlowDemo.class);

    private static final class ValueSupplier implements Supplier<String> {
        private final AtomicInteger atomicInteger = new AtomicInteger(0);

        @Override
        public String get() {
            if (atomicInteger.get() < 4) {
                return Integer.toString(atomicInteger.incrementAndGet());
            }

            return null;
        }
    }

    public static void main(final String[] args) {
        try (Stream<String> stream = toStream(new ValueSupplier())) {
            stream.forEach(value -> LOGGER.info("stream: {}", value));
        }

        toFlux(new ValueSupplier()).subscribe(value -> LOGGER.info("flux: {}", value)).dispose();

        final Flow.Subscriber<String> subscriber = new Flow.Subscriber<>() {
            private Flow.Subscription subscription;

            @Override
            public void onComplete() {
                // Close Resources.
                LOGGER.info("subscriber: onComplete");
            }

            @Override
            public void onError(final Throwable throwable) {
                LOGGER.error(throwable.getMessage(), throwable);
            }

            @Override
            public void onNext(final String item) {
                LOGGER.info("subscriber: {}", item);

                // Nächstes Element anfordern.
                subscription.request(1);
            }

            @Override
            public void onSubscribe(final Flow.Subscription subscription) {
                this.subscription = subscription;

                // Request first Element.
                subscription.request(1);

                // Request all Elements.
                // subscription.request(Long.MAX_VALUE);
            }
        };
        toPublisher(new ValueSupplier()).subscribe(subscriber);
    }

    private static <T> Flux<T> toFlux(final Supplier<T> supplier) {
        return Flux.generate((final SynchronousSink<T> sink) -> {
                    final T value = supplier.get();

                    if (value != null) {
                        sink.next(value);
                    }
                    else {
                        sink.complete();
                    }
                })
                .doFinally(state -> LOGGER.info("flux: doFinally"));
    }

    private static <T> Flow.Publisher<T> toPublisher(final Supplier<T> supplier) {
        return subscriber -> {
            final Flow.Subscription subscription = new Flow.Subscription() {
                @Override
                public void cancel() {
                    // Close Resources.
                    LOGGER.info("subscription: cancel");
                }

                @Override
                public void request(final long n) {
                    for (int i = 0; i < n; i++) {
                        final T value = supplier.get();

                        if (value != null) {
                            subscriber.onNext(value);
                        }
                        else {
                            // Close Resources.
                            subscriber.onComplete();
                            break;
                        }
                    }
                }
            };

            subscriber.onSubscribe(subscription);
        };
    }

    private static <T> Stream<T> toStream(final Supplier<T> supplier) {
        final Spliterator<T> spliterator = new Spliterator<>() {
            @Override
            public int characteristics() {
                return Spliterator.ORDERED;
            }

            @Override
            public long estimateSize() {
                return Long.MAX_VALUE;
            }

            @Override
            public boolean tryAdvance(final Consumer<? super T> action) {
                final T value = supplier.get();

                if (value != null) {
                    action.accept(value);

                    return true;
                }

                return false;
            }

            @Override
            public Spliterator<T> trySplit() {
                return null;
            }
        };

        return StreamSupport.stream(spliterator, false)
                .onClose(() -> LOGGER.info("stream: onClose"));
    }

    private ToFlowDemo() {
        super();
    }
}
