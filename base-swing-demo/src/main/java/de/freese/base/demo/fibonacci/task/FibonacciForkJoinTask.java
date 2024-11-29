// Created: 12.05.2012
package de.freese.base.demo.fibonacci.task;

import java.io.Serial;
import java.util.concurrent.RecursiveTask;

import de.freese.base.demo.fibonacci.FibonacciController;

/**
 * {@link RecursiveTask} für Fibonacci Algorithmus.
 *
 * @author Thomas Freese
 */
public class FibonacciForkJoinTask extends RecursiveTask<Long> {
    private static final int THRESHOLD = 10;
    @Serial
    private static final long serialVersionUID = 67781993370162624L;

    private final boolean enableCache;
    private final int n;

    public FibonacciForkJoinTask(final int n, final boolean enableCache) {
        super();

        this.n = n;
        this.enableCache = enableCache;
    }

    @Override
    protected Long compute() {
        final Long value = FibonacciController.FIBONACCI_CACHE.get(this.n);

        if (value != null && value > 0L) {
            return value;
        }

        final long result;

        if (this.n < THRESHOLD) {
            result = fibonacci(this.n);
        }
        else {
            final FibonacciForkJoinTask task1 = new FibonacciForkJoinTask(n - 1, enableCache);
            final FibonacciForkJoinTask task2 = new FibonacciForkJoinTask(n - 2, enableCache);
            task2.fork();

            result = task1.compute() + task2.join();
        }

        if (enableCache) {
            FibonacciController.FIBONACCI_CACHE.put(n, result);
        }

        return result;
    }

    private long fibonacci(final int n) {
        if (n <= 1) {
            return n;
        }

        return fibonacci(n - 1) + fibonacci(n - 2);
    }
}
