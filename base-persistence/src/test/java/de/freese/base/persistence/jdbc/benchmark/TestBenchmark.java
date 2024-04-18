package de.freese.base.persistence.jdbc.benchmark;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

/**
 * <a href="https://github.com/r2dbc/r2dbc-h2/tree/main/src/jmh/java/io/r2dbc/h2">r2dbc-h2</a>
 *
 * @author Thomas Freese
 */
class TestBenchmark {
    private static final Options DEFAULT_OPTIONS = new OptionsBuilder()
            .shouldFailOnError(true)
            .warmupIterations(0)
            .warmupTime(TimeValue.milliseconds(100))
            .measurementIterations(1)
            .measurementTime(TimeValue.milliseconds(200))
            .forks(0)
            .build();

    @Test
    void testStagedResultSizeBenchmarks() throws Exception {
        final Options options = new OptionsBuilder()
                .parent(DEFAULT_OPTIONS)
                .include(StagedResultSizeBenchmarks.class.getSimpleName())
                .build();

        new Runner(options).run();

        assertTrue(true);
    }

    @Test
    void testStatementBenchmarks() throws Exception {
        final Options options = new OptionsBuilder()
                .parent(DEFAULT_OPTIONS)
                .include(StatementBenchmarks.class.getSimpleName())
                .build();

        new Runner(options).run();

        assertTrue(true);
    }
}
