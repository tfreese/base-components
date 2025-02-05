// Created: 01.04.2020
package de.freese.base.core.throttle;

import java.time.Duration;
import java.util.Objects;

/**
 * @author Thomas Freese
 */
public final class SimpleThrottler implements Throttler {
    static Throttler create(final int permits, final Duration duration) {
        return new SimpleThrottler(permits, duration);
    }

    static Throttler create(final int permitsPerSecond) {
        return create(permitsPerSecond, Duration.ofSeconds(1));
    }

    /**
     * Returns the sum of {@code val1} and {@code val2} unless it would overflow or underflow in which case {@code Long.MAX_VALUE} or {@code Long.MIN_VALUE} is
     * returned, respectively.
     */
    private static long saturatedAdd(final long val1, final long val2) {
        final long naiveSum = val1 + val2;

        if ((val1 ^ val2) < 0 || (val1 ^ naiveSum) >= 0) {
            return naiveSum;
        }

        return Long.MAX_VALUE + ((naiveSum >>> (Long.SIZE - 1)) ^ 1);
    }

    private final long permitIntervalNanos;

    private long nextFreeSlotNanos;

    private SimpleThrottler(final int permits, final Duration duration) {
        super();

        if (permits <= 0) {
            throw new IllegalArgumentException(String.format("Permits (%s) must be positive", permits));
        }

        Objects.requireNonNull(duration, "duration required");

        // duration.dividedBy(permits).toNanos()
        this.permitIntervalNanos = duration.toNanos() / permits;

        this.nextFreeSlotNanos = System.nanoTime();
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
        final StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append(" [");
        sb.append("permitIntervalNanos=").append(permitIntervalNanos);
        sb.append("]");

        return sb.toString();
    }
}
