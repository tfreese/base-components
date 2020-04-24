package de.freese.base.demo.fibonacci.bp;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongConsumer;
import de.freese.base.mvc.process.AbstractBusinessProcess;
import de.freese.base.mvc.process.BusinessProcess;

/**
 * Konkreter {@link BusinessProcess} des Fibonacci Beispiels.
 *
 * @author Thomas Freese
 */
public class DefaultFibonacciBP extends AbstractBusinessProcess implements FibonacciBP
{
    /**
     *
     */
    public static final Map<Integer, Long> FIBONACCI_CACHE = new ConcurrentHashMap<>(100);

    /**
     *
     */
    private static final Map<Integer, Long> OPERATION_CACHE = new HashMap<>(100);

    /**
     *
     */
    private ForkJoinPool forkJoinPool = null;

    /**
     * Erstellt ein neues {@link DefaultFibonacciBP} Object.
     */
    public DefaultFibonacciBP()
    {
        super();
    }

    /**
     * @see de.freese.base.demo.fibonacci.bp.FibonacciBP#fibonacci(int, java.util.function.LongConsumer)
     */
    @Override
    public long fibonacci(final int n, final LongConsumer operationConsumer)
    {
        Long value = FIBONACCI_CACHE.get(n);

        if ((value != null) && (value > 0))
        {
            return Long.valueOf(value);
        }

        return fibonacci(n, operationConsumer, new AtomicLong(0));
    }

    /**
     * Liefert den Fibonacci-Wert des Parameters.
     *
     * @param n int
     * @param operationConsumer {@link LongConsumer}
     * @param operationCount {@link AtomicLong}
     * @return long
     */
    private long fibonacci(final int n, final LongConsumer operationConsumer, final AtomicLong operationCount)
    {
        if (n <= 1)
        {
            return n;
        }

        FibonacciForkJoinTask task = new FibonacciForkJoinTask(n, operationConsumer, operationCount, false);
        long result = this.forkJoinPool.invoke(task).longValue();

        return result;
    }

    /**
     * @see de.freese.base.demo.fibonacci.bp.FibonacciBP#getOperationCount(int)
     */
    @Override
    public long getOperationCount(final int n)
    {
        if (n <= 2)
        {
            return 0;
        }

        long result = Optional.ofNullable(OPERATION_CACHE.get(n)).orElse(0L);

        if (result == 0)
        {
            // Anzahl der Aufrufe.
            result = 1 + getOperationCount(n - 1) + getOperationCount(n - 2);

            OPERATION_CACHE.put(n, result);
        }

        return result;
    }

    /**
     * @see de.freese.base.mvc.process.AbstractBusinessProcess#initialize()
     */
    @Override
    public void initialize()
    {
        super.initialize();

        this.forkJoinPool = new ForkJoinPool();
    }

    /**
     * @see de.freese.base.mvc.process.AbstractBusinessProcess#release()
     */
    @Override
    public void release()
    {
        super.release();

        this.forkJoinPool.shutdown();
    }
}
