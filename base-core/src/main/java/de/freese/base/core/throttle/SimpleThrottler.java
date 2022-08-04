// Created: 01.04.2020
package de.freese.base.core.throttle;

import java.time.Duration;
import java.util.Objects;

/**
 * @author Thomas Freese
 */
public final class SimpleThrottler implements Throttler
{
    /**
     * @return {@link Throttler}
     */
    static Throttler create(final int permits, Duration duration)
    {
        return new SimpleThrottler(permits, duration);
    }

    /**
     * @return {@link Throttler}
     */
    static Throttler create(final int permitsPerSecond)
    {
        return create(permitsPerSecond, Duration.ofSeconds(1));
    }

    //    /**
    //     *
    //     */
    //    private static final boolean SLEEP_UNINTERRUPTIBLY = false;

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

    //    /**
    //     * @param waitNanos long
    //     *
    //     * @throws InterruptedException Falls was schiefgeht.
    //     */
    //    private static void sleep(final long waitNanos) throws InterruptedException
    //    {
    //        if (waitNanos <= 0L)
    //        {
    //            return;
    //        }
    //
    //        if (!SLEEP_UNINTERRUPTIBLY)
    //        {
    //            TimeUnit.NANOSECONDS.sleep(waitNanos);
    //
    //            return;
    //        }
    //
    //        // Inspired by com.google.common.util.concurrent.Uninterruptibles.
    //        boolean interrupted = false;
    //
    //        try
    //        {
    //            long remainingNanos = waitNanos;
    //            long end = System.nanoTime() + remainingNanos;
    //
    //            while (true)
    //            {
    //                try
    //                {
    //                    // TimeUnit.sleep() treats negative timeouts just like zero.
    //                    TimeUnit.NANOSECONDS.sleep(remainingNanos);
    //
    //                    return;
    //                }
    //                catch (InterruptedException ex)
    //                {
    //                    interrupted = true;
    //                    remainingNanos = end - System.nanoTime();
    //                }
    //            }
    //        }
    //        finally
    //        {
    //            if (interrupted)
    //            {
    //                Thread.currentThread().interrupt();
    //            }
    //        }
    //    }

    /**
     *
     */
    private final long permitIntervalNanos;
    /**
     *
     */
    private long nextFreeSlotNanos;

    private SimpleThrottler(final int permits, Duration duration)
    {
        super();

        if (permits <= 0)
        {
            throw new IllegalArgumentException(String.format("Permits (%s) must be positive", permits));
        }

        Objects.requireNonNull(duration, "duration required");

        this.permitIntervalNanos = duration.toNanos() / permits;

        this.nextFreeSlotNanos = System.nanoTime();
    }

    @Override
    public long reservePermits(final int permits)
    {
        if (permits <= 0)
        {
            throw new IllegalArgumentException(String.format("Requested permits (%s) must be positive", permits));
        }

        long delayNanos = 0L;

        long nowNanos = System.nanoTime();

        // Aktueller Timestamp liegt noch vor dem nächsten verfügbaren Zeitfenster -> warten.
        if (nowNanos <= this.nextFreeSlotNanos)
        {
            delayNanos = this.nextFreeSlotNanos - nowNanos;
        }

        // Nächstes verfügbares Zeitfenster berechnen.
        this.nextFreeSlotNanos = saturatedAdd(this.nextFreeSlotNanos, permits * this.permitIntervalNanos);

        return delayNanos;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append(" [");
        sb.append("permitIntervalNanos=").append(this.permitIntervalNanos);
        sb.append("]");

        return sb.toString();
    }
}
