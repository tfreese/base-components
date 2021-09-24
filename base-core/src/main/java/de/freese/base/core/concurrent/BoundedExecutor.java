// Created: 12.09.2021
package de.freese.base.core.concurrent;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;

/**
 * {@link Executor} der nur eine begrenzete Anzahl von Threads des Delegates verwendet.
 *
 * @author Thomas Freese
 */
public class BoundedExecutor implements Executor
{
    /**
     *
     */
    private final Executor delegate;
    /**
     *
     */
    private final Semaphore rateLimiter;

    /**
     * Erstellt ein neues {@link BoundedExecutor} Object.
     *
     * @param delegate {@link Executor}
     * @param parallelism int
     */
    public BoundedExecutor(final Executor delegate, final int parallelism)
    {
        super();

        this.delegate = Objects.requireNonNull(delegate, "executor required");

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
    public void execute(final Runnable command)
    {
        if (command == null)
        {
            throw new NullPointerException();
        }

        try
        {
            this.rateLimiter.acquire();

            this.delegate.execute(() -> {
                try
                {
                    command.run();
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
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }
}
