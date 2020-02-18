/**
 * Created: 29.04.2019
 */

package de.freese.base.core.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ExecutorConfigurationSupport;

/**
 * Utils für {@link ExecutorService}.
 *
 * @see ExecutorConfigurationSupport#shutdown()
 * @author Thomas Freese
 */
public final class ExecutorUtils
{
    /**
     * Wartet auf das Beenden des {@link ExecutorService}, falls awaitTerminationSeconds > 0 ist.
     *
     * @param executorService {@link ExecutorService}
     * @param awaitTerminationSeconds long
     * @param logger {@link Logger}
     */
    private static void awaitTerminationIfNecessary(final ExecutorService executorService, final long awaitTerminationSeconds, final Logger logger)
    {
        logger.info("shutdown ExecutorService");

        if (executorService == null)
        {
            return;
        }

        if (awaitTerminationSeconds <= 0)
        {
            return;
        }

        try
        {
            if (!executorService.awaitTermination(awaitTerminationSeconds, TimeUnit.SECONDS))
            {
                if (logger.isWarnEnabled())
                {
                    logger.warn("Timed out while waiting for executorService");
                }

                // Cancel currently executing tasks.
                for (Runnable remainingTask : executorService.shutdownNow())
                {
                    if (remainingTask instanceof Future)
                    {
                        ((Future<?>) remainingTask).cancel(true);
                    }
                }

                // Wait a while for tasks to respond to being cancelled.
                if (!executorService.awaitTermination(awaitTerminationSeconds, TimeUnit.SECONDS))
                {
                    logger.error("Pool did not terminate");
                }
            }
        }
        catch (InterruptedException iex)
        {
            if (logger.isWarnEnabled())
            {
                logger.warn("Interrupted while waiting for executorService");
            }

            // (Re-)Cancel if current thread also interrupted.
            executorService.shutdownNow();

            // Preserve interrupt status.
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Beendet den Task, wenn möglich.
     *
     * @param task {@link Runnable}
     */
    private static void cancelRemainingTask(final Runnable task)
    {
        if (task instanceof Future)
        {
            ((Future<?>) task).cancel(true);
        }
    }

    /**
     * Beendet den Executor.<br>
     * Analog {@link ExecutorConfigurationSupport#shutdown()}.
     *
     * @param executorService {@link ExecutorService}
     */
    public static void shutdown(final ExecutorService executorService)
    {
        shutdown(executorService, true, 3, LoggerFactory.getLogger(ExecutorUtils.class));
    }

    /**
     * Beendet den Executor.
     *
     * @param executorService {@link ExecutorService}
     * @param waitForTasksToCompleteOnShutdown boolean
     * @param awaitTerminationSeconds long
     * @param logger {@link Logger}
     */
    public static void shutdown(final ExecutorService executorService, final boolean waitForTasksToCompleteOnShutdown, final long awaitTerminationSeconds,
                                final Logger logger)
    {
        if (executorService == null)
        {
            return;
        }

        logger.info("shutdown ExecutorService");

        if (waitForTasksToCompleteOnShutdown)
        {
            executorService.shutdown();
        }
        else
        {
            // Cancel currently executing tasks.
            for (Runnable remainingTask : executorService.shutdownNow())
            {
                cancelRemainingTask(remainingTask);
            }
        }

        awaitTerminationIfNecessary(executorService, awaitTerminationSeconds, logger);
    }

    /**
     * Erstellt ein neues {@link ExecutorUtils} Object.
     */
    private ExecutorUtils()
    {
        super();
    }
}
