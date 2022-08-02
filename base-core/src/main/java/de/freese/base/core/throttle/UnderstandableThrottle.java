// Created: 01.04.2020
package de.freese.base.core.throttle;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

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
     *
     * @return {@link GoogleThrottle}
     */
    public static UnderstandableThrottle create(final double permitsPerSecond)
    {
        return new UnderstandableThrottle(permitsPerSecond);
    }

    /**
     * @param sleepFor {@link Duration}
     */
    static void sleepUninterruptibly(final Duration sleepFor)
    {
        sleepUninterruptibly(sleepFor.toNanos(), TimeUnit.NANOSECONDS);
    }

    /**
     * @param permits int
     */
    private static void checkPermits(final int permits)
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
     *
     * @return long
     */
    private static long saturatedAdd(final long val1, final long val2)
    {
        final long naiveSum = val1 + val2;

        if (((val1 ^ val2) < 0) || ((val1 ^ naiveSum) >= 0))
        {
            return naiveSum;
        }

        return Long.MAX_VALUE + ((naiveSum >>> (Long.SIZE - 1)) ^ 1);
    }

    /**
     * Inspired by com.google.common.util.concurrent.Uninterruptibles.
     *
     * @param sleepFor long
     * @param unit {@link TimeUnit}
     */
    private static void sleepUninterruptibly(final long sleepFor, final TimeUnit unit)
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
     */
    private UnderstandableThrottle(final double permitsPerSecond)
    {
        super();

        this.nextFreeSlot = System.nanoTime();

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
    public synchronized long acquireDelayDuration(final int permits)
    {
        checkPermits(permits);

        long delay = 0;

        long now = System.nanoTime();

        // Aktueller Timestamp liegt noch vor dem nächsten verfügbaren Zeitfenster -> warten.
        if (now < this.nextFreeSlot)
        {
            delay = this.nextFreeSlot - now;
        }

        // Nächstes verfügbares Zeitfenster berechnen.
        // this.nextFreeSlot = now + delay + (long) (permits * this.permitInterval);
        this.nextFreeSlot = saturatedAdd(saturatedAdd(now, delay), (long) (permits * this.permitInterval));

        return delay;
    }

    /**
     * @see de.freese.base.core.throttle.Throttle#getRate()
     */
    @Override
    public double getRate()
    {
        return ONE_SECOND_NANOS / this.permitInterval;
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

        this.permitInterval = ONE_SECOND_NANOS / permitsPerSecond;
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
     * @param nanos long
     *
     * @throws InterruptedException Falls was schiefgeht.
     */
    private void sleep(final long nanos) throws InterruptedException
    {
        if (nanos == 0L)
        {
            return;
        }

        if (SLEEP_UNINTERRUPTIBLY)
        {
            sleepUninterruptibly(nanos, TimeUnit.NANOSECONDS);
        }
        else
        {
            TimeUnit.NANOSECONDS.sleep(nanos);
        }
    }
}
