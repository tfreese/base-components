package de.freese.base.persistence.jdbc.benchmark;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * https://github.com/r2dbc/r2dbc-h2/tree/main/src/jmh/java/io/r2dbc/h2<br>
 * <br>
 * Console:<br>
 * - java -jar target/benchmarks.jar -h<br>
 * - java -jar target/benchmarks.jar -rf csv -rff results.csv<br>
 * - java -jar target/benchmarks.jar -bm thrpt -f 1 -wi 2 -w 2s -i 3 -r 2s -jvmArgs '-server -disablesystemassertions' -rf csv -rff results.csv<br>
 * - gnuplot benchmark.plt<br>
 *
 * @author Thomas Freese
 */
public class BenchmarkRunner
{
    /**
     * @param args String[]
     *
     * @throws Exception Falls was schiefgeht
     */
    public static void main(String[] args) throws Exception
    {
        // Der Builder überschreibt die Klassen-Annotationen.
        // Benötigt befüllte target\classes\META-INF\BenchmarkList -> Rebuild vor der Ausführung notwendig
        // @formatter:off
        Options options = new OptionsBuilder()
                .include(StatementBenchmarks.class.getSimpleName())
                .include(StagedResultSizeBenchmarks.class.getSimpleName())
                .shouldFailOnError(true)
                //.addProfiler(GCProfiler.class)
                //.addProfiler(HotspotMemoryProfiler.class)
                //.addProfiler(MaxMemoryProfiler.class)
                //.mode(Mode.Throughput)
                //.mode(Mode.AverageTime).timeUnit(TimeUnit.MICROSECONDS)
                //.jvmArgs("-disablesystemassertions")
                //.threads(1) // Anzahl paralleler Ausführungen
                //.verbosity(VerboseMode.SILENT) // Ohne Ausgaben
//                .forks(0).warmupForks(0) // Zum Debuggen
//                .warmupIterations(0).warmupTime(TimeValue.milliseconds(500))
//                .measurementIterations(1).measurementTime(TimeValue.milliseconds(1000))
                //.resultFormat(ResultFormatType.TEXT)
                //.resultFormat(ResultFormatType.CSV).result("benchmark.csv") // .result("/dev/null")
                //.output("result.log")
                .build()
                ;
        // @formatter:on

        new Runner(options).run();
        //Collection<RunResult> results = new Runner(options).run();
    }
}
