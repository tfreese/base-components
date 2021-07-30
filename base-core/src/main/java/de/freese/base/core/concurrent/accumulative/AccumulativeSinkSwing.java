// Created: 30.07.2021
package de.freese.base.core.concurrent.accumulative;

import java.util.function.Consumer;

import javax.swing.SwingUtilities;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * Nachbau eines {@link sun.swing.AccumulativeRunnable} durch einen {@link Flux}.<br>
 * Der {@link Consumer} wird im EDT-Thread ausgeführt {@link SwingUtilities#invokeLater(Runnable)}.
 *
 * @author Thomas Freese
 */
@SuppressWarnings("javadoc")
public class AccumulativeSinkSwing extends AbstractAccumulativeSink
{
    /**
     *
     */
    private static final Scheduler EDT_SCHEDULER = Schedulers.fromExecutor(SwingUtilities::invokeLater);

    /**
     * @see de.freese.jsync.swing.components.accumulative.AbstractAccumulativeSink#getScheduler()
     */
    @Override
    protected Scheduler getScheduler()
    {
        return EDT_SCHEDULER;
    }
}
