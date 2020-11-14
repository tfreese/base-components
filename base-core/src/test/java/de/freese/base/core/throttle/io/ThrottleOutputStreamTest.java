/**
 * Created: 29.03.2020
 */

package de.freese.base.core.throttle.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import de.freese.base.core.io.AbstractIoTest;
import de.freese.base.core.io.throttle.ThrottleOutputStream;
import de.freese.base.core.throttle.Throttle;
import de.freese.base.core.throttle.UnderstandableThrottle;
import de.freese.base.core.throttle.google.GoogleThrottle;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class ThrottleOutputStreamTest extends AbstractIoTest
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
     * @param throttleFunction {@link Function}
     * @param permits int
     * @throws IOException Falls was schief geht.
     */
    private void doTest(final Function<Integer, Throttle> throttleFunction, final int permits) throws IOException
    {
        try (OutputStream os = Files.newOutputStream(createFile("file_Test1.txt"));
             ThrottleOutputStream throttledStream = new ThrottleOutputStream(os, throttleFunction.apply(permits)))
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
     * @param name String
     * @param throttleFunction {@link Function}
     * @throws IOException Falls was schief geht.
     */
    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("createThrottler")
    void test3000Permits(final String name, final Function<Integer, Throttle> throttleFunction) throws IOException
    {
        doTest(throttleFunction, 3000);
    }

    /**
     * @param name String
     * @param throttleFunction {@link Function}
     * @throws IOException Falls was schief geht.
     */
    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("createThrottler")
    void test4000Permits(final String name, final Function<Integer, Throttle> throttleFunction) throws IOException
    {
        doTest(throttleFunction, 4000);
    }

    /**
     * @param name String
     * @param throttleFunction {@link Function}
     * @throws IOException Falls was schief geht.
     */
    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("createThrottler")
    void test5000Permits(final String name, final Function<Integer, Throttle> throttleFunction) throws IOException
    {
        doTest(throttleFunction, 5000);
    }
}
