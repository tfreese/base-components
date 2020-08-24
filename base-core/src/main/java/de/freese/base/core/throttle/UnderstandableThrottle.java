/**
 * Created: 01.04.2020
 */

package de.freese.base.core.throttle;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import de.freese.base.core.throttle.google.GoogleThrottle;

/**
 * Verständlich aufgebaute {@link Throttle}-Implementierung analog dem Google RateLimiter.
 *
 * @author Thomas Freese
 */
public final class UnderstandableThrottle implements Throttle
{
    /**
    *
    */
    private static final boolean SLEEP_UNINTERRUPTIBLY = false;

    /**
     * @param permitsPerSecond double
     * @return {@link GoogleThrottle}
     */
    public static UnderstandableThrottle create(final double permitsPerSecond)
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
     * @return a new throttle instance
     * @throws IllegalArgumentException if {@code permitsPerSecond} is negative or zero
     */
    static UnderstandableThrottle create(final double permitsPerSecond, final boolean fair)
    {
        return new UnderstandableThrottle(permitsPerSecond, fair);
    }

    /**
    *
    */
    private final ReentrantLock lock;

    /**
    *
    */
    private long nextFreeSlot;

    /**
    *
    */
    private double permitInterval;

    /**
     * Erstellt ein neues {@link UnderstandableThrottle} Object.
     *
     * @param permitsPerSecond double
     * @param fair boolean
     */
    private UnderstandableThrottle(final double permitsPerSecond, final boolean fair)
    {
        super();

        this.nextFreeSlot = System.nanoTime();

        this.lock = new ReentrantLock(fair);

        setRate(permitsPerSecond);
    }

    /**
     * @see de.freese.base.core.throttle.Throttle#acquire(int)
     */
    @Override
    public long acquire(final int permits) throws InterruptedException
    {
        final long waitDuration = acquireDelayDuration(permits);

        sleep(waitDuration);

        return waitDuration;
    }

    /**
     * @see de.freese.base.core.throttle.Throttle#acquireDelayDuration(int)
     */
    @Override
    public long acquireDelayDuration(final int permits)
    {
        checkPermits(permits);

        long delay = 0;
        this.lock.lock();

        try
        {
            long now = System.nanoTime();

            // Aktueller Timestamp liegt noch vor dem nächsten verfügbaren Zeitfenster - warten.
            if (now < this.nextFreeSlot)
            {
                delay = this.nextFreeSlot - now;
            }

            // Nächstes verfügbares Zeitfenster berechnen.
            // this.nextFreeSlot = now + delay + (long) (permits * this.permitInterval);
            this.nextFreeSlot = saturatedAdd(saturatedAdd(now, delay), (long) (permits * this.permitInterval));
        }
        finally
        {
            this.lock.unlock();
        }

        return delay;
    }

    /**
     * @see de.freese.base.core.throttle.Throttle#getRate()
     */
    @Override
    public double getRate()
    {
        this.lock.lock();

        try
        {
            return ONE_SECOND_NANOS / this.permitInterval;
        }
        finally
        {
            this.lock.unlock();
        }
    }

    /**
     * @see de.freese.base.core.throttle.Throttle#setRate(double)
     */
    @Override
    public void setRate(final double permitsPerSecond)
    {
        if ((permitsPerSecond <= 0.0) || !Double.isFinite(permitsPerSecond))
        {
            throw new IllegalArgumentException("rate must be positive");
        }

        this.lock.lock();

        try
        {
            this.permitInterval = ONE_SECOND_NANOS / permitsPerSecond;
        }
        finally
        {
            this.lock.unlock();
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append(" [");
        sb.append("rate=").append(getRate());
        sb.append("]");

        return sb.toString();
    }

    /**
     * @param permits int
     */
    protected void checkPermits(final int permits)
    {
        if (permits <= 0)
        {
            throw new IllegalArgumentException(String.format("Requested permits (%s) must be positive", permits));
        }
    }

    /**
     * Returns the sum of {@code val1} and {@code val2} unless it would overflow or underflow in which case {@code Long.MAX_VALUE} or {@code Long.MIN_VALUE} is
     * returned, respectively.
     *
     * @param val1 long
     * @param val2 long
     * @return long
     */
    protected long saturatedAdd(final long val1, final long val2)
    {
        final long naiveSum = val1 + val2;

        if (((val1 ^ val2) < 0) || ((val1 ^ naiveSum) >= 0))
        {
            return naiveSum;
        }

        return Long.MAX_VALUE + ((naiveSum >>> (Long.SIZE - 1)) ^ 1);
    }

    /**
     * @param nanos long
     * @throws InterruptedException Falls was schief geht.
     */
    protected void sleep(final long nanos) throws InterruptedException
    {
        // System.out.println("UnderstandableThrottle.sleep(): " + TimeUnit.NANOSECONDS.toMillis(nanos) + " ms");

        if (SLEEP_UNINTERRUPTIBLY)
        {
            sleepUninterruptibly(nanos, TimeUnit.NANOSECONDS);
        }
        else
        {
            TimeUnit.NANOSECONDS.sleep(nanos);
        }
    }

    /**
     * @param sleepFor {@link Duration}
     */
    protected void sleepUninterruptibly(final Duration sleepFor)
    {
        sleepUninterruptibly(sleepFor.toNanos(), TimeUnit.NANOSECONDS);
    }

    /**
     * @param sleepFor long
     * @param unit {@link TimeUnit}
     */
    protected void sleepUninterruptibly(final long sleepFor, final TimeUnit unit)
    {
        boolean interrupted = false;

        try
        {
            long remainingNanos = unit.toNanos(sleepFor);
            long end = System.nanoTime() + remainingNanos;

            while (true)
            {
                try
                {
                    // TimeUnit.sleep() treats negative timeouts just like zero.
                    TimeUnit.NANOSECONDS.sleep(remainingNanos);

                    return;
                }
                catch (InterruptedException ex)
                {
                    interrupted = true;
                    remainingNanos = end - System.nanoTime();
                }
            }
        }
        finally
        {
            if (interrupted)
            {
                Thread.currentThread().interrupt();
            }
        }
    }
}
