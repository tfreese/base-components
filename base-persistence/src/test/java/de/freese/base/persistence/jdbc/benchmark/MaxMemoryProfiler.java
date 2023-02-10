// Created: 30.04.2020
package de.freese.base.persistence.jdbc.benchmark;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.infra.IterationParams;
import org.openjdk.jmh.profile.InternalProfiler;
import org.openjdk.jmh.results.AggregationPolicy;
import org.openjdk.jmh.results.IterationResult;
import org.openjdk.jmh.results.Result;
import org.openjdk.jmh.results.ScalarResult;

/**
 * @author Thomas Freese
 */
public class MaxMemoryProfiler implements InternalProfiler {
    /**
     * @see InternalProfiler#afterIteration(BenchmarkParams, IterationParams,
     * IterationResult)
     */
    @Override
    public Collection<? extends Result<?>> afterIteration(final BenchmarkParams benchmarkParams, final IterationParams iterationParams, final IterationResult result) {
        long totalHeap = Runtime.getRuntime().totalMemory();

        double megaBytes = totalHeap / 1024D / 1024D;

        List<ScalarResult> results = new ArrayList<>();
        results.add(new ScalarResult("memory.heap.avg", megaBytes, "MB", AggregationPolicy.AVG));
        results.add(new ScalarResult("memory.heap.max", megaBytes, "MB", AggregationPolicy.MAX));

        return results;
    }

    /**
     * @see InternalProfiler#beforeIteration(BenchmarkParams, IterationParams)
     */
    @Override
    public void beforeIteration(final BenchmarkParams benchmarkParams, final IterationParams iterationParams) {
        // Empty
    }

    /**
     * @see org.openjdk.jmh.profile.Profiler#getDescription()
     */
    @Override
    public String getDescription() {
        return "Max memory heap profiler";
    }
}
