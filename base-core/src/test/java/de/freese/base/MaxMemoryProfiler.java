/**
 * Created: 30.04.2020
 */

package de.freese.base;

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
public class MaxMemoryProfiler implements InternalProfiler
{
    /**
     * Erstellt ein neues {@link MaxMemoryProfiler} Object.
     */
    public MaxMemoryProfiler()
    {
        super();
    }

    /**
     * @see org.openjdk.jmh.profile.InternalProfiler#afterIteration(org.openjdk.jmh.infra.BenchmarkParams, org.openjdk.jmh.infra.IterationParams,
     *      org.openjdk.jmh.results.IterationResult)
     */
    @Override
    public Collection<? extends Result<?>> afterIteration(final BenchmarkParams benchmarkParams, final IterationParams iterationParams,
                                                          final IterationResult result)
    {
        long totalHeap = Runtime.getRuntime().totalMemory();

        double megaBytes = totalHeap / 1024 / 1024;

        List<ScalarResult> results = new ArrayList<>();
        results.add(new ScalarResult("memory.heap.min", megaBytes, "MB", AggregationPolicy.MIN));
        results.add(new ScalarResult("memory.heap.max", megaBytes, "MB", AggregationPolicy.MAX));

        return results;
    }

    /**
     * @see org.openjdk.jmh.profile.InternalProfiler#beforeIteration(org.openjdk.jmh.infra.BenchmarkParams, org.openjdk.jmh.infra.IterationParams)
     */
    @Override
    public void beforeIteration(final BenchmarkParams benchmarkParams, final IterationParams iterationParams)
    {
        // NO-OP
    }

    /**
     * @see org.openjdk.jmh.profile.Profiler#getDescription()
     */
    @Override
    public String getDescription()
    {
        return "Max memory heap profiler";
    }
}
