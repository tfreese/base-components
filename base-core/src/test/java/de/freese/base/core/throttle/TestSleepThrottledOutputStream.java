// Created: 29.03.2020
package de.freese.base.core.throttle;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.OutputStream;

import de.freese.base.core.throttle.io.SleepThrottledOutputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

/**
 * @author Thomas Freese
 */
//@TestMethodOrder(MethodOrderer.MethodName.class)
@Execution(ExecutionMode.CONCURRENT)
class TestSleepThrottledOutputStream //extends AbstractIoTest
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

        try (OutputStream outputStream = OutputStream.nullOutputStream();
             SleepThrottledOutputStream throttledStream = new SleepThrottledOutputStream(outputStream, permits))
        {
            long start = System.nanoTime();

            for (int b = 0; b < size; b++)
            {
                throttledStream.write(b);
            }

            long elapsed = System.nanoTime() - start;
            double rate = size / (elapsed / 1_000_000_000D);

            throttledStream.flush();

            System.out.printf("Time = %.3f ms; Permits = %d; Rate = %.3f b/s%n", (double) elapsed / 1_000_000D, permits, rate);

            assertEquals(permits, rate, permits * 0.02D); // Max. 2% Abweichung
        }
    }
}
