package de.freese.base.core.concurrent.accumulative;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Time-Controlled {@link AccumulativeRunnable}.
 *
 * @author Thomas Freese
 */
public class AccumulativeRunnableScheduled<T> extends AccumulativeRunnable<T> {
    private final Duration delay;

    private final ScheduledExecutorService scheduledExecutor;

    private Consumer<List<T>> submitConsumer = chunks -> {
    };

    /**
     * Without {@link ScheduledExecutorService} a {@link Timer} is used.<br>
     * Default delay = 250 ms
     */
    public AccumulativeRunnableScheduled() {
        this(null, Duration.ofMillis(250));
    }

    /**
     * Default delay = 250 ms
     */
    public AccumulativeRunnableScheduled(final ScheduledExecutorService scheduledExecutor) {
        this(Objects.requireNonNull(scheduledExecutor, "scheduledExecutor required"), Duration.ofMillis(250));
    }

    /**
     * @param scheduledExecutor {@link ScheduledExecutorService}; optional
     */
    public AccumulativeRunnableScheduled(final ScheduledExecutorService scheduledExecutor, final Duration delay) {
        super();

        this.scheduledExecutor = scheduledExecutor;
        this.delay = Objects.requireNonNull(delay, "delay required");
    }

    public void doOnSubmit(final Consumer<List<T>> submitConsumer) {
        this.submitConsumer = Objects.requireNonNull(submitConsumer, "submitConsumer required");
    }

    @Override
    protected void run(final List<T> args) {
        this.submitConsumer.accept(args);
    }

    @Override
    protected final void submit() {
        if (this.scheduledExecutor != null) {
            this.scheduledExecutor.schedule(() -> SwingUtilities.invokeLater(this), this.delay.toMillis(), TimeUnit.MILLISECONDS);
        }
        else {
            Timer timer = new Timer((int) this.delay.toMillis(), event -> SwingUtilities.invokeLater(this));
            timer.setRepeats(false);
            timer.start();
        }
    }
}
