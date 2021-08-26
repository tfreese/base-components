// Created: 30.04.2020
package de.freese.base.benchmark;

import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * @author Thomas Freese
 */
public class BaseCoreBenchmarks
{
    /**
     * @param args String[]
     *
     * @throws Exception Falls was schief geht.
     */
    public static void main(final String[] args) throws Exception
    {
        // String filePrefix = "result_" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());

        // Der Builder überschreibt die Klassen-Annotationen.
        // @formatter:off
        Options options = new OptionsBuilder()
                .include(RemoveControlCharactersBenchmark.class.getSimpleName())
                .include(SpliteratorBenchmark.class.getSimpleName())
                //.addProfiler(GCProfiler.class)
                //.addProfiler(HotspotMemoryProfiler.class)
                //.addProfiler(MaxMemoryProfiler.class)
                //.mode(Mode.Throughput)
                //.mode(Mode.AverageTime).timeUnit(TimeUnit.MICROSECONDS)
                //.warmupIterations(1).warmupTime(TimeValue.seconds(1))
                //.measurementIterations(3).measurementTime(TimeValue.seconds(1))
                //.forks(1) // Fork multipliziert die Anzahl der Iterationen
                //.jvmArgs("-disablesystemassertions")
                //.threads(1) // Anzahl paralleler Ausführungen
                .resultFormat(ResultFormatType.CSV)
                //.result("result.csv")
                .result("/dev/null")
                //.output("result.log")
                .build()
                ;
        // @formatter:on

        new Runner(options).run();

        // Collection<RunResult> results = new Runner(options).run();
        //
//        // @formatter:off
//        results.stream()
//            .map(RunResult::getPrimaryResult)
//            .sorted(Comparator.comparing(Result::getScore, Comparator.reverseOrder()))
//            .forEach(r -> System.out.printf("Benchmark score: %f %s over %d iterations%n", r.getScore(), r.getScoreUnit(), r.getStatistics().getN()))
//            ;
//        // @formatter:on
    }
}
