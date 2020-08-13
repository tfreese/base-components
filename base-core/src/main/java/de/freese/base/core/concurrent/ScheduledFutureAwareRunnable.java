/**
 * Created: 12.10.2016
 */

package de.freese.base.core.concurrent;

import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.function.BooleanSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Runnable} mit der Referenz des {@link ScheduledFuture} um sich selbst zu beenden.<br>
 * Beispiel:
 *
 * <pre>
 * BooleanSupplier exitCondition = getWork()::isFinished;
 * Runnable task = () -> {
 *     if (exitCondition.getAsBoolean())
 *     {
 *         getWork().stop();
 *     }
 * };
 * ScheduledFutureAwareRunnable futureAwareRunnable = new ScheduledFutureAwareRunnable(task, exitCondition);
 *
 * ScheduledFuture<?> scheduledFuture = getScheduledExecutorService().scheduleWithFixedDelay(futureAwareRunnable, initialDelay, delay, TimeUnit);
 * futureAwareRunnable.setScheduledFuture(scheduledFuture);
 * </pre>
 *
 * @author Thomas Freese
 */
public class ScheduledFutureAwareRunnable implements Runnable
{
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledFutureAwareRunnable.class);

    /**
    *
    */
    private final BooleanSupplier exitCondition;

    /**
     *
     */
    private final String name;

    /**
    *
    */
    private ScheduledFuture<?> scheduledFuture;

    /**
    *
    */
    private final Runnable task;

    /**
     * Erstellt ein neues {@link ScheduledFutureAwareRunnable} Object.
     *
     * @param task {@link Runnable}
     * @param exitCondition {@link BooleanSupplier}
     */
    public ScheduledFutureAwareRunnable(final Runnable task, final BooleanSupplier exitCondition)
    {
        this(task, exitCondition, null);
    }

    /**
     * Erstellt ein neues {@link ScheduledFutureAwareRunnable} Object.
     *
     * @param task {@link Runnable}
     * @param exitCondition {@link BooleanSupplier}
     * @param name String; Optional
     */
    public ScheduledFutureAwareRunnable(final Runnable task, final BooleanSupplier exitCondition, final String name)
    {
        super();

        this.task = Objects.requireNonNull(task, "task required");
        this.exitCondition = Objects.requireNonNull(exitCondition, "exitCondition required");
        this.name = name;
    }

    /**
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run()
    {
        // System.out.println("BallView.ScheduledFutureAwareRunnable.run(): " + new Date());

        this.task.run();

        if (this.exitCondition.getAsBoolean())
        {
            LOGGER.info("{}: exit", Objects.toString(this.name, toString()));

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

    /**
     * @param scheduledFuture {@link ScheduledFuture}
     */
    public void setScheduledFuture(final ScheduledFuture<?> scheduledFuture)
    {
        this.scheduledFuture = scheduledFuture;

        // notify();
    }
}
