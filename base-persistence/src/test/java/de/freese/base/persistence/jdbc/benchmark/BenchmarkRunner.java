package de.freese.base.persistence.jdbc.benchmark;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * https://github.com/r2dbc/r2dbc-h2/tree/main/src/jmh/java/io/r2dbc/h2
 *
 * @author Thomas Freese
 */
public class BenchmarkRunner
{
    /**
     * @param args String[]
     *
     * @throws Exception Falls was schief geht
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
//                .forks(0) // Zum Debuggen
//                .warmupForks(0)
//                .warmupIterations(0)
//                .warmupTime(TimeValue.milliseconds(500))
//                .measurementIterations(2)
//                .measurementTime(TimeValue.milliseconds(1000))
                .build()
                ;
        // @formatter:on

        new Runner(options).run();
    }
}
