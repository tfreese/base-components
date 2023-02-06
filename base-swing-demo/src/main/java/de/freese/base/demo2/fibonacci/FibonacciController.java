package de.freese.base.demo2.fibonacci;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongConsumer;

import de.freese.base.demo2.fibonacci.task.FibonacciForkJoinTask;
import de.freese.base.demo2.fibonacci.view.FibonacciView;
import de.freese.base.mvc2.controller.AbstractController;

/**
 * @author Thomas Freese
 */
public class FibonacciController extends AbstractController
{
    public static final Map<Integer, Long> FIBONACCI_CACHE = new ConcurrentHashMap<>(100);

    private static final Map<Integer, Long> OPERATION_CACHE = new HashMap<>(100);

    private final ForkJoinPool forkJoinPool;

    public FibonacciController(FibonacciView view)
    {
        super(view);

        // this.forkJoinPool = new ForkJoinPool();
        this.forkJoinPool = ForkJoinPool.commonPool();
    }

    public long fibonacci(final int n, final LongConsumer operationConsumer)
    {
        Long value = FIBONACCI_CACHE.get(n);

        if ((value != null) && (value > 0))
        {
            return value;
        }

        return fibonacci(n, operationConsumer, new AtomicLong(0));
    }

    /**
     * Liefert die Anzahl der benötigten mathematischen Operationen zurück.<br>
     * ACHTUNG: Dieser Wert ist bedeutend grösser als das Ergebnis !
     */
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

    @Override
    public FibonacciView getView()
    {
        return (FibonacciView) super.getView();
    }

    public void shutdown()
    {
        this.forkJoinPool.shutdown();
    }

    private long fibonacci(final int n, final LongConsumer operationConsumer, final AtomicLong operationCount)
    {
        if (n <= 1)
        {
            return n;
        }

        FibonacciForkJoinTask task = new FibonacciForkJoinTask(n, operationConsumer, operationCount, false);

        return this.forkJoinPool.invoke(task);
    }
}
