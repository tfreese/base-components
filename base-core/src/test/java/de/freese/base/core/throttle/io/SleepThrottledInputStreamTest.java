/**
 * Created: 29.03.2020
 */

package de.freese.base.core.throttle.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import java.io.InputStream;
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
class SleepThrottledInputStreamTest extends AbstractIoTest
{
    /**
     * @param permits int
     * @throws IOException Falls was schief geht.
     */
    private void doTest(final int permits) throws IOException
    {
        try (InputStream is = Files.newInputStream(createFile(SIZE_10kb));
             SleepThrottledInputStream throttledStream = new SleepThrottledInputStream(is, permits))
        {
            // int data = 0;
            //
            // while ((data = throttledStream.read()) != -1)
            // {
            // System.out.print((char) data);
            // }

            while (throttledStream.read() != -1)
            {
                // NO-OP
            }

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
