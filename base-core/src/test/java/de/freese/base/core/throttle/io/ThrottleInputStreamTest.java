/**
 * Created: 29.03.2020
 */

package de.freese.base.core.throttle.io;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;
import de.freese.base.core.io.AbstractIoTest;
import de.freese.base.core.throttle.Throttle;

/**
 * @author Thomas Freese
 */
public class ThrottleInputStreamTest extends AbstractIoTest
{
    /**
     * @throws IOException Falls was schief geht.
     */
    @Test
    void test1() throws IOException
    {
        try (InputStream is = Files.newInputStream(PATH_FILE_10kB);
             ThrottleInputStream throttledStream = new ThrottleInputStream(is, Throttle.create(2000)))
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

            System.out.println("test1 : " + throttledStream.toString());
        }
    }

    /**
     * @throws IOException Falls was schief geht.
     */
    @Test
    void test2() throws IOException
    {
        try (InputStream is = Files.newInputStream(PATH_FILE_10kB);
             ThrottleInputStream throttledStream = new ThrottleInputStream(is, Throttle.create(3000));
             BufferedInputStream bufferedInputStream = new BufferedInputStream(throttledStream))
        {
            while (bufferedInputStream.read() != -1)
            {
                // NO-OP
            }

            System.out.println("test2 : " + throttledStream.toString());
        }
    }

    /**
     * @throws IOException Falls was schief geht.
     */
    @Test
    void test3() throws IOException
    {
        try (InputStream is = Files.newInputStream(PATH_FILE_10kB);
             ThrottleInputStream throttledStream = new ThrottleInputStream(is, Throttle.create(4000));
             BufferedReader br = new BufferedReader(new InputStreamReader(throttledStream)))
        {
            while (br.readLine() != null)
            {
                // NO-OP
            }

            System.out.println("test3 : " + throttledStream.toString());
        }
    }
}
