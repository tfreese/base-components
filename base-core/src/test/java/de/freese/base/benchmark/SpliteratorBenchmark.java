/**
 * Created: 13.06.2020
 */

package de.freese.base.benchmark;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import java.util.Spliterator;
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
import de.freese.base.core.collection.stream.spliterator.TunedListSpliterator;

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
public class SpliteratorBenchmark
{
    /**
     * @author Thomas Freese
     */
    @State(Scope.Benchmark)
    public static class BenchmarkState
    {
        // /**
        // *
        // */
        // public final Integer[] array;

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

            // this.array = new Integer[length];
            this.list = new ArrayList<>(length);

            for (int i = 0; i < length; i++)
            {
                int value = random.nextInt();

                // this.array[i] = value;
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
     * Erstellt ein neues {@link SpliteratorBenchmark} Object.
     */
    public SpliteratorBenchmark()
    {
        super();
    }

    /**
     * @param blackhole B{@link Blackhole}
     * @param state {@link BenchmarkState}
     */
    @Benchmark
    public void listMaxDefault(final Blackhole blackhole, final BenchmarkState state)
    {
        // List<Integer> list = new ArrayList<>(state.list.size());
        // list.addAll(state.list);

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
        // List<Integer> list = new ArrayList<>(state.list.size());
        // list.addAll(state.list);

        OptionalInt optional = state.list.stream().parallel().mapToInt(i -> i).max();

        blackhole.consume(optional);
    }

    /**
     * @param blackhole B{@link Blackhole}
     * @param state {@link BenchmarkState}
     */
    @Benchmark
    public void listMaxTuned(final Blackhole blackhole, final BenchmarkState state)
    {
        // List<Integer> list = new ArrayList<>(state.list.size());
        // list.addAll(state.list);

        Spliterator<Integer> spliterator = new TunedListSpliterator<>(state.list);

        OptionalInt optional = StreamSupport.stream(spliterator, false).mapToInt(i -> i).max();

        blackhole.consume(optional);
    }

    /**
     * @param blackhole B{@link Blackhole}
     * @param state {@link BenchmarkState}
     */
    @Benchmark
    public void listMaxTunedParallel(final Blackhole blackhole, final BenchmarkState state)
    {
        // List<Integer> list = new ArrayList<>(state.list.size());
        // list.addAll(state.list);

        Spliterator<Integer> spliterator = new TunedListSpliterator<>(state.list);

        OptionalInt optional = StreamSupport.stream(spliterator, true).mapToInt(i -> i).max();

        blackhole.consume(optional);
    }
}
