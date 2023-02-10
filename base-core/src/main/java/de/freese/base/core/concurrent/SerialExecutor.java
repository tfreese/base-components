// Created: 31.12.2021
package de.freese.base.core.concurrent;

import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.Executor;

/**
 * @author Thomas Freese
 */
public class SerialExecutor implements Executor {
    private final Executor delegate;

    private final Queue<Runnable> queue = new ArrayDeque<>();

    private Runnable active;

    public SerialExecutor(final Executor delegate) {
        super();

        this.delegate = Objects.requireNonNull(delegate, "delegate required");
    }

    /**
     * @see java.util.concurrent.Executor#execute(java.lang.Runnable)
     */
    @Override
    public synchronized void execute(final Runnable runnable) {
        this.queue.add(() -> {
            try {
                runnable.run();
            }
            finally {
                scheduleNext();
            }
        });

        if (this.active == null) {
            scheduleNext();
        }
    }

    private synchronized void scheduleNext() {
        if ((this.active = this.queue.poll()) != null) {
            this.delegate.execute(this.active);
        }
    }
}
