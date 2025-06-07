package de.freese.base.demo.fibonacci;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;

import de.freese.base.demo.fibonacci.task.FibonacciForkJoinTask;
import de.freese.base.demo.fibonacci.view.FibonacciView;
import de.freese.base.mvc.controller.AbstractController;

/**
 * @author Thomas Freese
 */
public class FibonacciController extends AbstractController {
    public static final Map<Integer, Long> FIBONACCI_CACHE = new ConcurrentHashMap<>(100);

    private final ForkJoinPool forkJoinPool;

    public FibonacciController(final FibonacciView view) {
        super(view);

        // forkJoinPool = new ForkJoinPool();
        forkJoinPool = ForkJoinPool.commonPool();
    }

    public long fibonacci(final int n) {
        if (n <= 1) {
            return n;
        }

        final Long value = FIBONACCI_CACHE.get(n);

        if (value != null && value > 0L) {
            return value;
        }

        final FibonacciForkJoinTask task = new FibonacciForkJoinTask(n, false);

        return forkJoinPool.invoke(task);
    }

    @Override
    public FibonacciView getView() {
        return (FibonacciView) super.getView();
    }

    public void shutdown() {
        forkJoinPool.shutdown();
    }
}
