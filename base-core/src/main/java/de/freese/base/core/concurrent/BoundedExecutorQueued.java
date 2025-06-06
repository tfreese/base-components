// Created: 08.02.2022
package de.freese.base.core.concurrent;

import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;

/**
 * {@link Executor} who is using only n Threads from the Delegates.<br>
 *
 * @author Thomas Freese
 */
public class BoundedExecutorQueued implements Executor {
    private final Executor delegate;
    private final Queue<Runnable> queue = new ArrayDeque<>();
    private final Semaphore rateLimiter;

    /**
     * @param parallelism int; Number of Threads to use from the Delegate
     */
    public BoundedExecutorQueued(final Executor delegate, final int parallelism) {
        super();

        this.delegate = Objects.requireNonNull(delegate, "delegate required");

        if (parallelism < 1) {
            throw new IllegalArgumentException("parallelism < 1: " + parallelism);
        }

        this.rateLimiter = new Semaphore(parallelism, true);
    }

    @Override
    public void execute(final Runnable runnable) {
        if (runnable == null) {
            throw new NullPointerException();
        }

        if (rateLimiter.availablePermits() > 0) {
            // Are Slots available, than execute.
            schedule(runnable);
        }
        else {
            // Park in the Queue.
            queue.add(runnable);
        }
    }

    public int getQueueSize() {
        return queue.size();
    }

    private void schedule(final Runnable runnable) {
        if (runnable == null) {
            return;
        }

        try {
            rateLimiter.acquire();

            delegate.execute(() -> {
                try {
                    runnable.run();
                }
                finally {
                    rateLimiter.release();
                    schedule(queue.poll());
                }
            });
        }
        catch (RejectedExecutionException ex) {
            rateLimiter.release();

            throw ex;
        }
        catch (RuntimeException ex) {
            throw ex;
        }
        catch (InterruptedException ex) {
            // Restore interrupted state.
            Thread.currentThread().interrupt();
        }
    }
}
