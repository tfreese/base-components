// Created: 30.07.2021
package de.freese.base.core.concurrent.accumulative;

import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;
import reactor.core.scheduler.Scheduler;

/**
 * Nachbau eines {@link sun.swing.AccumulativeRunnable} durch einen {@link Flux}.
 *
 * @author Thomas Freese
 */
@SuppressWarnings("javadoc")
abstract class AbstractAccumulativeSink
{
    /**
     *
     */
    private static final Duration DEFAULT_DURATION = Duration.ofMillis(250);

    /**
     * @param <T> Type
     * @param consumer {@link Consumer}
     *
     * @return {@link Many}
     */
    public <T> Sinks.Many<T> createForList(final Consumer<List<T>> consumer)
    {
        Sinks.Many<T> sink = createSink();

        createForList(sink, consumer, DEFAULT_DURATION);

        return sink;
    }

    /**
     * @param <T> Type
     * @param consumer {@link Consumer}
     * @param duration {@link Duration}
     *
     * @return {@link Many}
     */
    public <T> Sinks.Many<T> createForList(final Consumer<List<T>> consumer, final Duration duration)
    {
        Sinks.Many<T> sink = createSink();

        createForList(sink, consumer, duration);

        return sink;
    }

    /**
     * @param <T> Type
     * @param sink {@link Many}
     * @param consumer {@link Consumer}
     * @param duration {@link Duration}
     */
    public <T> void createForList(final Sinks.Many<T> sink, final Consumer<List<T>> consumer, final Duration duration)
    {
        sink.asFlux().buffer(duration).publishOn(getScheduler()).subscribe(consumer);
    }

    /**
     * @param <T> Type
     * @param consumer {@link Consumer}
     *
     * @return {@link Many}
     */
    public <T> Sinks.Many<T> createForSingle(final Consumer<T> consumer)
    {
        Sinks.Many<T> sink = createSink();

        createForSingle(sink, consumer, DEFAULT_DURATION);

        return sink;
    }

    /**
     * @param <T> Type
     * @param consumer {@link Consumer}
     * @param duration {@link Duration}
     *
     * @return {@link Many}
     */
    public <T> Sinks.Many<T> createForSingle(final Consumer<T> consumer, final Duration duration)
    {
        Sinks.Many<T> sink = createSink();

        createForSingle(sink, consumer, duration);

        return sink;
    }

    /**
     * @param <T> Type
     * @param sink {@link Many}
     * @param consumer {@link Consumer}
     * @param duration {@link Duration}
     */
    public <T> void createForSingle(final Sinks.Many<T> sink, final Consumer<T> consumer, final Duration duration)
    {
        sink.asFlux().sample(duration).publishOn(getScheduler()).subscribe(consumer);
    }

    /**
     * @param <T> Type
     *
     * @return {@link Many}
     */
    public <T> Sinks.Many<T> createSink()
    {
        return Sinks.many().replay().latest();
    }

    /**
     * @return {@link Scheduler}
     */
    protected abstract Scheduler getScheduler();
}
