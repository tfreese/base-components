// Created: 08.02.2022
package de.freese.base.core.concurrent;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;

/**
 * {@link Executor} der nur eine begrenzte Anzahl von Threads des Delegates verwendet.<br>
 * Verwendet werden parallelism + 1 Threads, da ein Thread für die Queue-Verarbeitung benötigt wird.
 *
 * @author Thomas Freese
 */
public class BoundedExecutorQueuedWithScheduler implements Executor
{
    /**
     *
     */
    private static final Runnable SHUTDOWN_RUNNABLE = () ->
    {
    };

    /**
     * @author Thomas Freese
     */
    private class QueueScheduler implements Runnable
    {
        /**
         * @see java.lang.Thread#run()
         */
        @Override
        public void run()
        {
            while (!Thread.interrupted())
            {
                try
                {
                    Runnable runnable = BoundedExecutorQueuedWithScheduler.this.queue.take();

                    if (runnable == SHUTDOWN_RUNNABLE)
                    {
                        break;
                    }

                    schedule(runnable);
                }
                catch (InterruptedException ex)
                {
                    // Restore interrupted state.
                    Thread.currentThread().interrupt();
                }
            }
        }

        /**
         * @param runnable {@link Runnable}
         */
        private void schedule(final Runnable runnable)
        {
            try
            {
                BoundedExecutorQueuedWithScheduler.this.rateLimiter.acquire();

                BoundedExecutorQueuedWithScheduler.this.delegate.execute(() ->
                {
                    try
                    {
                        runnable.run();
                    }
                    finally
                    {
                        BoundedExecutorQueuedWithScheduler.this.rateLimiter.release();
                    }
                });
            }
            catch (RejectedExecutionException ex)
            {
                BoundedExecutorQueuedWithScheduler.this.rateLimiter.release();

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
    /**
     *
     */
    private final Executor delegate;
    /**
     *
     */
    private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    /**
     *
     */
    private final Semaphore rateLimiter;

    /**
     * Erstellt ein neues {@link BoundedExecutorQueuedWithScheduler} Object.
     *
     * @param delegate {@link Executor}
     * @param parallelism int; Anzahl zu nutzender Threads des Delegates
     */
    public BoundedExecutorQueuedWithScheduler(final Executor delegate, final int parallelism)
    {
        super();

        this.delegate = Objects.requireNonNull(delegate, "delegate required");

        if (parallelism < 1)
        {
            throw new IllegalArgumentException("parallelism < 1: " + parallelism);
        }

        this.rateLimiter = new Semaphore(parallelism, true);

        delegate.execute(new QueueScheduler());
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

        this.queue.add(runnable);
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
    public void shutdown()
    {
        execute(SHUTDOWN_RUNNABLE);
    }
}
