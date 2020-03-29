/**
 * Created: 29.03.2020
 */

package de.freese.base.core.throttle.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;
import de.freese.base.core.io.AbstractIoTest;

/**
 * @author Thomas Freese
 */
public class SleepThrottledOutputStreamTest extends AbstractIoTest
{
    /**
     * @throws IOException Falls was schief geht.
     */
    @Test
    void test1() throws IOException
    {
        try (OutputStream os = Files.newOutputStream(createFile("file_Test1.txt"));
             SleepThrottledOutputStream throttledStream = new SleepThrottledOutputStream(os, 2000))
        {
            String str = "Hello World, Throttled Stream\n";

            for (int i = 0; i < 500; i++)
            {
                throttledStream.write(str.getBytes());
            }

            System.out.println("test1 : " + throttledStream.toString());
        }
    }

    /**
     * @throws IOException Falls was schief geht.
     */
    @Test
    void test2() throws IOException
    {
        try (OutputStream os = Files.newOutputStream(createFile("file_Test2.txt"));
             SleepThrottledOutputStream throttledStream = new SleepThrottledOutputStream(os, 3000))
        {
            String str = "Hello World, Throttled Stream\n";

            for (int i = 0; i < 500; i++)
            {
                throttledStream.write(str.getBytes());
            }

            System.out.println("test2 : " + throttledStream.toString());
        }
    }
}
