/**
 * Created: 12.05.2012
 */
package de.freese.base.demo.fibonacci.bp;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongConsumer;

/**
 * {@link RecursiveTask} für Fibonacci Algorithmus.
 *
 * @author Thomas Freese
 */
public class FibonacciForkJoinTask extends RecursiveTask<Long>
{
    /**
     *
     */
    private static final Map<Integer, Long> FIBONACCI_CACHE = new HashMap<>(100);

    /**
     *
     */
    private static final long serialVersionUID = 67781993370162624L;

    /**
     * Schwellenwert, bei dem die Berechnung sequenziell durchgeführt wird.
     */
    private static final int THRESHOLD = 10;

    /**
     *
     */
    public final int n;

    /**
     *
     */
    private boolean enableCache = false;

    /**
     *
     */
    private final AtomicLong operation;

    /**
     *
     */
    private final LongConsumer operationConsumer;

    /**
     * Erstellt ein neues {@link FibonacciForkJoinTask} Object.
     *
     * @param n int
     * @param operationConsumer {@link LongConsumer}
     * @param operation {@link AtomicLong}
     */
    public FibonacciForkJoinTask(final int n, final LongConsumer operationConsumer, final AtomicLong operation)
    {
        super();

        this.n = n;
        this.operationConsumer = operationConsumer;
        this.operation = operation;
    }

    /**
     * Algorithmus.
     *
     * @param n int
     * @return long
     */
    private long fibonacci(final int n)
    {
        if (n <= 1)
        {
            return n;
        }

        return fibonacci(n - 1) + fibonacci(n - 2);
    }

    /**
     * @see java.util.concurrent.RecursiveTask#compute()
     */
    @Override
    protected Long compute()
    {
        long result = 0;

        if (this.enableCache)
        {
            result = FIBONACCI_CACHE.get(this.n);

            if (result > 0)
            {
                return Long.valueOf(result);
            }
        }

        if (this.n < THRESHOLD)
        {
            result = fibonacci(this.n);

            this.operation.addAndGet(THRESHOLD / 2);
            this.operationConsumer.accept(this.operation.get());
        }
        else
        {
            FibonacciForkJoinTask task1 = new FibonacciForkJoinTask(this.n - 1, this.operationConsumer, this.operation);
            FibonacciForkJoinTask task2 = new FibonacciForkJoinTask(this.n - 2, this.operationConsumer, this.operation);
            task2.fork();

            result = task1.compute().longValue() + task2.join().longValue();
        }

        FIBONACCI_CACHE.put(this.n, result);

        return Long.valueOf(result);
    }
}
