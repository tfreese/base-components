package de.freese.base.demo.fibonacci;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongConsumer;

import de.freese.base.demo.fibonacci.task.FibonacciForkJoinTask;
import de.freese.base.demo.fibonacci.task.FibonacciTask;
import de.freese.base.demo.fibonacci.view.DefaultFibonacciView;
import de.freese.base.demo.fibonacci.view.FibonacciPanel;
import de.freese.base.demo.fibonacci.view.FibonacciTaskListener;
import de.freese.base.demo.fibonacci.view.FibonacciView;
import de.freese.base.mvc.AbstractController;
import de.freese.base.swing.task.inputblocker.DefaultGlassPaneInputBlocker;
import de.freese.base.swing.task.inputblocker.DefaultInputBlocker;

/**
 * @author Thomas Freese
 */
public class FibonacciController extends AbstractController
{
    public static final Map<Integer, Long> FIBONACCI_CACHE = new ConcurrentHashMap<>(100);

    private static final Map<Integer, Long> OPERATION_CACHE = new HashMap<>(100);

    private final FibonacciView view;

    private ForkJoinPool forkJoinPool;

    public FibonacciController()
    {
        super();

        this.view = new DefaultFibonacciView();
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

    /**
     * @see de.freese.base.mvc.Controller#getView()
     */
    @Override
    public FibonacciView getView()
    {
        return this.view;
    }

    /**
     * @see de.freese.base.mvc.AbstractController#initialize()
     */
    @Override
    public void initialize()
    {
        super.initialize();

        // this.forkJoinPool = new ForkJoinPool();
        this.forkJoinPool = ForkJoinPool.commonPool();

        FibonacciPanel panel = getView().getComponent();

        panel.getButtonGlassPaneBlock().addActionListener(event ->
        {
            getView().setResult(0);

            // Task mit GlassPaneInputBlocker
            int value = Integer.parseInt(panel.getTextField().getText());

            FibonacciTask task = new FibonacciTask(value, this, getResourceMap());
            task.setInputBlocker(new DefaultGlassPaneInputBlocker(panel));

            // Könnte auch im Task implementiert werden.
            task.addPropertyChangeListener(new FibonacciTaskListener(getView()));

            getContext().getTaskManager().execute(task);
        });

        panel.getButtonComponentBlock().addActionListener(event ->
        {
            getView().setResult(0);

            // Task mit ComponentInputBlocker
            int value = Integer.parseInt(panel.getTextField().getText());

            FibonacciTask task = new FibonacciTask(value, this, getResourceMap());
            task.setInputBlocker(new DefaultInputBlocker().add((Component) event.getSource()));

            // Könnte auch im Task implementiert werden.
            task.addPropertyChangeListener(new FibonacciTaskListener(getView()));

            getContext().getTaskManager().execute(task);
        });
    }

    /**
     * @see de.freese.base.mvc.AbstractController#release()
     */
    @Override
    public void release()
    {
        super.release();

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
