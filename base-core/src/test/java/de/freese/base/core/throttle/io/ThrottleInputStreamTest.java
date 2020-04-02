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
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import de.freese.base.core.io.AbstractIoTest;
import de.freese.base.core.throttle.Throttle;
import de.freese.base.core.throttle.UnderstandableThrottle;
import de.freese.base.core.throttle.google.GoogleThrottle;

/**
 * @author Thomas Freese
 */
public class ThrottleInputStreamTest extends AbstractIoTest
{
    /**
     * @return {@link Stream}
     */
    static Stream<Arguments> createThrottler()
    {
        // @formatter:off
        return Stream.of(
                Arguments.of("GoogleThrottle", (Function<Integer, Throttle>) (permits -> GoogleThrottle.create(permits)))
                , Arguments.of("UnderstandableThrottle", (Function<Integer, Throttle>) (permits -> UnderstandableThrottle.create(permits)))
                );
        // @formatter:on
    }

    /**
     * @param name String
     * @param throttleFunction {@link Function}
     * @throws IOException Falls was schief geht.
     */
    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("createThrottler")
    void test1(final String name, final Function<Integer, Throttle> throttleFunction) throws IOException
    {
        try (InputStream is = Files.newInputStream(PATH_FILE_10kB);
             ThrottleInputStream throttledStream = new ThrottleInputStream(is, throttleFunction.apply(2000)))
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
     * @param name String
     * @param throttleFunction {@link Function}
     * @throws IOException Falls was schief geht.
     */
    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("createThrottler")
    void test2(final String name, final Function<Integer, Throttle> throttleFunction) throws IOException
    {
        try (InputStream is = Files.newInputStream(PATH_FILE_10kB);
             ThrottleInputStream throttledStream = new ThrottleInputStream(is, throttleFunction.apply(3000));
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
     * @param name String
     * @param throttleFunction {@link Function}
     * @throws IOException Falls was schief geht.
     */
    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("createThrottler")
    void test3(final String name, final Function<Integer, Throttle> throttleFunction) throws IOException
    {
        try (InputStream is = Files.newInputStream(PATH_FILE_10kB);
             ThrottleInputStream throttledStream = new ThrottleInputStream(is, throttleFunction.apply(4000));
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
