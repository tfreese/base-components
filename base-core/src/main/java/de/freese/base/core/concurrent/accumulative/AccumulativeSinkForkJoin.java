// Created: 30.07.2021
package de.freese.base.core.concurrent.accumulative;

import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * Replica of a {@link sun.swing.AccumulativeRunnable} by a {@link Flux}.<br>
 * The {@link Consumer} is executed in {@link ForkJoinPool#commonPool()}.
 *
 * @author Thomas Freese
 */
public class AccumulativeSinkForkJoin extends AbstractAccumulativeSink {
    private static final Scheduler FORK_JOIN_SCHEDULER = Schedulers.fromExecutor(ForkJoinPool.commonPool());

    @Override
    protected Scheduler getScheduler() {
        return FORK_JOIN_SCHEDULER;
    }
}
