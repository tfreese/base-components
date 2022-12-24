// Created: 12.09.2021
package de.freese.base.core.concurrent;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;

/**
 * {@link Executor} who is using only n Threads from the Delegate.<br>
 *
 * @author Thomas Freese
 */
public class BoundedExecutor implements Executor
{
    private final Executor delegate;

    private final Semaphore rateLimiter;

    /**
     * @param parallelism int; Number of Threads to use from the Delegate
     */
    public BoundedExecutor(final Executor delegate, final int parallelism)
    {
        super();

        this.delegate = Objects.requireNonNull(delegate, "delegate required");

        if (parallelism < 1)
        {
            throw new IllegalArgumentException("parallelism < 1: " + parallelism);
        }

        this.rateLimiter = new Semaphore(parallelism, true);
    }

    /**
     * @see java.util.concurrent.Executor#execute(java.lang.Runnable)
     */
    @Override
    public void execute(final Runnable runnable)
    {
        if (runnable == null)
        {
            throw new NullPointerException();
        }

        try
        {
            this.rateLimiter.acquire();

            this.delegate.execute(() ->
            {
                try
                {
                    runnable.run();
                }
                finally
                {
                    this.rateLimiter.release();
                }
            });
        }
        catch (RejectedExecutionException ex)
        {
            this.rateLimiter.release();

            throw ex;
        }
        catch (RuntimeException ex)
        {
            throw ex;
        }
        catch (InterruptedException ex)
        {
            // Restore interrupted state.
            Thread.currentThread().interrupt();
        }
    }
}
