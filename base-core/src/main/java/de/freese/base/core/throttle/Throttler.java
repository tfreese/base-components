// Created: 01.04.2020
package de.freese.base.core.throttle;

import java.util.concurrent.TimeUnit;

/**
 * Inspired by dev.failsafe.RateLimiter.
 *
 * @author Thomas Freese
 */
public interface Throttler
{
    /**
     * Attempts to acquire a permit to perform an execution against the rate limiter, waiting until one is available or
     * the thread is interrupted.
     *
     * @throws InterruptedException if the current thread is interrupted while waiting to acquire a permit
     * @see #tryAcquirePermit()
     * @see #reservePermit()
     */
    default void acquirePermit() throws InterruptedException
    {
        acquirePermits(1);
    }

    /**
     * Attempts to acquire the requested {@code permits} to perform executions against the rate limiter, waiting until
     * they are available or the thread is interrupted.
     *
     * @throws IllegalArgumentException if {@code permits} is < 1
     * @throws InterruptedException if the current thread is interrupted while waiting to acquire the {@code permits}
     * @see #tryAcquirePermits(int)
     * @see #reservePermits(int)
     */
    default void acquirePermits(final int permits) throws InterruptedException
    {
        long waitNanos = reservePermits(permits);

        if (waitNanos > 0L)
        {
            TimeUnit.NANOSECONDS.sleep(waitNanos);
            //LockSupport.parkNanos(waitNanos);
        }
    }

    /**
     * Reserves a permit to perform an execution against the rate limiter, and returns the nanoseconds that the caller is
     * expected to wait before acting on the permit. Returns {@code 0} if the permit is immediately available and no
     * waiting is needed.
     *
     * @see #acquirePermit()
     * @see #tryAcquirePermit()
     */
    default long reservePermit()
    {
        return reservePermits(1);
    }

    /**
     * Reserves the {@code permits} to perform executions against the rate limiter, and returns the nanoseconds that the caller
     * is expected to wait before acting on the permits. Returns {@code 0} if the permits are immediately available and no
     * waiting is needed.
     *
     * @throws IllegalArgumentException if {@code permits} is < 1
     * @see #acquirePermits(int)
     * @see #tryAcquirePermits(int)
     */
    long reservePermits(final int permits);

    /**
     * Tries to acquire a permit to perform an execution against the rate limiter, returning immediately without waiting.
     *
     * @return whether the requested {@code permits} are successfully acquired or not
     *
     * @see #acquirePermit()
     * @see #reservePermits(int)
     */
    default boolean tryAcquirePermit()
    {
        return tryAcquirePermits(1);
    }

    /**
     * Tries to acquire the requested {@code permits} to perform executions against the rate limiter, returning
     * immediately without waiting.
     *
     * @return whether the requested {@code permits} are successfully acquired or not
     *
     * @throws IllegalArgumentException if {@code permits} is < 1
     * @see #acquirePermits(int)
     */
    default boolean tryAcquirePermits(int permits)
    {
        return reservePermits(permits) == 0;
    }
}
