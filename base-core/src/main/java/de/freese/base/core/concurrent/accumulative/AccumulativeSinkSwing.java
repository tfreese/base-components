// Created: 30.07.2021
package de.freese.base.core.concurrent.accumulative;

import java.util.function.Consumer;

import javax.swing.SwingUtilities;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * Replica of a {@link sun.swing.AccumulativeRunnable} by a {@link Flux}.<br>
 * The {@link Consumer} is executed in the EDT-Thread by {@link SwingUtilities#invokeLater(Runnable)}.
 *
 * @author Thomas Freese
 */
public class AccumulativeSinkSwing extends AbstractAccumulativeSink {
    private static final Scheduler EDT_SCHEDULER = Schedulers.fromExecutor(SwingUtilities::invokeLater);

    @Override
    protected Scheduler getScheduler() {
        return EDT_SCHEDULER;
    }
}
