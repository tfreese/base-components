// Created: 08.02.2022
package de.freese.base.core.concurrent;

import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.Semaphore;

/**
 * {@link Executor} der nur eine begrenzte Anzahl von Threads des Delegates verwendet.<br>
 *
 * @author Thomas Freese
 */
public class BoundedExecutorQueued implements Executor
{
    /**
     *
     */
    private final Executor delegate;
    /**
     *
     */
    private final Queue<Runnable> queue = new ArrayDeque<>();
    /**
     *
     */
    private final Semaphore rateLimiter;

    /**
     * Erstellt ein neues {@link BoundedExecutorQueued} Object.
     *
     * @param delegate {@link Executor}
     * @param parallelism int; Anzahl zu nutzender Threads des Delegates
     */
    public BoundedExecutorQueued(final Executor delegate, final int parallelism)
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
     * @param <T> Type
     * @param callable {@link Callable}
     *
     * @return {@link Future}
     */
    public <T> Future<T> execute(final Callable<T> callable)
    {
        if (callable == null)
        {
            throw new NullPointerException();
        }

        RunnableFuture<T> task = new FutureTask<>(callable);

        execute(task);

        return task;
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

        if (this.rateLimiter.availablePermits() > 0)
        {
            // Sind noch Slots frei -> direkt ausführen.
            schedule(runnable);
        }
        else
        {
            // In der Queue parken.
            this.queue.add(runnable);
        }
    }

    /**
     * @return int
     */
    public int getQueueSize()
    {
        return this.queue.size();
    }

    /**
     * @param runnable {@link Runnable}
     */
    private void schedule(final Runnable runnable)
    {
        if (runnable == null)
        {
            return;
        }

        try
        {
            this.rateLimiter.acquire();

            this.delegate.execute(() -> {
                try
                {
                    runnable.run();
                }
                finally
                {
                    this.rateLimiter.release();
                    schedule(this.queue.poll());
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
        // catch (Exception ex)
        // {
        // throw new RuntimeException(ex);
        // }
    }
}
