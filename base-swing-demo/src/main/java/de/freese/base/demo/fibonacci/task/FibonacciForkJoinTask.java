// Created: 12.05.2012
package de.freese.base.demo.fibonacci.task;

import java.io.Serial;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongConsumer;

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

    private final transient LongConsumer operationConsumer;

    private final AtomicLong operationCount;

    public FibonacciForkJoinTask(final int n, final LongConsumer operationConsumer, final AtomicLong operationCount, final boolean enableCache) {
        super();

        this.n = n;
        this.operationConsumer = operationConsumer;
        this.operationCount = operationCount;
        this.enableCache = enableCache;
    }

    /**
     * @see RecursiveTask#compute()
     */
    @Override
    protected Long compute() {
        Long value = FibonacciController.FIBONACCI_CACHE.get(this.n);

        if ((value != null) && (value > 0)) {
            return value;
        }

        long result = 0L;

        if (this.n < THRESHOLD) {
            result = fibonacci(this.n);

            this.operationCount.addAndGet(THRESHOLD / 2);
            this.operationConsumer.accept(this.operationCount.get());
        }
        else {
            FibonacciForkJoinTask task1 = new FibonacciForkJoinTask(this.n - 1, this.operationConsumer, this.operationCount, this.enableCache);
            FibonacciForkJoinTask task2 = new FibonacciForkJoinTask(this.n - 2, this.operationConsumer, this.operationCount, this.enableCache);
            task2.fork();

            result = task1.compute() + task2.join();
        }

        if (this.enableCache) {
            FibonacciController.FIBONACCI_CACHE.put(this.n, result);
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
