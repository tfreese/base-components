package de.freese.base.core.concurrent;

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
 * @author Thomas Freese
 * @param <T> Type
 */
public class ScheduledAccumulativeRunnable<T> extends AccumulativeRunnable<T>
{
    /**
     * MilliSeconds
     */
    private final int delay;

    /**
     *
     */
    private final ScheduledExecutorService scheduledExecutor;

    /**
     *
     */
    private Consumer<List<T>> submitConsumer = chunks -> {
    };

    /**
     * Erstellt ein neues {@link ScheduledAccumulativeRunnable} Object.
     *
     * @param delay int, MilliSeconds
     */
    public ScheduledAccumulativeRunnable(final int delay)
    {
        this(delay, null);
    }

    /**
     * Erstellt ein neues {@link ScheduledAccumulativeRunnable} Object.
     *
     * @param delay int, MilliSeconds
     * @param scheduledExecutor {@link ScheduledExecutorService}; optional
     */
    public ScheduledAccumulativeRunnable(final int delay, final ScheduledExecutorService scheduledExecutor)
    {
        super();

        this.delay = delay;
        this.scheduledExecutor = scheduledExecutor;
    }

    /**
     * @param submitConsumer {@link Consumer}
     */
    public void doOnSubmit(final Consumer<List<T>> submitConsumer)
    {
        this.submitConsumer = Objects.requireNonNull(submitConsumer, "submitConsumer required");
    }

    /**
     * @see de.freese.base.core.concurrent.AccumulativeRunnable#run(java.util.List)
     */
    @Override
    protected void run(final List<T> args)
    {
        this.submitConsumer.accept(args);
    }

    /**
     * @see de.freese.base.core.concurrent.AccumulativeRunnable#submit()
     */
    @Override
    protected final void submit()
    {
        if (this.scheduledExecutor != null)
        {
            this.scheduledExecutor.schedule(() -> SwingUtilities.invokeLater(this), this.delay, TimeUnit.MILLISECONDS);

            // LoggerFactory.getLogger(getClass()).info("ActiveCount: {}", ((ScheduledThreadPoolExecutor) this.scheduledExecutor).getActiveCount());
            // LoggerFactory.getLogger(getClass()).info("PoolSize: {}", ((ScheduledThreadPoolExecutor) this.scheduledExecutor).getPoolSize());
        }
        else
        {
            Timer timer = new Timer(this.delay, event -> SwingUtilities.invokeLater(this));
            timer.setRepeats(false);
            timer.start();
        }
    }
}
