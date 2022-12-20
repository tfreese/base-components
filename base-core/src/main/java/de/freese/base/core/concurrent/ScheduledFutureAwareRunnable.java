// Created: 12.10.2016
package de.freese.base.core.concurrent;

import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.function.BooleanSupplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Runnable} with a Reference of {@link ScheduledFuture} to shut down itself.<br>
 * Beispiel:
 *
 * <pre>
 * BooleanSupplier exitCondition = getWork()::isFinished;
 * Runnable task = () -> getWork()::stop;
 *
 * ScheduledFutureAwareRunnable futureAwareRunnable = new ScheduledFutureAwareRunnable(exitCondition, task);
 *
 * ScheduledFuture<?> scheduledFuture = getScheduledExecutorService().scheduleWithFixedDelay(futureAwareRunnable, initialDelay, delay, TimeUnit);
 * futureAwareRunnable.setScheduledFuture(scheduledFuture);
 * </pre>
 *
 * @author Thomas Freese
 */
public class ScheduledFutureAwareRunnable implements Runnable
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledFutureAwareRunnable.class);

    private final BooleanSupplier exitCondition;

    private final String name;
    private final Runnable task;
    private ScheduledFuture<?> scheduledFuture;

    public ScheduledFutureAwareRunnable(final BooleanSupplier exitCondition, final Runnable task)
    {
        this(exitCondition, task, null);
    }

    /**
     * @param name String; Optional
     */
    public ScheduledFutureAwareRunnable(final BooleanSupplier exitCondition, final Runnable task, final String name)
    {
        super();

        this.exitCondition = Objects.requireNonNull(exitCondition, "exitCondition required");
        this.task = Objects.requireNonNull(task, "task required");
        this.name = name;
    }

    /**
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run()
    {
        if (this.exitCondition.getAsBoolean())
        {
            LOGGER.info("{}: exit", Objects.toString(this.name, toString()));

            this.task.run();

            if (this.scheduledFuture != null)
            {
                this.scheduledFuture.cancel(false);
            }
            else
            {
                LOGGER.warn("{}: no ScheduledFuture reference", Objects.toString(this.name, toString()));
            }
        }
    }

    public void setScheduledFuture(final ScheduledFuture<?> scheduledFuture)
    {
        this.scheduledFuture = scheduledFuture;
    }
}
