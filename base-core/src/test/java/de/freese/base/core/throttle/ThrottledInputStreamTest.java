// Created: 29.03.2020
package de.freese.base.core.throttle;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import de.freese.base.core.throttle.io.FakeInputStream;
import de.freese.base.core.throttle.io.SleepThrottledInputStream;
import de.freese.base.core.throttle.io.ThrottledInputStream;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * @author Thomas Freese
 */
@Execution(ExecutionMode.CONCURRENT)
class ThrottledInputStreamTest //extends AbstractIoTest
{
    /**
     * @return {@link Stream}
     */
    static Stream<Arguments> createThrottler()
    {
        // @formatter:off
        return Stream.of(
                Arguments.of(
                        "Input - SimpleThrottler",
                        (BiFunction<InputStream, Integer, InputStream>) (inputStream, permits) -> new ThrottledInputStream(inputStream, SimpleThrottler.create(permits))
                )
                , Arguments.of(
                        "Input - Failsafe",
                        (BiFunction<InputStream, Integer, InputStream>) (inputStream, permits) -> new ThrottledInputStream(inputStream, FailsafeThrottlerAdapter.createBuilder(permits))
                )
                , Arguments.of(
                        "Input - Resilience4J",
                        (BiFunction<InputStream, Integer, InputStream>) (inputStream, permits) -> new ThrottledInputStream(inputStream, Resilience4JThrottlerAdapter.createBuilder(permits))
                )
                , Arguments.of(
                        "Input - SleepThrottled",
                        (BiFunction<InputStream, Integer, InputStream>) SleepThrottledInputStream::new
                )
                );
        // @formatter:on
    }

    /**
     *
     */
    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("createThrottler")
    void testPermits2000(final String name, final BiFunction<InputStream, Integer, InputStream> throttleFunction) throws IOException
    {
        doTest(name, 2000, throttleFunction);
    }

    /**
     *
     */
    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("createThrottler")
    void testPermits4000(final String name, final BiFunction<InputStream, Integer, InputStream> throttleFunction) throws IOException
    {
        doTest(name, 4000, throttleFunction);
    }

    /**
     *
     */
    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("createThrottler")
    void testPermits6000(final String name, final BiFunction<InputStream, Integer, InputStream> throttleFunction) throws IOException
    {
        doTest(name, 6000, throttleFunction);
    }

    /**
     *
     */
    private void doTest(String name, final int permits, final BiFunction<InputStream, Integer, InputStream> throttleFunction) throws IOException
    {
        int size = 12 * 1024;

        try (InputStream inputStream = new FakeInputStream(size);
             InputStream throttledStream = throttleFunction.apply(inputStream, permits))
        {
            long start = System.nanoTime();

            while (throttledStream.read() != -1)
            {
                // Empty
            }

            long elapsed = System.nanoTime() - start;
            double rate = size / (elapsed / 1_000_000_000D);
            double timeMillis = (double) elapsed / 1_000_000D;
            double delta = Math.abs(rate - permits);

            System.out.printf("Name: %-30s; Time = %.3f ms; Permits = %d; Rate = %.3f b/s; Delta = %.3f%n", name, timeMillis, permits, rate, delta);

            assertEquals(permits, rate, permits * 0.03D); // Max. 3% Abweichung
        }
    }
}
