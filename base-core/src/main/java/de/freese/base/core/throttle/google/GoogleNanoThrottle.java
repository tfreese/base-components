/**
 * Created: 29.03.2020
 */
package de.freese.base.core.throttle.google;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Dimitris Andreou - Original author.
 * @author James P Edwards - Throttle project derivations.
 * @see <a href=
 *      "https://github.com/google/guava/blob/master/guava/src/com/google/common/util/concurrent/RateLimiter.java">com.google.common.util.concurrent.RateLimiter</a>
 */
public abstract class GoogleNanoThrottle implements GoogleThrottle
{
    /**
     * @author Thomas Freese
     */
    public static final class GoldFish extends GoogleNanoThrottle
    {
        /**
         *
         */
        private final double maxBurstSeconds;

        /**
         * Erstellt ein neues {@link GoldFish} Object.
         *
         * @param permitsPerSecond double
         * @param maxBurstSeconds double
         * @param fair boolean
         */
        public GoldFish(final double permitsPerSecond, final double maxBurstSeconds, final boolean fair)
        {
            super(permitsPerSecond, fair);

            this.maxBurstSeconds = maxBurstSeconds;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        double coolDownIntervalNanos()
        {
            return this.stableIntervalNanos;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        void doSetRate(final double permitsPerSecond, final double stableIntervalNanos)
        {
            final double oldMaxPermits = this.maxPermits;
            this.maxPermits = this.maxBurstSeconds * permitsPerSecond;
            this.storedPermits = oldMaxPermits == 0.0D ? 0.0D : (this.storedPermits * this.maxPermits) / oldMaxPermits;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        long storedPermitsToWaitTime(final double storedPermits, final double permitsToTake)
        {
            return 0L;
        }
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
     * @return long
     */
    static long saturatedAdd(final long val1, final long val2)
    {
        final long naiveSum = val1 + val2;

        if (((val1 ^ val2) < 0) || ((val1 ^ naiveSum) >= 0))
        {
            return naiveSum;
        }

        return Long.MAX_VALUE + ((naiveSum >>> (Long.SIZE - 1)) ^ 1);
    }

    /**
     *
     */
    private final ReentrantLock lock;

    /**
     *
     */
    double maxPermits;

    /**
     *
     */
    private final long nanoStart;

    /**
     *
     */
    private long nextFreeTicketNanos;

    /**
     *
     */
    double stableIntervalNanos;

    /**
     *
     */
    double storedPermits;

    /**
     * Erstellt ein neues {@link GoogleNanoThrottle} Object.
     *
     * @param permitsPerSecond double
     * @param fair boolean
     */
    private GoogleNanoThrottle(final double permitsPerSecond, final boolean fair)
    {
        if ((permitsPerSecond <= 0.0) || !Double.isFinite(permitsPerSecond))
        {
            throw new IllegalArgumentException("rate must be positive");
        }

        this.nanoStart = System.nanoTime();
        this.nextFreeTicketNanos = 0L;

        doSetRate(permitsPerSecond);

        this.lock = new ReentrantLock(fair);
    }

    /**
     * @see de.freese.base.core.throttle.Throttle#acquire(int)
     */
    @Override
    public final long acquire(final int permits) throws InterruptedException
    {
        final long waitDuration = acquireDelayDuration(permits);

        sleep(waitDuration);

        return waitDuration;
    }

    /**
     * @see de.freese.base.core.throttle.Throttle#acquireDelayDuration(int)
     */
    @Override
    public final long acquireDelayDuration(final int permits)
    {
        checkPermits(permits);

        long elapsedNanos = 0;
        long momentAvailable = 0;
        this.lock.lock();

        try
        {
            elapsedNanos = System.nanoTime() - this.nanoStart;
            momentAvailable = reserveEarliestAvailable(permits, elapsedNanos);
        }
        finally
        {
            this.lock.unlock();
        }

        return Math.max(momentAvailable - elapsedNanos, 0);
    }

    /**
     * @param elapsedNanos long
     * @param timeoutNanos long
     * @return boolean
     */
    private boolean canAcquire(final long elapsedNanos, final long timeoutNanos)
    {
        return (this.nextFreeTicketNanos - timeoutNanos) <= elapsedNanos;
    }

    /**
     * Returns the number of nanoseconds during cool down that we have to wait to get a new permit.
     *
     * @return double
     */
    abstract double coolDownIntervalNanos();

    /**
     * @param permitsPerSecond double
     */
    private void doSetRate(final double permitsPerSecond)
    {
        reSync(System.nanoTime() - this.nanoStart);

        this.stableIntervalNanos = ONE_SECOND_NANOS / permitsPerSecond;

        doSetRate(permitsPerSecond, this.stableIntervalNanos);
    }

    /**
     * @param permitsPerSecond double
     * @param stableIntervalNanos double
     */
    abstract void doSetRate(final double permitsPerSecond, final double stableIntervalNanos);

    /**
     * @see de.freese.base.core.throttle.Throttle#getRate()
     */
    @Override
    public final double getRate()
    {
        this.lock.lock();

        try
        {
            return ONE_SECOND_NANOS / this.stableIntervalNanos;
        }
        finally
        {
            this.lock.unlock();
        }
    }

    /**
     * Reserves the requested number of permits and returns the time that those permits can be used (with one caveat).
     *
     * @param requiredPermits int
     * @param elapsedNanos long
     * @return the time that the permits may be used, or, if the permits may be used immediately, an arbitrary past or present time
     */
    private long reserveEarliestAvailable(final int requiredPermits, final long elapsedNanos)
    {
        reSync(elapsedNanos);

        final long returnValue = this.nextFreeTicketNanos;
        final double storedPermitsToSpend = Math.min(requiredPermits, this.storedPermits);
        final double freshPermits = requiredPermits - storedPermitsToSpend;
        final long waitNanos = storedPermitsToWaitTime(this.storedPermits, storedPermitsToSpend) + (long) (freshPermits * this.stableIntervalNanos);
        this.nextFreeTicketNanos = saturatedAdd(this.nextFreeTicketNanos, waitNanos);
        this.storedPermits -= storedPermitsToSpend;

        return returnValue;
    }

    /**
     * Updates {@code storedPermits} and {@code nextFreeTicketNanos} based on the current time.
     *
     * @param elapsedNanos long
     */
    private void reSync(final long elapsedNanos)
    {
        if (elapsedNanos > this.nextFreeTicketNanos)
        {
            final double newPermits = (elapsedNanos - this.nextFreeTicketNanos) / coolDownIntervalNanos();
            this.storedPermits = Math.min(this.maxPermits, this.storedPermits + newPermits);
            this.nextFreeTicketNanos = elapsedNanos;
        }
    }

    /**
     * @see de.freese.base.core.throttle.Throttle#setRate(double)
     */
    @Override
    public final void setRate(final double permitsPerSecond)
    {
        if ((permitsPerSecond <= 0.0) || !Double.isFinite(permitsPerSecond))
        {
            throw new IllegalArgumentException("rate must be positive");
        }

        this.lock.lock();

        try
        {
            doSetRate(permitsPerSecond);
        }
        finally
        {
            this.lock.unlock();
        }
    }

    /**
     * @param nanos long
     * @throws InterruptedException Falls was schief geht.
     */
    protected void sleep(final long nanos) throws InterruptedException
    {
        if (GoogleThrottle.sleepUninterruptibly)
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

    /**
     * Translates a specified portion of our currently stored permits which we want to spend/acquire, into a throttling time. Conceptually, this evaluates the
     * integral of the underlying function we use, for the range of [(storedPermits - permitsToTake), storedPermits].
     * <p>
     * This always holds: {@code 0 <= permitsToTake <= storedPermits}
     *
     * @param storedPermits double
     * @param permitsToTake double
     * @return long
     */
    abstract long storedPermitsToWaitTime(final double storedPermits, final double permitsToTake);

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
     * @see de.freese.base.core.throttle.google.GoogleThrottle#tryAcquire(int)
     */
    @Override
    public final boolean tryAcquire(final int permits)
    {
        checkPermits(permits);
        this.lock.lock();

        try
        {
            final long elapsedNanos = System.nanoTime() - this.nanoStart;

            if (canAcquire(elapsedNanos, 0))
            {
                reserveEarliestAvailable(permits, elapsedNanos);

                return true;
            }
        }
        finally
        {
            this.lock.unlock();
        }

        return false;
    }

    /**
     * @see de.freese.base.core.throttle.google.GoogleThrottle#tryAcquire(int, long, java.util.concurrent.TimeUnit)
     */
    @Override
    public final boolean tryAcquire(final int permits, final long timeout, final TimeUnit unit) throws InterruptedException
    {
        final long waitDuration = tryAcquireDelayDuration(permits, timeout, unit);

        if (waitDuration < 0)
        {
            return false;
        }

        sleep(waitDuration);

        return true;
    }

    /**
     * @see de.freese.base.core.throttle.google.GoogleThrottle#tryAcquireDelayDuration(int, long, java.util.concurrent.TimeUnit)
     */
    @Override
    public final long tryAcquireDelayDuration(final int permits, final long timeout, final TimeUnit unit)
    {
        if (timeout <= 0)
        {
            return tryAcquire(permits) ? 0 : -1;
        }

        final long waitDuration = unit.toNanos(timeout);

        checkPermits(permits);

        long durationElapsed = 0;
        long momentAvailable = 0;
        this.lock.lock();

        try
        {
            durationElapsed = System.nanoTime() - this.nanoStart;

            if (!canAcquire(durationElapsed, waitDuration))
            {
                return -1;
            }

            momentAvailable = reserveEarliestAvailable(permits, durationElapsed);
        }
        finally
        {
            this.lock.unlock();
        }

        return momentAvailable - durationElapsed;
    }
}
