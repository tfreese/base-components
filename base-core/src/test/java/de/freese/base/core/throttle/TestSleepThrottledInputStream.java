// Created: 29.03.2020
package de.freese.base.core.throttle;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;

import de.freese.base.core.throttle.io.FakeInputStream;
import de.freese.base.core.throttle.io.SleepThrottledInputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

/**
 * @author Thomas Freese
 */
//@TestMethodOrder(MethodOrderer.MethodName.class)
@Execution(ExecutionMode.CONCURRENT)
class TestSleepThrottledInputStream //extends AbstractIoTest
{
    /**
     * @throws IOException Falls was schiefgeht.
     */
    @Test
    void testPermits2000() throws IOException
    {
        doTest(2000);
    }

    /**
     * @throws IOException Falls was schiefgeht.
     */
    @Test
    void testPermits4000() throws IOException
    {
        doTest(4000);
    }

    /**
     * @throws IOException Falls was schiefgeht.
     */
    @Test
    void testPermits6000() throws IOException
    {
        doTest(6000);
    }

    /**
     * @param permits int
     *
     * @throws IOException Falls was schiefgeht.
     */
    private void doTest(final int permits) throws IOException
    {
        int size = 12 * 1024;

        try (InputStream inputStream = new FakeInputStream(size);
             SleepThrottledInputStream throttledStream = new SleepThrottledInputStream(inputStream, permits))
        {
            long start = System.nanoTime();

            while (throttledStream.read() != -1)
            {
                // Empty
            }

            long elapsed = System.nanoTime() - start;
            double rate = size / (elapsed / 1_000_000_000D);

            System.out.printf("Time = %.3f ms; Permits = %d; Rate = %.3f b/s%n", (double) elapsed / 1_000_000D, permits, rate);
            //            System.out.println(throttledStream);

            assertEquals(permits, rate, permits * 0.02D); // Max. 2% Abweichung
        }
    }
}
