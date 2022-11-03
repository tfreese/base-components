// Created: 30.07.2021
package de.freese.base.core.concurrent.accumulative;

import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * Nachbau eines {@link sun.swing.AccumulativeRunnable} durch einen {@link Flux}.<br>
 * Der {@link Consumer} wird in einem Thread des {@link ForkJoinPool#commonPool()} ausgeführt.
 *
 * @author Thomas Freese
 */
public class AccumulativeSinkForkJoin extends AbstractAccumulativeSink
{
    private static final Scheduler FORK_JOIN__SCHEDULER = Schedulers.fromExecutor(ForkJoinPool.commonPool());

    @Override
    protected Scheduler getScheduler()
    {
        return FORK_JOIN__SCHEDULER;
    }
}
