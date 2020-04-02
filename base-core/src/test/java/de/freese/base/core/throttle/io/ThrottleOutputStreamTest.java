/**
 * Created: 29.03.2020
 */

package de.freese.base.core.throttle.io;

import java.io.IOException;
import java.io.OutputStream;
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
public class ThrottleOutputStreamTest extends AbstractIoTest
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
        try (OutputStream os = Files.newOutputStream(createFile("file_Test1.txt"));
             ThrottleOutputStream throttledStream = new ThrottleOutputStream(os, throttleFunction.apply(2000)))
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
     * @param name String
     * @param throttleFunction {@link Function}
     * @throws IOException Falls was schief geht.
     */
    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("createThrottler")
    void test2(final String name, final Function<Integer, Throttle> throttleFunction) throws IOException
    {
        try (OutputStream os = Files.newOutputStream(createFile("file_Test2.txt"));
             ThrottleOutputStream throttledStream = new ThrottleOutputStream(os, throttleFunction.apply(3000)))
        {
            String str = "Hello World, Throttled Stream\n";

            for (int i = 0; i < 500; i++)
            {
                throttledStream.write(str.getBytes());
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
        try (OutputStream os = Files.newOutputStream(createFile("file_Test2.txt"));
             ThrottleOutputStream throttledStream = new ThrottleOutputStream(os, throttleFunction.apply(4000)))
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
