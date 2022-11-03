package de.freese.base.core.concurrent.accumulative;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Zeitgesteuerter {@link AccumulativeRunnable}, der nach einer Zeitspanne die gesammelten Daten ausführt.
 *
 * @param <T> Type
 *
 * @author Thomas Freese
 */
public class AccumulativeRunnableScheduled<T> extends AccumulativeRunnable<T>
{
    private final Duration delay;

    private final ScheduledExecutorService scheduledExecutor;

    private Consumer<List<T>> submitConsumer = chunks ->
    {
    };

    /**
     * Erstellt ein neues {@link AccumulativeRunnableScheduled} Object.<br>
     * Ohne {@link ScheduledExecutorService} wird ein {@link Timer} verwendet.<br>
     * Default delay = 250 ms
     */
    public AccumulativeRunnableScheduled()
    {
        this(null, Duration.ofMillis(250));
    }

    /**
     * Erstellt ein neues {@link AccumulativeRunnableScheduled} Object.<br>
     * Default delay = 250 ms
     */
    public AccumulativeRunnableScheduled(final ScheduledExecutorService scheduledExecutor)
    {
        this(Objects.requireNonNull(scheduledExecutor, "scheduledExecutor required"), Duration.ofMillis(250));
    }

    /**
     * @param scheduledExecutor {@link ScheduledExecutorService}; optional
     */
    public AccumulativeRunnableScheduled(final ScheduledExecutorService scheduledExecutor, final Duration delay)
    {
        super();

        this.scheduledExecutor = scheduledExecutor;
        this.delay = Objects.requireNonNull(delay, "delay required");
    }

    public void doOnSubmit(final Consumer<List<T>> submitConsumer)
    {
        this.submitConsumer = Objects.requireNonNull(submitConsumer, "submitConsumer required");
    }

    /**
     * @see de.freese.base.core.concurrent.accumulative.AccumulativeRunnable#run(java.util.List)
     */
    @Override
    protected void run(final List<T> args)
    {
        this.submitConsumer.accept(args);
    }

    /**
     * @see de.freese.base.core.concurrent.accumulative.AccumulativeRunnable#submit()
     */
    @Override
    protected final void submit()
    {
        if (this.scheduledExecutor != null)
        {
            this.scheduledExecutor.schedule(() -> SwingUtilities.invokeLater(this), this.delay.toMillis(), TimeUnit.MILLISECONDS);
        }
        else
        {
            Timer timer = new Timer((int) this.delay.toMillis(), event -> SwingUtilities.invokeLater(this));
            timer.setRepeats(false);
            timer.start();
        }
    }
}
