// Created: 30.07.2021
package de.freese.base.core.concurrent.accumulative;

import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Scheduler;

/**
 * Replica of a {@link sun.swing.AccumulativeRunnable} by a {@link Flux}.
 *
 * @author Thomas Freese
 */
abstract class AbstractAccumulativeSink {
    private static final Duration DEFAULT_DURATION = Duration.ofMillis(250);

    public <T> Sinks.Many<T> createForList(final Consumer<List<T>> consumer) {
        Sinks.Many<T> sink = createSink();

        createForList(sink, consumer, DEFAULT_DURATION);

        return sink;
    }

    public <T> Sinks.Many<T> createForList(final Consumer<List<T>> consumer, final Duration duration) {
        Sinks.Many<T> sink = createSink();

        createForList(sink, consumer, duration);

        return sink;
    }

    public <T> void createForList(final Sinks.Many<T> sink, final Consumer<List<T>> consumer, final Duration duration) {
        sink.asFlux().buffer(duration).publishOn(getScheduler()).subscribe(consumer);
    }

    public <T> Sinks.Many<T> createForSingle(final Consumer<T> consumer) {
        Sinks.Many<T> sink = createSink();

        createForSingle(sink, consumer, DEFAULT_DURATION);

        return sink;
    }

    public <T> Sinks.Many<T> createForSingle(final Consumer<T> consumer, final Duration duration) {
        Sinks.Many<T> sink = createSink();

        createForSingle(sink, consumer, duration);

        return sink;
    }

    public <T> void createForSingle(final Sinks.Many<T> sink, final Consumer<T> consumer, final Duration duration) {
        sink.asFlux().sample(duration).publishOn(getScheduler()).subscribe(consumer);
    }

    public <T> Sinks.Many<T> createSink() {
        return Sinks.many().replay().latest();
    }

    protected abstract Scheduler getScheduler();
}
