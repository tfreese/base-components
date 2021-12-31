// Created: 12.09.2021
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
 * {@link Executor} der nur eine begrenzete Anzahl von Threads des Delegates verwendet.<br>
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
     * Blockiert bis der RateLimiter freie Slots hat.
     *
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
     * Blockiert bis der RateLimiter freie Slots hat.
     *
     * @see java.util.concurrent.Executor#execute(java.lang.Runnable)
     */
    @Override
    public void execute(final Runnable runnable)
    {
        if (runnable == null)
        {
            throw new NullPointerException();
        }

        this.queue.add(runnable);

        // Verarbeitung anschubsen, wenn freie Slots vorhanden sind.
        for (int i = 0; i < this.rateLimiter.availablePermits(); i++)
        {
            // System.err.println("scheduleNext");
            scheduleNext();
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
    *
    */
    private void scheduleNext()
    {
        Runnable runnable = this.queue.poll();

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
                    scheduleNext();
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
