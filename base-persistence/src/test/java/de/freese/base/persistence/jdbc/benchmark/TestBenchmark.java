package de.freese.base.persistence.jdbc.benchmark;

import org.junit.jupiter.api.Test;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

/**
 * https://github.com/r2dbc/r2dbc-h2/tree/main/src/jmh/java/io/r2dbc/h2
 *
 * @author Thomas Freese
 */
class TestBenchmark
{
    private final Options DEFAULT_OPTIONS = new OptionsBuilder().shouldFailOnError(true)
            .warmupIterations(0)
            .warmupTime(TimeValue.milliseconds(100))
            .measurementIterations(2)
            .measurementTime(TimeValue.milliseconds(200))
            .forks(0)
            .build();

    /**
     * @throws Exception Falls was schief geht
     */
    @Test
    void testStagedResultSizeBenchmarks() throws Exception
    {
        // @formatter:off
        Options options = new OptionsBuilder()
                .include(StagedResultSizeBenchmarks.class.getSimpleName())
                .parent(DEFAULT_OPTIONS)
                .build()
                ;
        // @formatter:on

        new Runner(options).run();
    }

    /**
     * @throws Exception Falls was schief geht
     */
    @Test
    void testStatementBenchmarks() throws Exception
    {
        // @formatter:off
        Options options = new OptionsBuilder()
                .include(StatementBenchmarks.class.getSimpleName())
                .parent(DEFAULT_OPTIONS)
                .build()
                ;
        // @formatter:on

        new Runner(options).run();
    }
}
