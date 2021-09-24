// Created: 01.04.2020
package de.freese.base.core.throttle;

import java.util.concurrent.CompletionException;

/**
 * Interface für einen Rate-Limiter.<br>
 * <br>
 * As an example, imagine that we have a list of tasks to execute, but we don't want to submit more than 2 per second:
 *
 * <pre>
 * final Throttle rateLimiter = Throttle.create(2.0); // rate is "2 permits per second"
 *
 * void submitTasks(List<Runnable> tasks, Executor executor)
 * {
 *     for (Runnable task : tasks)
 *     {
 *         rateLimiter.acquire(); // may wait
 *         executor.execute(task);
 *     }
 * }
 * </pre>
 *
 * As another example, imagine that we produce a stream of data, and we want to cap it at 5kb per second. This could be accomplished by requiring a permit per
 * byte, and specifying a rate of 5000 permits per second:
 *
 * <pre>
 * final Throttle rateLimiter = Throttle.create(5000.0); // rate = 5000 permits per second
 *
 * void submitPacket(byte[] packet)
 * {
 *     rateLimiter.acquire(packet.length);
 *     networkService.send(packet);
 * }
 * </pre>
 *
 * It is important to note that the number of permits requested <i>never</i> affects the throttling of the request itself (an invocation to
 * {@code acquire(1)}<br>
 * and an invocation to {@code acquire(1000)} will result in exactly the same throttling, if any), but it affects the throttling of the <i>next</i> request.<br>
 * I.e., if an expensive task arrives at an idle Throttle, it will be granted immediately, but it is the <i>next</i> request that will experience extra<br>
 * throttling, thus paying for the cost of the expensive task.<br>
 * <br>
 *
 * @author Thomas Freese
 *
 * @see <a href="https://github.com/client-side/throttle">https://github.com/client-side/throttle</a>
 * @see <a href=
 *      "https://github.com/google/guava/blob/master/guava/src/com/google/common/util/concurrent/RateLimiter.java">com.google.common.util.concurrent.RateLimiter</a>
 * @see <a href="https://en.wikipedia.org/wiki/Token_bucket">https://en.wikipedia.org/wiki/Token_bucket</a>
 * @see <a href="https://en.wikipedia.org/wiki/Leaky_bucket">https://en.wikipedia.org/wiki/Leaky_bucket</a>
 */
public interface Throttle
{
    /**
    *
    */
    double ONE_SECOND_NANOS = 1_000_000_000.0D;

    /**
     * Acquires a single permit from this {@code Throttle}, blocking until the request can be granted. Tells the amount of time slept, if any.
     * <p>
     * This method is equivalent to {@code acquire(1)}.
     *
     * @return time spent sleeping to enforce rate, in nanoseconds; 0.0 if not rate-limited
     *
     * @throws InterruptedException unchecked internally if thread is interrupted
     */
    default long acquire() throws InterruptedException
    {
        return acquire(1);
    }

    /**
     * Acquires the given number of permits from this {@code Throttle}, blocking until the request can be granted. Tells the amount of time slept, if any.
     *
     * @param permits the number of permits to acquire
     *
     * @return time spent sleeping to enforce rate, in nanoseconds; 0.0 if not rate-limited
     *
     * @throws IllegalArgumentException if the requested number of permits is negative or zero
     * @throws InterruptedException unchecked internally if thread is interrupted
     */
    long acquire(final int permits) throws InterruptedException;

    /**
     * Acquires the given number of permits from this {@code Throttle}, returning the duration in nanoseconds to wait to match the number of permits acquired.
     *
     * @param permits the number of permits to acquire
     *
     * @return the duration in nanoseconds to wait to match the number of permits acquired
     *
     * @throws IllegalArgumentException if the requested number of permits is negative or zero
     */
    long acquireDelayDuration(final int permits);

    /**
     * Acquires a single permit from this {@code Throttle}, blocking until the request can be granted. Tells the amount of time slept, if any.
     * <p>
     * This method is equivalent to {@code acquire(1)}.
     *
     * @return time spent sleeping to enforce rate, in nanoseconds; 0.0 if not rate-limited
     *
     * @throws CompletionException if this Thread is interrupted. The cause is set to the caught InterruptedException and this Thread is re-interrupted
     */
    default long acquireUnchecked()
    {
        return acquireUnchecked(1);
    }

    /**
     * Acquires the given number of permits from this {@code Throttle}, blocking until the request can be granted. Tells the amount of time slept, if any.
     *
     * @param permits the number of permits to acquire
     *
     * @return time spent sleeping to enforce rate, in nanoseconds; 0.0 if not rate-limited
     *
     * @throws IllegalArgumentException if the requested number of permits is negative or zero
     * @throws CompletionException if this Thread is interrupted. The cause is set to the caught InterruptedException and this Thread is re-interrupted
     */
    default long acquireUnchecked(final int permits)
    {
        try
        {
            return acquire(permits);
        }
        catch (InterruptedException ex)
        {
            Thread.currentThread().interrupt();
            throw new CompletionException(ex);
        }
    }

    /**
     * Returns the stable rate as permits per second with which this {@code Throttle} is configured with. The initial value of this is the same as the
     * {@code permitsPerSecond} argument passed in the factory method that produced this {@code Throttle}, and it is only updated after invocations to
     * {@linkplain #setRate}.
     *
     * @return the current stable rate as permits per second
     */
    double getRate();

    /**
     * Updates the stable rate of this {@code Throttle}, that is, the {@code permitsPerSecond} argument provided in the factory method that constructed the
     * {@code Throttle}. Currently throttled threads will <b>not</b> be awakened as a result of this invocation, thus they do not observe the new rate; only
     * subsequent requests will.
     * <p>
     * Note though that, since each request repays (by waiting, if necessary) the cost of the <i>previous</i> request, this means that the very next request
     * after an invocation to {@code setRate} will not be affected by the new rate; it will pay the cost of the previous request, which is in terms of the
     * previous rate.
     * <p>
     * The behavior of the {@code Throttle} is not modified in any other way, e.g. if the {@code Throttle} was configured with a warmup period of 20 seconds, it
     * still has a warmup period of 20 seconds after this method invocation.
     *
     * @param permitsPerSecond the new stable rate of this {@code Throttle}
     *
     * @throws IllegalArgumentException if {@code permitsPerSecond} is negative or zero
     */
    void setRate(final double permitsPerSecond);
}
