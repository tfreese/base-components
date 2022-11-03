package de.freese.base.core.throttle;

import java.time.Duration;
import java.util.Objects;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;

/**
 * @author Thomas Freese
 */
public final class Resilience4JThrottlerAdapter implements Throttler
{
    private static final RateLimiterRegistry RATELIMITER_REGISTRY = RateLimiterRegistry.ofDefaults();

    public static Throttler create(final int permits, Duration duration)
    {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(permits)
                .limitRefreshPeriod(duration)
                //.timeoutDuration(Duration.ofMinutes(1))
                .build();

        // Sorgt für Wiederverwendung des RateLimiters bei gleicher Permit/Duration Kombination.
        String name = permits + "_" + duration;

        RateLimiter rateLimiter = RATELIMITER_REGISTRY.rateLimiter(name, config);

        return new Resilience4JThrottlerAdapter(rateLimiter);
    }

    public static Throttler create(final int permitsPerSecond)
    {
        return create(permitsPerSecond, Duration.ofSeconds(1));
    }

    private final RateLimiter rateLimiter;

    private Resilience4JThrottlerAdapter(final RateLimiter rateLimiter)
    {
        super();

        this.rateLimiter = Objects.requireNonNull(rateLimiter, "rateLimiter required");
    }

    @Override
    public synchronized long reservePermits(final int permits)
    {
        return this.rateLimiter.reservePermission(permits);
    }
}
