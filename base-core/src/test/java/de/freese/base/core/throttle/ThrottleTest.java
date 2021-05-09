// Created: 29.03.2020
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
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import de.freese.base.core.throttle.google.GoogleNanoThrottle;
import de.freese.base.core.throttle.google.GoogleThrottle;

/**
 * The following tests were adapted directly from com.google.common.util.concurrent.RateLimiterTest. Changes were made to test the non-burst behavior of
 * GoogleThrottle, to ensure the rate limit is not exceeded over the period of one second.
 *
 * @author Dimitris Andreou - Original RateLimiterTest author
 * @author James P Edwards
 * @author Thomas Freese
 */
class ThrottleTest
{
    /**
     * 7 ms
     */
    private static final long FIRST_DELTA = 7;

    /**
     * 6 ms
     */
    private static final long SECOND_DELTA = 6;

    /**
     * @return {@link Stream}
     */
    static Stream<Arguments> createThrottler()
    {
        // @formatter:off
        return Stream.of(
                Arguments.of("GoogleThrottle", (Function<Double, Throttle>) (permits -> GoogleThrottle.create(permits)))
                , Arguments.of("UnderstandableThrottle", (Function<Double, Throttle>) (permits -> UnderstandableThrottle.create(permits)))
                );
        // @formatter:on
    }

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
    static void warmup()
    {
        GoogleThrottle.create(100.0);
    }

    /**
     * @param name String
     * @param throttleFunction {@link Function}
     * @throws InterruptedException Falls was schief geht.
     */
    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("createThrottler")
    void testAcquire(final String name, final Function<Double, Throttle> throttleFunction) throws InterruptedException
    {
        final Throttle throttle = throttleFunction.apply(5.0D);
        assertEquals(0.0, throttle.acquire(), 0.0);
        assertEquals(200, NANOSECONDS.toMillis(throttle.acquireUnchecked()), FIRST_DELTA);
        assertEquals(200, NANOSECONDS.toMillis(throttle.acquire()), SECOND_DELTA);
        assertEquals(200, NANOSECONDS.toMillis(throttle.acquire(5000)), SECOND_DELTA);
    }

    /**
     * @param name String
     * @param throttleFunction {@link Function}
     * @throws InterruptedException Falls was schief geht.
     */
    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("createThrottler")
    void testAcquireAndUpdate(final String name, final Function<Double, Throttle> throttleFunction) throws InterruptedException
    {
        final Throttle throttle = throttleFunction.apply(10.0D);
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
    void testAcquireDelayDuration() throws InterruptedException
    {
        final Throttle throttle = new GoogleNanoThrottle.GoldFish(5.0, 1.0, false);
        long sleep = throttle.acquireDelayDuration(1);
        TimeUnit.NANOSECONDS.sleep(sleep);
        assertEquals(0.0, sleep / Throttle.ONE_SECOND_NANOS, 0.0);

        sleep = throttle.acquireDelayDuration(1);
        TimeUnit.NANOSECONDS.sleep(sleep);
        assertEquals(0.20, sleep / Throttle.ONE_SECOND_NANOS, FIRST_DELTA);

        sleep = throttle.acquireDelayDuration(1);
        TimeUnit.NANOSECONDS.sleep(sleep);
        assertEquals(0.20, sleep / Throttle.ONE_SECOND_NANOS, SECOND_DELTA);
    }

    /**
     * @throws InterruptedException Falls was schief geht.
     */
    @Test
    void testAcquireParameterValidation() throws InterruptedException
    {
        final GoogleThrottle throttle = GoogleThrottle.create(999);

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
     * @param name String
     * @param throttleFunction {@link Function}
     * @throws InterruptedException Falls was schief geht.
     */
    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("createThrottler")
    void testAcquireWeights(final String name, final Function<Double, Throttle> throttleFunction) throws InterruptedException
    {
        final Throttle throttle = throttleFunction.apply(20.0D);
        assertEquals(0.00, throttle.acquireUnchecked(1), FIRST_DELTA);
        assertEquals(50, NANOSECONDS.toMillis(throttle.acquire(1)), SECOND_DELTA);
        assertEquals(50, NANOSECONDS.toMillis(throttle.acquire(2)), SECOND_DELTA);
        assertEquals(100, NANOSECONDS.toMillis(throttle.acquire(4)), SECOND_DELTA);
        assertEquals(200, NANOSECONDS.toMillis(throttle.acquire(8)), SECOND_DELTA);
        assertEquals(400, NANOSECONDS.toMillis(throttle.acquire(1)), SECOND_DELTA);
    }

    /**
     * @param name String
     * @param throttleFunction {@link Function}
     * @throws InterruptedException Falls was schief geht.
     */
    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("createThrottler")
    void testAcquireWithDoubleWait(final String name, final Function<Double, Throttle> throttleFunction) throws InterruptedException
    {
        final Throttle throttle = throttleFunction.apply(50.0D);
        assertEquals(0.0, throttle.acquire(), 0.0);

        TimeUnit.MILLISECONDS.sleep(40);

        assertEquals(0.0, throttle.acquire(), 0.0);
        assertEquals(20, NANOSECONDS.toMillis(throttle.acquire()), SECOND_DELTA);
        assertEquals(20, NANOSECONDS.toMillis(throttle.acquire()), SECOND_DELTA);
    }

    /**
     * @param name String
     * @param throttleFunction {@link Function}
     * @throws InterruptedException Falls was schief geht.
     */
    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("createThrottler")
    void testAcquireWithWait(final String name, final Function<Double, Throttle> throttleFunction) throws InterruptedException
    {
        final Throttle throttle = throttleFunction.apply(50.0D);
        assertEquals(0.0, throttle.acquire(), 0.0);

        TimeUnit.MILLISECONDS.sleep(20);

        assertEquals(0.0, throttle.acquire(), 0.0);
        assertEquals(20, NANOSECONDS.toMillis(throttle.acquire()), SECOND_DELTA);
    }

    /**
     * @throws InterruptedException Falls was schief geht.
     */
    @Test
    void testDoubleMinValueCanAcquireExactlyOnce() throws InterruptedException
    {
        final GoogleThrottle throttle = GoogleThrottle.create(Double.MIN_VALUE);
        assertTrue(throttle.tryAcquire(), "Unable to acquire initial permit");
        assertFalse(throttle.tryAcquire(), "Capable of acquiring an additional permit");

        TimeUnit.MILLISECONDS.sleep(10);

        assertFalse(throttle.tryAcquire(), "Capable of acquiring an additional permit after sleeping");
    }

    /**
     * @throws InterruptedException Falls was schief geht.
     */
    @Test
    void testIllegalConstructorArgs() throws InterruptedException
    {
        try
        {
            GoogleThrottle.create(Double.POSITIVE_INFINITY);
            fail();
        }
        catch (RuntimeException expected)
        {
            // Empty
        }

        try
        {
            GoogleThrottle.create(Double.NEGATIVE_INFINITY);
            fail();
        }
        catch (RuntimeException expected)
        {
            // Empty
        }

        try
        {
            GoogleThrottle.create(Double.NaN);
            fail();
        }
        catch (RuntimeException expected)
        {
            // Empty
        }

        try
        {
            GoogleThrottle.create(-.0000001);
            fail();
        }
        catch (RuntimeException expected)
        {
            // Empty
        }

        try
        {
            final GoogleThrottle throttle = GoogleThrottle.create(1.0);
            throttle.setRate(Double.POSITIVE_INFINITY);
            fail();
        }
        catch (RuntimeException expected)
        {
            // Empty
        }

        try
        {
            final GoogleThrottle throttle = GoogleThrottle.create(1.0);
            throttle.setRate(Double.NaN);
            fail();
        }
        catch (RuntimeException expected)
        {
            // Empty
        }
    }

    /**
     * @throws InterruptedException Falls was schief geht.
     */
    @Test
    void testImmediateTryAcquire() throws InterruptedException
    {
        final GoogleThrottle throttle = GoogleThrottle.create(1.0);
        assertTrue(throttle.tryAcquire(), "Unable to acquire initial permit");
        assertFalse(throttle.tryAcquire(), "Capable of acquiring secondary permit");
    }

    /**
     * Dieser Test schlägt felh, solange die Methode {@link GoogleThrottle#sleepUninterruptibly} true ist.
     *
     * @throws InterruptedException Falls was schief geht.
     */
    @Test
    void testInterruptUnchecked() throws InterruptedException
    {
        if (!GoogleThrottle.sleepUninterruptibly)
        {
            final GoogleThrottle throttle = GoogleThrottle.create(1);
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
     * @param name String
     * @param throttleFunction {@link Function}
     * @throws InterruptedException Falls was schief geht.
     */
    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("createThrottler")
    void testManyPermits(final String name, final Function<Double, Throttle> throttleFunction) throws InterruptedException
    {
        final Throttle throttle = throttleFunction.apply(50.0D);
        assertEquals(0.0, NANOSECONDS.toMillis(throttle.acquire()), 0.0);
        assertEquals(20, NANOSECONDS.toMillis(throttle.acquire()), FIRST_DELTA);
        assertEquals(20, NANOSECONDS.toMillis(throttle.acquire(3)), SECOND_DELTA);
        assertEquals(60, NANOSECONDS.toMillis(throttle.acquire()), SECOND_DELTA);
        assertEquals(20, NANOSECONDS.toMillis(throttle.acquire()), SECOND_DELTA);
    }

    /**
     * @param name String
     * @param throttleFunction {@link Function}
     * @throws InterruptedException Falls was schief geht.
     */
    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("createThrottler")
    void testMax(final String name, final Function<Double, Throttle> throttleFunction) throws InterruptedException
    {
        final Throttle throttle = throttleFunction.apply(Double.MAX_VALUE);

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
     * @param name String
     * @param throttleFunction {@link Function}
     */
    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("createThrottler")
    void testSimpleRateUpdate(final String name, final Function<Double, Throttle> throttleFunction)
    {
        final Throttle throttle = throttleFunction.apply(5.0D);
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
            // Empty
        }

        try
        {
            throttle.setRate(-10.0);
            fail();
        }
        catch (IllegalArgumentException expected)
        {
            // Empty
        }
    }

    /**
     * @param name String
     * @param throttleFunction {@link Function}
     */
    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("createThrottler")
    void testStream(final String name, final Function<Double, Throttle> throttleFunction)
    {
        final int qps = 1_024;
        final Throttle throttle = throttleFunction.apply((double) qps);

        // warm-up
        IntStream stream = IntStream.range(0, 128).parallel();
        stream.forEach(index -> throttle.acquireUnchecked());

        stream = IntStream.range(0, qps + 1).parallel();
        long start = System.nanoTime();
        stream.forEach(index -> throttle.acquireUnchecked());
        long duration = TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
        assertTrue((duration >= 1000) && (duration < 1050), "Expected duration between 1,000 and 1,050ms. Observed " + duration);

        // final GoogleThrottle fairGoogleThrottle = GoogleThrottle.create(qps, true);
        //
        // // warm-up
        // IntStream.range(0, 128).parallel().forEach(index -> fairGoogleThrottle.acquireUnchecked());
        //
        // stream = IntStream.range(0, qps + 1).parallel();
        // start = System.nanoTime();
        // stream.forEach(index -> fairGoogleThrottle.tryAcquireUnchecked(200_000_000, TimeUnit.NANOSECONDS));
        // duration = TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
        // assertTrue((duration >= 1000) && (duration < 1050), "Expected duration between 1,000 and 1,050ms. Observed " + duration);
    }

    /**
     * @throws InterruptedException Falls was schief geht.
     */
    @Test
    void testTryAcquireNegative() throws InterruptedException
    {
        final GoogleThrottle throttle = GoogleThrottle.create(50.0);
        assertTrue(throttle.tryAcquire(5, 0, TimeUnit.SECONDS));

        TimeUnit.MILLISECONDS.sleep(90);
        assertFalse(throttle.tryAcquire(1, Long.MIN_VALUE, TimeUnit.SECONDS));

        TimeUnit.MILLISECONDS.sleep(90);
        assertTrue(throttle.tryAcquire(1, -1, TimeUnit.SECONDS));
    }

    /**
     * @throws InterruptedException Falls was schief geht.
     */
    @Test
    void testTryAcquireNoWaitAllowed() throws InterruptedException
    {
        final GoogleThrottle throttle = GoogleThrottle.create(50.0);
        assertTrue(throttle.tryAcquire());
        assertFalse(throttle.tryAcquireUnchecked(0, TimeUnit.SECONDS));
        assertFalse(throttle.tryAcquire(0, TimeUnit.SECONDS));

        TimeUnit.MILLISECONDS.sleep(10);
        assertFalse(throttle.tryAcquire(0, TimeUnit.SECONDS));
    }

    /**
     * @throws InterruptedException Falls was schief geht.
     */
    @Test
    void testTryAcquireOverflow() throws InterruptedException
    {
        final GoogleThrottle throttle = GoogleThrottle.create(50.0);
        assertTrue(throttle.tryAcquire(0, TimeUnit.MICROSECONDS));

        TimeUnit.MILLISECONDS.sleep(10);
        assertTrue(throttle.tryAcquire(Long.MAX_VALUE, TimeUnit.MICROSECONDS));
    }

    /**
     * @throws InterruptedException Falls was schief geht.
     */
    @Test
    void testTryAcquireSomeWaitAllowed() throws InterruptedException
    {
        final GoogleThrottle throttle = GoogleThrottle.create(50.0);
        assertTrue(throttle.tryAcquire(0, TimeUnit.SECONDS));
        assertTrue(throttle.tryAcquire(20, TimeUnit.MILLISECONDS));
        assertFalse(throttle.tryAcquire(10, TimeUnit.MILLISECONDS));

        TimeUnit.MILLISECONDS.sleep(10);
        assertTrue(throttle.tryAcquire(10, TimeUnit.MILLISECONDS));
    }

    /**
     * @param name String
     * @param throttleFunction {@link Function}
     * @throws InterruptedException Falls was schief geht.
     */
    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("createThrottler")
    void testWeNeverGetABurstMoreThanOneSec(final String name, final Function<Double, Throttle> throttleFunction) throws InterruptedException
    {
        final Throttle throttle = throttleFunction.apply(100.0);
        final int[] rates =
        {
                10000, 100, 1000000, 1000, 100
        };

        for (final int oneSecWorthOfWork : rates)
        {
            throttle.setRate(oneSecWorthOfWork);
            final int oneHundredMillisWorthOfWork = (int) (oneSecWorthOfWork / 10.0);
            long durationMillis = measureTotalTimeMillis(throttle, oneHundredMillisWorthOfWork);
            assertEquals(100.0, durationMillis, 20.0);
            durationMillis = measureTotalTimeMillis(throttle, oneHundredMillisWorthOfWork);
            assertEquals(100.0, durationMillis, 20.0);
        }
    }
}
