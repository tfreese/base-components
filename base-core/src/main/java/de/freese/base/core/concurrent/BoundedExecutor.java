package de.freese.base.core.concurrent;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;

import org.jspecify.annotations.NonNull;

/**
 * {@link Executor} who is using only n Threads from the Delegate.<br>
 *
 * @author Thomas Freese
 * @since 12.09.2021
 */
public class BoundedExecutor implements Executor {
    private final Executor delegate;
    private final Semaphore rateLimiter;

    /**
     * @param parallelism int; Number of Threads to use from the Delegate
     */
    public BoundedExecutor(final Executor delegate, final int parallelism) {
        if (parallelism < 1) {
            throw new IllegalArgumentException("parallelism < 1: " + parallelism);
        }

        super();

        this.delegate = Objects.requireNonNull(delegate, "delegate required");

        rateLimiter = new Semaphore(parallelism, true);
    }

    @Override
    public void execute(final @NonNull Runnable runnable) {
        try {
            rateLimiter.acquire();

            delegate.execute(() -> {
                try {
                    runnable.run();
                }
                finally {
                    rateLimiter.release();
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
