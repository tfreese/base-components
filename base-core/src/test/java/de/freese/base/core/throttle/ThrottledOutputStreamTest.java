// Created: 29.03.2020
package de.freese.base.core.throttle;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.OutputStream;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import de.freese.base.core.io.AbstractIoTest;
import de.freese.base.core.throttle.io.SleepThrottledOutputStream;
import de.freese.base.core.throttle.io.ThrottledOutputStream;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * @author Thomas Freese
 */
@Execution(ExecutionMode.CONCURRENT)
class ThrottledOutputStreamTest extends AbstractIoTest
{
    /**
     * @return {@link Stream}
     */
    static Stream<Arguments> createThrottler()
    {
        // @formatter:off
        return Stream.of(
                Arguments.of(
                        "Output - SimpleThrottler",
                        (BiFunction<OutputStream,Integer, OutputStream>) (outputStream, permits) -> new ThrottledOutputStream(outputStream, SimpleThrottler.create(permits))
                )
                , Arguments.of(
                        "Output - Failsafe",
                        (BiFunction<OutputStream,Integer, OutputStream>) (outputStream, permits) -> new ThrottledOutputStream(outputStream, FailsafeThrottlerAdapter.createBuilder(permits))
                )
                , Arguments.of(
                        "Output - Resilience4J",
                        (BiFunction<OutputStream,Integer, OutputStream>) (outputStream, permits) -> new ThrottledOutputStream(outputStream, Resilience4JThrottlerAdapter.createBuilder(permits))
                )
                , Arguments.of(
                        "Output - SleepThrottled",
                        (BiFunction<OutputStream, Integer, OutputStream>) SleepThrottledOutputStream::new
                )
                );
        // @formatter:on
    }

    /**
     *
     */
    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("createThrottler")
    void testPermits2000(final String name, final BiFunction<OutputStream, Integer, OutputStream> throttleFunction) throws IOException
    {
        doTest(name, throttleFunction, 2000);
    }

    /**
     *
     */
    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("createThrottler")
    void testPermits4000(final String name, final BiFunction<OutputStream, Integer, OutputStream> throttleFunction) throws IOException
    {
        doTest(name, throttleFunction, 4000);
    }

    /**
     *
     */
    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("createThrottler")
    void testPermits6000(final String name, final BiFunction<OutputStream, Integer, OutputStream> throttleFunction) throws IOException
    {
        doTest(name, throttleFunction, 6000);
    }

    /**
     *
     */
    private void doTest(String name, final BiFunction<OutputStream, Integer, OutputStream> throttleFunction, final int permits) throws IOException
    {
        int size = 12 * 1024;

        try (OutputStream outputStream = OutputStream.nullOutputStream();
             OutputStream throttledStream = throttleFunction.apply(outputStream, permits))
        {
            long start = System.nanoTime();

            for (int b = 0; b < size; b++)
            {
                throttledStream.write(b);
            }

            long elapsed = System.nanoTime() - start;
            double rate = size / (elapsed / 1_000_000_000D);
            double timeMillis = (double) elapsed / 1_000_000D;
            double delta = Math.abs(rate - permits);

            throttledStream.flush();

            System.out.printf("Name: %-30s; Time = %.3f ms; Permits = %d; Rate = %.3f b/s; Delta = %.3f%n", name, timeMillis, permits, rate, delta);

            assertEquals(permits, rate, permits * 0.03D); // Max. 3% Abweichung
        }
    }
}
