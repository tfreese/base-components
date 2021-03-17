// Created: 13.06.2020
package de.freese.base.benchmark;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;
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
@Threads(8) // Anzahl paralleler Ausführungen
public class SpliteratorBenchmark
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
        public final List<Integer> list;

        /**
         * Erstellt ein neues {@link BenchmarkState} Object.
         */
        public BenchmarkState()
        {
            super();

            System.out.println("SpliteratorBenchmark.BenchmarkState.BenchmarkState()");

            Random random = new SecureRandom();

            int length = 1_000_000;

            this.list = new ArrayList<>(length);

            for (int i = 0; i < length; i++)
            {
                int value = random.nextInt();

                this.list.add(value);
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
     * @param blackhole B{@link Blackhole}
     * @param state {@link BenchmarkState}
     */
    @Benchmark
    public void listMaxDefault(final Blackhole blackhole, final BenchmarkState state)
    {
        OptionalInt optional = state.list.stream().mapToInt(i -> i).max();

        blackhole.consume(optional);
    }

    /**
     * @param blackhole B{@link Blackhole}
     * @param state {@link BenchmarkState}
     */
    @Benchmark
    public void listMaxDefaultParallel(final Blackhole blackhole, final BenchmarkState state)
    {
        OptionalInt optional = state.list.stream().parallel().mapToInt(i -> i).max();

        blackhole.consume(optional);
    }

    /**
     * @param blackhole B{@link Blackhole}
     * @param state {@link BenchmarkState}
     */
    @Benchmark
    public void listMaxIteratorSpliterator(final Blackhole blackhole, final BenchmarkState state)
    {
        Spliterator<Integer> spliterator = Spliterators.spliterator(state.list, 0);

        OptionalInt optional = StreamSupport.stream(spliterator, false).mapToInt(i -> i).max();

        blackhole.consume(optional);
    }

    /**
     * @param blackhole B{@link Blackhole}
     * @param state {@link BenchmarkState}
     */
    @Benchmark
    public void listMaxIteratorSpliteratorParallel(final Blackhole blackhole, final BenchmarkState state)
    {
        Spliterator<Integer> spliterator = Spliterators.spliterator(state.list, 0);

        OptionalInt optional = StreamSupport.stream(spliterator, true).mapToInt(i -> i).max();

        blackhole.consume(optional);
    }
}
