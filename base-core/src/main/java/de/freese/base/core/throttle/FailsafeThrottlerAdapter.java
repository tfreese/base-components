package de.freese.base.core.throttle;

import dev.failsafe.RateLimiter;

import java.time.Duration;
import java.util.Objects;

/**
 * @author Thomas Freese
 */
public final class FailsafeThrottlerAdapter implements Throttler {
    private final RateLimiter<Object> rateLimiter;

    private FailsafeThrottlerAdapter(final RateLimiter<Object> rateLimiter) {
        super();

        this.rateLimiter = Objects.requireNonNull(rateLimiter, "rateLimiter required");
    }

    public static Throttler create(final int permitsPerSecond, final Duration duration) {
        // builder.withMaxWaitTime(Duration.ofSeconds(1))
//        return new FailsafeThrottlerAdapter(RateLimiter.burstyBuilder(permitsPerSecond, duration).build());
        return new FailsafeThrottlerAdapter(RateLimiter.smoothBuilder(permitsPerSecond, duration).build());
    }

    public static Throttler create(final int permitsPerSecond) {
        return create(permitsPerSecond, Duration.ofSeconds(1L));
    }

    @Override
    public synchronized long reservePermits(final int permits) {
        return rateLimiter.reservePermits(permits).toNanos();
    }
}
