// Created: 08.02.2022
package de.freese.base.core.concurrent;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;

/**
 * {@link Executor} who is using only n Threads from the Delegates.<br>
 * Uses parallelism + 1 Threads for the Queue-Processing.
 *
 * @author Thomas Freese
 */
public class BoundedExecutorQueuedWithScheduler implements Executor {
    private static final Runnable SHUTDOWN_RUNNABLE = () -> {
    };

    /**
     * @author Thomas Freese
     */
    private final class QueueScheduler implements Runnable {
        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    final Runnable runnable = BoundedExecutorQueuedWithScheduler.this.queue.take();

                    if (runnable == SHUTDOWN_RUNNABLE) {
                        break;
                    }

                    schedule(runnable);
                }
                catch (InterruptedException _) {
                    // Restore interrupted state.
                    Thread.currentThread().interrupt();
                }
            }
        }

        private void schedule(final Runnable runnable) {
            try {
                BoundedExecutorQueuedWithScheduler.this.rateLimiter.acquire();

                BoundedExecutorQueuedWithScheduler.this.delegate.execute(() -> {
                    try {
                        runnable.run();
                    }
                    finally {
                        BoundedExecutorQueuedWithScheduler.this.rateLimiter.release();
                    }
                });
            }
            catch (RejectedExecutionException ex) {
                BoundedExecutorQueuedWithScheduler.this.rateLimiter.release();

                throw ex;
            }
            catch (InterruptedException _) {
                // Restore interrupted state.
                Thread.currentThread().interrupt();
            }
        }
    }

    private final Executor delegate;
    private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    private final Semaphore rateLimiter;

    /**
     * @param parallelism int; Number of Threads to use from the Delegate
     */
    public BoundedExecutorQueuedWithScheduler(final Executor delegate, final int parallelism) {
        super();

        this.delegate = Objects.requireNonNull(delegate, "delegate required");

        if (parallelism < 1) {
            throw new IllegalArgumentException("parallelism < 1: " + parallelism);
        }

        rateLimiter = new Semaphore(parallelism, true);

        delegate.execute(new QueueScheduler());
    }

    @Override
    public void execute(final Runnable runnable) {
        if (runnable == null) {
            throw new NullPointerException();
        }

        queue.add(runnable);
    }

    public int getQueueSize() {
        return queue.size();
    }

    public void shutdown() {
        execute(SHUTDOWN_RUNNABLE);
    }
}
