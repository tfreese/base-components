/**
 * Created: 13.06.2020
 */

package de.freese.base.benchmark;

import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

/**
 * @author Thomas Freese
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1, jvmArgsAppend =
{
        // Fork multipliziert die Anzahl der Iterationen
        "-disablesystemassertions"
})
@Threads(1) // Anzahl paralleler Ausführungen
public class RemoveControlCharactersBenchmark
{
    /**
     * @author Thomas Freese
     */
    @State(Scope.Benchmark)
    public static class BenchmarkState
    {
        /**
        *
        */
        public String longString = null;

        /**
         * Erstellt ein neues {@link BenchmarkState} Object.
         */
        public BenchmarkState()
        {
            super();

            System.out.println("RemoveControlCharactersBenchmark.BenchmarkState.BenchmarkState()");
            this.longString = "TestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTest";

            for (int i = 0; i < 20; i++)
            {
                this.longString += this.longString;
            }
        }

        // /**
        // *
        // */
        // @TearDown
        // public void close()
        // {
        // // Empty
        // }
        //
        // /**
        // * Jede Measurement-Iteration bekommt eine neue Objekt-Struktur.
        // */
        // @Setup(Level.Iteration)
        // public void preInit()
        // {
        // // Empty
        // }
        //
        // /**
        // * Jede Durchlauf bekommt einen neuen String.
        // */
        // @Setup(Level.Trial)
        // public void setUp()
        // {
        // System.out.println("RemoveControlCharactersBenchmark.BenchmarkState.setUp()");
        // this.longString = "TestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTest";
        //
        // for (int i = 0; i < 30; i++)
        // {
        // this.longString += this.longString;
        // }
        // }
    }

    /**
     * Erstellt ein neues {@link RemoveControlCharactersBenchmark} Object.
     */
    public RemoveControlCharactersBenchmark()
    {
        super();
    }

    /**
     * @param blackhole B{@link Blackhole}
     * @param state {@link BenchmarkState}
     */
    @Benchmark
    public void removeControlCharactersArray(final Blackhole blackhole, final BenchmarkState state)
    {
        int pos = 0;
        char[] chars = state.longString.toCharArray();

        for (char c : chars)
        {
            if (c < 32)
            {
                switch (c)
                {
                    case 9: // ASCII 09 (HorizontalTab)
                    case 10: // ASCII 10 (LineFeed)
                    case 13: // ASCII 13 (CarriageReturn)
                        break;
                    default:
                        continue;
                }
            }
            else if (c > 126)
            {
                continue;
            }

            chars[pos++] = c;
        }

        blackhole.consume(new String(chars, 0, pos));
    }

    /**
     * @param blackhole B{@link Blackhole}
     * @param state {@link BenchmarkState}
     */
    @Benchmark
    public void removeControlCharactersStringBuilder(final Blackhole blackhole, final BenchmarkState state)
    {
        char[] chars = state.longString.toCharArray();
        StringBuilder sb = new StringBuilder();

        for (char c : chars)
        {
            if (c < 32)
            {
                switch (c)
                {
                    case 9: // ASCII 09 (HorizontalTab)
                    case 10: // ASCII 10 (LineFeed)
                    case 13: // ASCII 13 (CarriageReturn)
                        break;
                    default:
                        continue;
                }
            }
            else if (c > 126)
            {
                continue;
            }

            sb.append(c);
        }

        // return new String(chars, 0, pos);
        blackhole.consume(sb.toString());
    }
}
