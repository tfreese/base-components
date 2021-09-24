// Created: 29.03.2020
package de.freese.base.core.throttle.io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import de.freese.base.core.io.AbstractIoTest;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
@Execution(ExecutionMode.CONCURRENT)
class SleepThrottledOutputStreamTest extends AbstractIoTest
{
    /**
     * @param permits int
     *
     * @throws IOException Falls was schief geht.
     */
    private void doTest(final int permits) throws IOException
    {
        try (OutputStream os = Files.newOutputStream(createFile("file_Test1.txt"));
             SleepThrottledOutputStream throttledStream = new SleepThrottledOutputStream(os, permits))
        {
            String str = "Hello World, Throttled Stream\n";

            for (int i = 0; i < 250; i++)
            {
                throttledStream.write(str.getBytes());
            }

            throttledStream.flush();

            System.out.println(throttledStream.toString());

            assertEquals(permits, throttledStream.getBytesPerSec(), permits * 0.02D); // Max. 2% Abweichung
        }
    }

    /**
     * @throws IOException Falls was schief geht.
     */
    @Test
    void testPermits3000() throws IOException
    {
        doTest(3000);
    }

    /**
     * @throws IOException Falls was schief geht.
     */
    @Test
    void testPermits4000() throws IOException
    {
        doTest(4000);
    }

    /**
     * @throws IOException Falls was schief geht.
     */
    @Test
    void testPermits5000() throws IOException
    {
        doTest(5000);
    }
}
