/**
 * Created: 29.03.2020
 */

package de.freese.base.core.throttle;

import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * The following tests were adapted directly from com.google.common.util.concurrent.RateLimiterTest. Changes were made to test the non-burst behavior of
 * Throttle, to ensure the rate limit is not exceeded over the period of one second.
 *
 * @author Dimitris Andreou - Original RateLimiterTest author
 * @author James P Edwards
 */
public class ThrottleTest
{
    /**
     * 7ms
     */
    private static final long FIRST_DELTA = 7;

    /**
     * 6ms
     */
    private static final long SECOND_DELTA = 6;

    /**
     * @param throttle {@link Throttle}
     * @param permits int
     * @return long
     * @throws InterruptedException Falls was schief geht.
     */
    private static long measureTotalTimeMillis(final Throttle throttle, int permits) throws InterruptedException
    {
        final Random random = ThreadLocalRandom.current();
        final long startTime = System.nanoTime();

        while (permits > 0)
        {
            final int nextPermitsToAcquire = Math.max(1, random.nextInt(permits));
            permits -= nextPermitsToAcquire;
            throttle.acquire(nextPermitsToAcquire);
        }

        throttle.acquire(1); // to repay for any pending debt

        return NANOSECONDS.toMillis(System.nanoTime() - startTime);
    }

    /**
     *
     */
    @BeforeAll
    public static void warmup()
    {
        Throttle.create(100.0);
    }

    /**
     * @throws InterruptedException Falls was schief geht.
     */
    @Test
    public void testAcquire() throws InterruptedException
    {
        final Throttle throttle = Throttle.create(5.0);
        assertEquals(0.0, throttle.acquire(), 0.0);
        assertEquals(200, NANOSECONDS.toMillis(throttle.acquireUnchecked()), FIRST_DELTA);
        assertEquals(200, NANOSECONDS.toMillis(throttle.acquire()), SECOND_DELTA);
    }

    /**
     * @throws InterruptedException Falls was schief geht.
     */
    @Test
    public void testAcquireAndUpdate() throws InterruptedException
    {
        final Throttle throttle = Throttle.create(10.0);
        assertEquals(0.0, NANOSECONDS.toMillis(throttle.acquire(1)), 0.0);
        assertEquals(100, NANOSECONDS.toMillis(throttle.acquire(1)), FIRST_DELTA);

        throttle.setRate(20.0);

        assertEquals(100, NANOSECONDS.toMillis(throttle.acquire(1)), SECOND_DELTA);
        assertEquals(50, NANOSECONDS.toMillis(throttle.acquire(2)), SECOND_DELTA);
        assertEquals(100, NANOSECONDS.toMillis(throttle.acquire(4)), SECOND_DELTA);
        assertEquals(200, NANOSECONDS.toMillis(throttle.acquire(1)), SECOND_DELTA);
    }

    /**
     * @throws InterruptedException Falls was schief geht.
     */
    @Test
    public void testAcquireDelayDuration() throws InterruptedException
    {
        final NanoThrottle throttle = new NanoThrottle.GoldFish(5.0, 1.0, false);
        long sleep = throttle.acquireDelayDuration(1);
        TimeUnit.NANOSECONDS.sleep(sleep);
        assertEquals(0.0, sleep / NanoThrottle.ONE_SECOND_NANOS, 0.0);

        sleep = throttle.acquireDelayDuration(1);
        TimeUnit.NANOSECONDS.sleep(sleep);
        assertEquals(0.20, sleep / NanoThrottle.ONE_SECOND_NANOS, FIRST_DELTA);

        sleep = throttle.acquireDelayDuration(1);
        TimeUnit.NANOSECONDS.sleep(sleep);
        assertEquals(0.20, sleep / NanoThrottle.ONE_SECOND_NANOS, SECOND_DELTA);
    }

    /**
     * @throws InterruptedException Falls was schief geht.
     */
    @Test
    public void testAcquireParameterValidation() throws InterruptedException
    {
        final Throttle throttle = Throttle.create(999);

        try
        {
            throttle.acquire(0);
            fail();
        }
        catch (IllegalArgumentException expected)
        {
        }

        try
        {
            throttle.acquire(-1);
            fail();
        }
        catch (IllegalArgumentException expected)
        {
        }

        try
        {
            throttle.tryAcquire(0);
            fail();
        }
        catch (IllegalArgumentException expected)
        {
        }

        try
        {
            throttle.tryAcquire(-1);
            fail();
        }
        catch (IllegalArgumentException expected)
        {
        }

        try
        {
            throttle.tryAcquireUnchecked(0, 1, TimeUnit.SECONDS);
            fail();
        }
        catch (IllegalArgumentException expected)
        {
        }

        try
        {
            throttle.tryAcquire(-1, 1, TimeUnit.SECONDS);
            fail();
        }
        catch (IllegalArgumentException expected)
        {
        }
    }

    /**
     * @throws InterruptedException Falls was schief geht.
     */
    @Test
    public void testAcquireWeights() throws InterruptedException
    {
        final Throttle throttle = Throttle.create(20.0);
        assertEquals(0.00, throttle.acquireUnchecked(1), FIRST_DELTA);
        assertEquals(50, NANOSECONDS.toMillis(throttle.acquire(1)), SECOND_DELTA);
        assertEquals(50, NANOSECONDS.toMillis(throttle.acquire(2)), SECOND_DELTA);
        assertEquals(100, NANOSECONDS.toMillis(throttle.acquire(4)), SECOND_DELTA);
        assertEquals(200, NANOSECONDS.toMillis(throttle.acquire(8)), SECOND_DELTA);
        assertEquals(400, NANOSECONDS.toMillis(throttle.acquire(1)), SECOND_DELTA);
    }

    /**
     * @throws InterruptedException Falls was schief geht.
     */
    @Test
    public void testAcquireWithDoubleWait() throws InterruptedException
    {
        final Throttle throttle = Throttle.create(50.0);
        assertEquals(0.0, throttle.acquire(), 0.0);

        Thread.sleep(40);
        assertEquals(0.0, throttle.acquire(), 0.0);
        assertEquals(20, NANOSECONDS.toMillis(throttle.acquire()), SECOND_DELTA);
        assertEquals(20, NANOSECONDS.toMillis(throttle.acquire()), SECOND_DELTA);
    }

    /**
     * @throws InterruptedException Falls was schief geht.
     */
    @Test
    public void testAcquireWithWait() throws InterruptedException
    {
        final Throttle throttle = Throttle.create(50.0);
        assertEquals(0.0, throttle.acquire(), 0.0);

        Thread.sleep(20);
        assertEquals(0.0, throttle.acquire(), 0.0);
        assertEquals(20, NANOSECONDS.toMillis(throttle.acquire()), SECOND_DELTA);
    }

    /**
     * @throws InterruptedException Falls was schief geht.
     */
    @Test
    public void testDoubleMinValueCanAcquireExactlyOnce() throws InterruptedException
    {
        final Throttle throttle = Throttle.create(Double.MIN_VALUE);
        assertTrue(throttle.tryAcquire(), "Unable to acquire initial permit");
        assertFalse(throttle.tryAcquire(), "Capable of acquiring an additional permit");

        Thread.sleep(10);
        assertFalse(throttle.tryAcquire(), "Capable of acquiring an additional permit after sleeping");
    }

    /**
     * @throws InterruptedException Falls was schief geht.
     */
    @Test
    public void testIllegalConstructorArgs() throws InterruptedException
    {
        try
        {
            Throttle.create(Double.POSITIVE_INFINITY);
            fail();
        }
        catch (IllegalArgumentException expected)
        {
        }
        try
        {
            Throttle.create(Double.NEGATIVE_INFINITY);
            fail();
        }
        catch (IllegalArgumentException expected)
        {
        }
        try
        {
            Throttle.create(Double.NaN);
            fail();
        }
        catch (IllegalArgumentException expected)
        {
        }
        try
        {
            Throttle.create(-.0000001);
            fail();
        }
        catch (IllegalArgumentException expected)
        {
        }
        try
        {
            final Throttle throttle = Throttle.create(1.0);
            throttle.setRate(Double.POSITIVE_INFINITY);
            fail();
        }
        catch (IllegalArgumentException expected)
        {
        }
        try
        {
            final Throttle throttle = Throttle.create(1.0);
            throttle.setRate(Double.NaN);
            fail();
        }
        catch (IllegalArgumentException expected)
        {
        }
    }

    /**
     * @throws InterruptedException Falls was schief geht.
     */
    @Test
    public void testImmediateTryAcquire() throws InterruptedException
    {
        final Throttle throttle = Throttle.create(1.0);
        assertTrue(throttle.tryAcquire(), "Unable to acquire initial permit");
        assertFalse(throttle.tryAcquire(), "Capable of acquiring secondary permit");
    }

    /**
     * Dieser Test schlägt felh, solange die Methode {@link Throttle#sleepUninterruptibly} true ist.
     *
     * @throws InterruptedException Falls was schief geht.
     */
    @Test
    public void testInterruptUnchecked() throws InterruptedException
    {
        if (!Throttle.sleepUninterruptibly)
        {
            final Throttle throttle = Throttle.create(1);
            throttle.acquireUnchecked(10);

            final CompletableFuture<Throwable> futureEx = new CompletableFuture<>();
            Thread thread = new Thread(() -> {
                try
                {
                    throttle.acquireUnchecked();
                    futureEx.complete(null);
                }
                catch (CompletionException ex)
                {
                    futureEx.complete(ex.getCause());
                }
            });

            thread.start();
            thread.interrupt();
            thread.join();
            assertFalse(throttle.tryAcquire());
            assertEquals(InterruptedException.class, futureEx.join().getClass());

            final CompletableFuture<Throwable> futureEx2 = new CompletableFuture<>();
            thread = new Thread(() -> {
                try
                {
                    throttle.tryAcquireUnchecked(20, TimeUnit.SECONDS);
                    futureEx2.complete(null);
                }
                catch (CompletionException ex)
                {
                    futureEx2.complete(ex.getCause());
                }
            });

            thread.start();
            thread.interrupt();
            thread.join();

            assertFalse(throttle.tryAcquire());
            assertEquals(InterruptedException.class, futureEx2.join().getClass());
        }
    }

    /**
     * @throws InterruptedException Falls was schief geht.
     */
    @Test
    public void testManyPermits() throws InterruptedException
    {
        final Throttle throttle = Throttle.create(50.0);
        assertEquals(0.0, NANOSECONDS.toMillis(throttle.acquire()), 0.0);
        assertEquals(20, NANOSECONDS.toMillis(throttle.acquire()), FIRST_DELTA);
        assertEquals(20, NANOSECONDS.toMillis(throttle.acquire(3)), SECOND_DELTA);
        assertEquals(60, NANOSECONDS.toMillis(throttle.acquire()), SECOND_DELTA);
        assertEquals(20, NANOSECONDS.toMillis(throttle.acquire()), SECOND_DELTA);
    }

    /**
     * @throws InterruptedException Falls was schief geht.
     */
    @Test
    public void testMax() throws InterruptedException
    {
        final Throttle throttle = Throttle.create(Double.MAX_VALUE);

        assertEquals(0.0, throttle.acquire(Integer.MAX_VALUE / 4), 0.0);
        assertEquals(0.0, throttle.acquire(Integer.MAX_VALUE / 2), 0.0);
        assertEquals(0.0, throttle.acquireUnchecked(Integer.MAX_VALUE), 0.0);

        throttle.setRate(20.0);
        assertEquals(0.0, throttle.acquire(), 0.0);
        assertEquals(50, NANOSECONDS.toMillis(throttle.acquire()), SECOND_DELTA);

        throttle.setRate(Double.MAX_VALUE);
        assertEquals(50, NANOSECONDS.toMillis(throttle.acquire()), FIRST_DELTA);
        assertEquals(0.0, throttle.acquire(), 0.0);
        assertEquals(0.0, throttle.acquire(), 0.0);
    }

    /**
     *
     */
    @Test
    public void testSimpleRateUpdate()
    {
        final Throttle throttle = Throttle.create(5.0);
        assertEquals(5.0, throttle.getRate(), 0.0);
        throttle.setRate(10.0);
        assertEquals(10.0, throttle.getRate(), 0.0);

        try
        {
            throttle.setRate(0.0);
            fail();
        }
        catch (IllegalArgumentException expected)
        {
        }

        try
        {
            throttle.setRate(-10.0);
            fail();
        }
        catch (IllegalArgumentException expected)
        {
        }
    }

    /**
     *
     */
    @Test
    public void testStream()
    {
        final int qps = 1_024;
        final Throttle throttle = Throttle.create(qps);

        // warm-up
        IntStream stream = IntStream.range(0, 128).parallel();
        stream.forEach(index -> throttle.acquireUnchecked());

        stream = IntStream.range(0, qps + 1).parallel();
        long start = System.nanoTime();
        stream.forEach(index -> throttle.acquireUnchecked());
        long duration = TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
        assertTrue((duration >= 1000) && (duration < 1050), "Expected duration between 1,000 and 1,050ms. Observed " + duration);

        final Throttle fairThrottle = Throttle.create(qps, true);

        // warm-up
        IntStream.range(0, 128).parallel().forEach(index -> fairThrottle.acquireUnchecked());

        stream = IntStream.range(0, qps + 1).parallel();
        start = System.nanoTime();
        stream.forEach(index -> fairThrottle.tryAcquireUnchecked(200_000_000, TimeUnit.NANOSECONDS));
        duration = TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
        assertTrue((duration >= 1000) && (duration < 1050), "Expected duration between 1,000 and 1,050ms. Observed " + duration);
    }

    /**
     *
     */
    @Test
    public void testToString()
    {
        final Throttle throttle = Throttle.create(100.0);

        assertEquals("Throttle{rate=100.0}", throttle.toString());
    }

    /**
     * @throws InterruptedException Falls was schief geht.
     */
    @Test
    public void testTryAcquire_negative() throws InterruptedException
    {
        final Throttle throttle = Throttle.create(50.0);
        assertTrue(throttle.tryAcquire(5, 0, TimeUnit.SECONDS));

        Thread.sleep(90);
        assertFalse(throttle.tryAcquire(1, Long.MIN_VALUE, TimeUnit.SECONDS));

        Thread.sleep(10);
        assertTrue(throttle.tryAcquire(1, -1, TimeUnit.SECONDS));
    }

    /**
     * @throws InterruptedException Falls was schief geht.
     */
    @Test
    public void testTryAcquire_noWaitAllowed() throws InterruptedException
    {
        final Throttle throttle = Throttle.create(50.0);
        assertTrue(throttle.tryAcquire());
        assertFalse(throttle.tryAcquireUnchecked(0, TimeUnit.SECONDS));
        assertFalse(throttle.tryAcquire(0, TimeUnit.SECONDS));

        Thread.sleep(10);
        assertFalse(throttle.tryAcquire(0, TimeUnit.SECONDS));
    }

    /**
     * @throws InterruptedException Falls was schief geht.
     */
    @Test
    public void testTryAcquire_overflow() throws InterruptedException
    {
        final Throttle throttle = Throttle.create(50.0);
        assertTrue(throttle.tryAcquire(0, TimeUnit.MICROSECONDS));

        Thread.sleep(10);
        assertTrue(throttle.tryAcquire(Long.MAX_VALUE, TimeUnit.MICROSECONDS));
    }

    /**
     * @throws InterruptedException Falls was schief geht.
     */
    @Test
    public void testTryAcquire_someWaitAllowed() throws InterruptedException
    {
        final Throttle throttle = Throttle.create(50.0);
        assertTrue(throttle.tryAcquire(0, TimeUnit.SECONDS));
        assertTrue(throttle.tryAcquire(20, TimeUnit.MILLISECONDS));
        assertFalse(throttle.tryAcquire(10, TimeUnit.MILLISECONDS));

        Thread.sleep(10);
        assertTrue(throttle.tryAcquire(10, TimeUnit.MILLISECONDS));
    }

    /**
     * @throws InterruptedException Falls was schief geht.
     */
    @Test
    public void testWeNeverGetABurstMoreThanOneSec() throws InterruptedException
    {
        final Throttle throttle = Throttle.create(100.0);
        final int[] rates =
        {
                10000, 100, 1000000, 1000, 100
        };

        for (final int oneSecWorthOfWork : rates)
        {
            throttle.setRate(oneSecWorthOfWork);
            final int oneHundredMillisWorthOfWork = (int) (oneSecWorthOfWork / 10.0);
            long durationMillis = measureTotalTimeMillis(throttle, oneHundredMillisWorthOfWork);
            assertEquals(100.0, durationMillis, 15.0);
            durationMillis = measureTotalTimeMillis(throttle, oneHundredMillisWorthOfWork);
            assertEquals(100.0, durationMillis, 15.0);
        }
    }
}
