package de.freese.base.demo.fibonacci.bp;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
public class FibonacciBP extends AbstractBusinessProcess implements IFibonacciBP
{
    /**
     *
     */
    private static final Map<Integer, Long> OPERATION_CACHE = new HashMap<>(100);

    /**
     *
     */
    private ForkJoinPool forkJoinPool = null;

    /**
     * Erstellt ein neues {@link FibonacciBP} Object.
     */
    public FibonacciBP()
    {
        super();
    }

    /**
     * @see de.freese.base.demo.fibonacci.bp.IFibonacciBP#fibonacci(int, java.util.function.LongConsumer)
     */
    @Override
    public long fibonacci(final int n, final LongConsumer operationConsumer)
    {
        return fibonacci(n, operationConsumer, new AtomicLong(0));
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
     * @see de.freese.base.demo.fibonacci.bp.IFibonacciBP#operations(int)
     */
    @Override
    public long operations(final int n)
    {
        if (n <= 2)
        {
            return 0;
        }

        long result = Optional.ofNullable(OPERATION_CACHE.get(n)).orElse(0L);

        if (result == 0)
        {
            // Anzahl der Aufrufe.
            result = 1 + operations(n - 1) + operations(n - 2);

            OPERATION_CACHE.put(n, result);
        }

        return result;
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

    /**
     * Liefert den Fibonacci-Wert des Parameters.
     *
     * @param n int
     * @param operationConsumer {@link LongConsumer}
     * @param operation {@link AtomicLong}
     * @return long
     */
    private long fibonacci(final int n, final LongConsumer operationConsumer, final AtomicLong operation)
    {
        if (n <= 1)
        {
            return n;
        }

        FibonacciForkJoinTask task = new FibonacciForkJoinTask(n, operationConsumer, operation);
        long result = this.forkJoinPool.invoke(task).longValue();

        return result;
    }
}
