package de.freese.base.core.concurrent;

import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;

import org.jspecify.annotations.NonNull;

/**
 * {@link Executor} who is using only n Threads from the Delegates.<br>
 *
 * @author Thomas Freese
 * @since 08.02.2022
 */
public class BoundedExecutorQueued implements Executor {
    private final Executor delegate;
    private final Queue<Runnable> queue = new ArrayDeque<>();
    private final Semaphore rateLimiter;

    /**
     * @param parallelism int; Number of Threads to use from the Delegate
     */
    public BoundedExecutorQueued(final Executor delegate, final int parallelism) {
        if (parallelism < 1) {
            throw new IllegalArgumentException("parallelism < 1: " + parallelism);
        }

        super();

        this.delegate = Objects.requireNonNull(delegate, "delegate required");

        this.rateLimiter = new Semaphore(parallelism, true);
    }

    @Override
    public void execute(final @NonNull Runnable runnable) {
        if (rateLimiter.availablePermits() > 0) {
            // Are Slots available, then execute.
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
        catch (final RejectedExecutionException ex) {
            rateLimiter.release();

            throw ex;
        }
        catch (InterruptedException _) {
            // Restore interrupted state.
            Thread.currentThread().interrupt();
        }
    }
}
