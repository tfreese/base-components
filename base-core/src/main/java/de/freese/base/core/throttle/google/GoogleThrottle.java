// Created: 29.03.2020
package de.freese.base.core.throttle.google;

import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

import de.freese.base.core.throttle.Throttle;

/**
 * A rate limiter. Conceptually, a rate limiter distributes permits at a configurable rate. Each {@link #acquire()} blocks if necessary until a permit is
 * available, and then takes it. Once acquired, permits need not be released.
 * <p>
 * Rate limiters are often used to restrict the rate at which some physical or logical resource is accessed. This is in contrast to
 * {@link java.util.concurrent.Semaphore} which restricts the number of concurrent accesses instead of the rate (note though that concurrency and rate are
 * closely related, e.g. see <a href="http://en.wikipedia.org/wiki/Little%27s_law">Little's Law</a>).
 * <p>
 * A {@code Throttle} is defined primarily by the rate at which permits are issued. Absent additional configuration, permits will be distributed at a fixed
 * rate, defined in terms of permits per second. Permits will be distributed smoothly, with the delay between individual permits being adjusted to ensure that
 * the configured rate is maintained.
 * <p>
 * It is possible to configure a {@code Throttle} to have a warmup period during which time the permits issued each second steadily increases until it hits the
 * stable rate.
 * <p>
 * As an example, imagine that we have a list of tasks to execute, but we don't want to submit more than 2 per second:
 *
 * <pre>
 *    {@code
 *  final Throttle rateLimiter = Throttle.create(2.0); // rate is "2 permits per second"
 *  void submitTasks(List<Runnable> tasks, Executor executor) {
 *    for (Runnable task : tasks) {
 *      rateLimiter.acquire(); // may wait
 *      executor.execute(task);
 *    }
 *  }}
 * </pre>
 * <p>
 * As another example, imagine that we produce a stream of data, and we want to cap it at 5kb per second. This could be accomplished by requiring a permit per
 * byte, and specifying a rate of 5000 permits per second:
 *
 * <pre>
 *  {@code
 *  final Throttle rateLimiter = Throttle.create(5000.0); // rate = 5000 permits per second
 *  void submitPacket(byte[] packet) {
 *    rateLimiter.acquire(packet.length);
 *    networkService.send(packet);
 *  }}
 * </pre>
 * <p>
 * It is important to note that the number of permits requested <i>never</i> affects the throttling of the request itself (an invocation to {@code acquire(1)}
 * and an invocation to {@code acquire(1000)} will result in exactly the same throttling, if any), but it affects the throttling of the <i>next</i> request.
 * I.e., if an expensive task arrives at an idle Throttle, it will be granted immediately, but it is the <i>next</i> request that will experience extra
 * throttling, thus paying for the cost of the expensive task.
 * <p>
 * Note: {@code Throttle} does not provide fairness guarantees.
 * <p>
 * Note: Dimitris Andreou is the original author of the RateLimiter class from the Google Guava project that implementations of this interface derive from.
 * <p>
 * Derivations include:
 * <ul>
 * <li>Nanosecond accuracy instead of microsecond accuracy.</li>
 * <li>Factoring out an interface class (Throttle.java) from the base abstract class.</li>
 * <li>Remove the need for any non-core-Java class outside of the original RateLimiter and SmoothRateLimiter classes.</li>
 * <li>Remove the need for a SleepingStopwatch or similar class instance.</li>
 * <li>Use volatile variables to prevent stale reads under concurrent access.</li>
 * </ul>
 *
 * @author Dimitris Andreou - Original author.
 * @author James P Edwards - Throttle project derivations.
 */
public interface GoogleThrottle extends Throttle
{
    /**
     *
     */
    boolean sleepUninterruptibly = false;

    /**
     * @param permitsPerSecond double
     *
     * @return {@link GoogleThrottle}
     */
    static GoogleThrottle create(final double permitsPerSecond)
    {
        return create(permitsPerSecond, false);
    }

    /**
     * Creates a {@code Throttle} with the specified stable throughput, given as "permits per second" (commonly referred to as <i>QPS</i>, queries per second).
     * <p>
     * The returned {@code Throttle} ensures that on average no more than {@code
     * permitsPerSecond} are issued during any given second, with sustained requests being smoothly spread over each second. When the incoming request rate
     * exceeds {@code permitsPerSecond} the rate limiter will release one permit every {@code
     * (1.0 / permitsPerSecond)} seconds. When the rate limiter is unused, bursts of up to {@code permitsPerSecond} permits will be allowed, with subsequent
     * requests being smoothly limited at the stable rate of {@code permitsPerSecond}.
     *
     * @param permitsPerSecond the rate of the returned {@code Throttle}, measured in how many permits become available per second
     * @param fair {@code true} if acquisition should use a fair ordering policy
     *
     * @return a new throttle instance
     *
     * @throws IllegalArgumentException if {@code permitsPerSecond} is negative or zero
     */
    static GoogleThrottle create(final double permitsPerSecond, final boolean fair)
    {
        return new GoogleNanoThrottle.GoldFish(permitsPerSecond, 0.1D, fair);
    }

    /**
     * Acquires a permit from this {@link Throttle} if it can be acquired immediately without delay.
     * <p>
     * This method is equivalent to {@code tryAcquire(1)}.
     *
     * @return {@code true} if the permit was acquired, {@code false} otherwise
     */
    default boolean tryAcquire()
    {
        return tryAcquire(1);
    }

    /**
     * Acquires permits from this {@link Throttle} if it can be acquired immediately without delay.
     * <p>
     * This method is equivalent to {@code tryAcquire(permits, 0, anyUnit)}.
     *
     * @param permits the number of permits to acquire
     *
     * @return {@code true} if the permits were acquired, {@code false} otherwise
     *
     * @throws IllegalArgumentException if the requested number of permits is negative or zero
     */
    boolean tryAcquire(final int permits);

    /**
     * Acquires the given number of permits from this {@code Throttle} if it can be obtained without exceeding the specified {@code timeout}, or returns
     * {@code false} immediately (without waiting) if the permits would not have been granted before the timeout expired.
     *
     * @param permits the number of permits to acquire
     * @param timeout the maximum time to wait for the permits. Negative values are treated as zero.
     * @param unit the time unit of the timeout argument
     *
     * @return {@code true} if the permits were acquired, {@code false} otherwise
     *
     * @throws IllegalArgumentException if the requested number of permits is negative or zero
     * @throws InterruptedException unchecked internally if thread is interrupted
     */
    boolean tryAcquire(final int permits, final long timeout, final TimeUnit unit) throws InterruptedException;

    /**
     * Acquires a permit from this {@code Throttle} if it can be obtained without exceeding the specified {@code timeout}, or returns {@code false} immediately
     * (without waiting) if the permit would not have been granted before the timeout expired.
     * <p>
     * This method is equivalent to {@code tryAcquire(1, timeout, unit)}.
     *
     * @param timeout the maximum time to wait for the permit. Negative values are treated as zero.
     * @param unit the time unit of the timeout argument
     *
     * @return {@code true} if the permit was acquired, {@code false} otherwise
     *
     * @throws IllegalArgumentException if the requested number of permits is negative or zero
     * @throws InterruptedException unchecked internally if thread is interrupted
     */
    default boolean tryAcquire(final long timeout, final TimeUnit unit) throws InterruptedException
    {
        return tryAcquire(1, timeout, unit);
    }

    /**
     * Acquires the given number of permits from this {@code Throttle} if it can be obtained without exceeding the specified {@code timeout} and returns the
     * duration in nanoseconds to wait to match the acquired permits, or returns {@code -1} if the permits would not have been granted before the timeout
     * expired.
     *
     * @param permits the number of permits to acquire
     * @param timeout the maximum time to wait for the permits. Negative values are treated as zero.
     * @param unit the time unit of the timeout argument
     *
     * @return The duration in nanosecond to wait to match the acquired permits, or -1 if no permits were acquired because the timeout would expire.
     *
     * @throws IllegalArgumentException if the requested number of permits is negative or zero
     */
    long tryAcquireDelayDuration(final int permits, final long timeout, final TimeUnit unit);

    /**
     * Acquires the given number of permits from this {@code Throttle} if it can be obtained without exceeding the specified {@code timeout}, or returns
     * {@code false} immediately (without waiting) if the permits would not have been granted before the timeout expired.
     *
     * @param permits the number of permits to acquire
     * @param timeout the maximum time to wait for the permits. Negative values are treated as zero.
     * @param unit the time unit of the timeout argument
     *
     * @return {@code true} if the permits were acquired, {@code false} otherwise
     *
     * @throws IllegalArgumentException if the requested number of permits is negative or zero
     * @throws CompletionException if this Thread is interrupted. The cause is set to the caught InterruptedException and this Thread is re-interrupted
     */
    default boolean tryAcquireUnchecked(final int permits, final long timeout, final TimeUnit unit)
    {
        try
        {
            return tryAcquire(permits, timeout, unit);
        }
        catch (InterruptedException ex)
        {
            Thread.currentThread().interrupt();

            throw new CompletionException(ex);
        }
    }

    /**
     * @param timeout long
     * @param unit {@link TimeUnit}
     *
     * @return boolean
     */
    default boolean tryAcquireUnchecked(final long timeout, final TimeUnit unit)
    {
        return tryAcquireUnchecked(1, timeout, unit);
    }
}
