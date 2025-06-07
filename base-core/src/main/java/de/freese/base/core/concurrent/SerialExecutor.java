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

    @Override
    public synchronized void execute(final Runnable runnable) {
        queue.add(() -> {
            try {
                runnable.run();
            }
            finally {
                scheduleNext();
            }
        });

        if (active == null) {
            scheduleNext();
        }
    }

    private synchronized void scheduleNext() {
        active = queue.poll();

        if (active != null) {
            delegate.execute(active);
        }
    }
}
