package de.freese.base.core.throttle;

import java.time.Duration;
import java.util.Objects;

import dev.failsafe.RateLimiter;

/**
 * @author Thomas Freese
 */
public final class FailsafeThrottlerAdapter implements Throttler {
    public static Throttler create(final int permits, final Duration duration) {
        return new FailsafeThrottlerAdapter(RateLimiter.burstyBuilder(permits, duration).build());
    }

    public static Throttler create(final int permitsPerSecond) {
        return create(permitsPerSecond, Duration.ofSeconds(1));
    }

    private final RateLimiter<Object> rateLimiter;

    private FailsafeThrottlerAdapter(final RateLimiter<Object> rateLimiter) {
        super();

        this.rateLimiter = Objects.requireNonNull(rateLimiter, "rateLimiter required");
    }

    @Override
    public synchronized long reservePermits(final int permits) {
        return this.rateLimiter.reservePermits(permits).toNanos();
    }
}
