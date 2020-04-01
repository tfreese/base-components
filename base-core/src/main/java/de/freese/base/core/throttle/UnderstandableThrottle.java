/**
 * Created: 01.04.2020
 */

package de.freese.base.core.throttle;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Verständlich aufgebaute {@link Throttle}-Implementierung analog dem Google RateLimiter.
 *
 * @author Thomas Freese
 */
public class UnderstandableThrottle implements Throttle
{
    /**
    *
    */
    private static final double ONE_SECOND_NANOS = 1_000_000_000.0D;

    /**
    *
    */
    private static final boolean SLEEP_UNINTERRUPTIBLY = false;

    /**
       *
       */
    private final ReentrantLock lock;

    /**
    *
    */
    private final long nanoStart;

    /**
    *
    */
    private double permitInterval = 0.0D;

    /**
     * Erstellt ein neues {@link UnderstandableThrottle} Object.
     *
     * @param permitsPerSecond double
     * @param fair boolean
     */
    private UnderstandableThrottle(final double permitsPerSecond, final boolean fair)
    {
        super();

        this.nanoStart = System.nanoTime();

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
        // TODO Auto-generated method stub
        return 0;
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
     * @param nanos long
     * @throws InterruptedException Falls was schief geht.
     */
    protected void sleep(final long nanos) throws InterruptedException
    {
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
