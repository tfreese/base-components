// Created: 01.04.2020
package de.freese.base.core.throttle;

import java.time.Duration;
import java.util.Objects;

/**
 * @author Thomas Freese
 */
public final class SimpleThrottler implements Throttler {
    private final long permitIntervalNanos;
    private long nextFreeSlotNanos;

    private SimpleThrottler(final int permits, final Duration duration) {
        super();

        if (permits <= 0) {
            throw new IllegalArgumentException(String.format("Permits (%s) must be positive", permits));
        }

        Objects.requireNonNull(duration, "duration required");

        // duration.dividedBy(permits).toNanos()
        permitIntervalNanos = duration.toNanos() / permits;

        nextFreeSlotNanos = System.nanoTime();
    }

    static Throttler create(final int permitsPerSecond, final Duration duration) {
        return new SimpleThrottler(permitsPerSecond, duration);
    }

    static Throttler create(final int permitsPerSecond) {
        return create(permitsPerSecond, Duration.ofSeconds(1L));
    }

    /**
     * Returns the sum of {@code val1} and {@code val2} unless it would overflow or underflow in which case {@code Long.MAX_VALUE} or {@code Long.MIN_VALUE} is
     * returned, respectively.
     */
    private static long saturatedAdd(final long val1, final long val2) {
        final long naiveSum = val1 + val2;

        if ((val1 ^ val2) < 0 || (val1 ^ naiveSum) >= 0L) {
            return naiveSum;
        }

        return Long.MAX_VALUE + ((naiveSum >>> (Long.SIZE - 1L)) ^ 1L);
    }

    @Override
    public synchronized long reservePermits(final int permits) {
        if (permits <= 0) {
            throw new IllegalArgumentException(String.format("Requested permits (%s) must be positive", permits));
        }

        long delayNanos = 0L;

        final long nowNanos = System.nanoTime();

        // Aktueller Timestamp liegt noch vor dem nächsten verfügbaren Zeitfenster -> warten.
        if (nowNanos <= nextFreeSlotNanos) {
            delayNanos = nextFreeSlotNanos - nowNanos;
        }

        // Nächstes verfügbares Zeitfenster berechnen.
        nextFreeSlotNanos = saturatedAdd(nextFreeSlotNanos, permits * permitIntervalNanos);

        return delayNanos;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " ["
                + "permitIntervalNanos=" + permitIntervalNanos
                + "]";
    }
}
