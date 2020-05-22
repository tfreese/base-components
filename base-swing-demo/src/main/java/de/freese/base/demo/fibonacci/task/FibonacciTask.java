package de.freese.base.demo.fibonacci.task;

import java.util.Objects;
import de.freese.base.demo.fibonacci.bp.FibonacciBP;
import de.freese.base.resourcemap.ResourceMap;
import de.freese.base.swing.task.AbstractSwingTask;

/**
 * Task für die Fibonacci Demo.
 *
 * @author Thomas Freese
 */
public class FibonacciTask extends AbstractSwingTask<Long, Void>
{
    /**
     *
     */
    private final FibonacciBP fibonacciBP;

    /**
     *
     */
    private final ResourceMap resourceMap;

    /**
     *
     */
    private final int value;

    /**
     * Erstellt ein neues {@link FibonacciTask} Object.
     *
     * @param value int
     * @param fibonacciBP {@link FibonacciBP}
     * @param resourceMap {@link ResourceMap}
     */
    public FibonacciTask(final int value, final FibonacciBP fibonacciBP, final ResourceMap resourceMap)
    {
        super();

        this.value = value;
        this.fibonacciBP = Objects.requireNonNull(fibonacciBP, "fibonacciBP required");
        this.resourceMap = Objects.requireNonNull(resourceMap, "resourceMap required");

        setTitle(this.resourceMap.getString("fibonacci.title"));
    }

    /**
     * @see javax.swing.SwingWorker#doInBackground()
     */
    @Override
    protected Long doInBackground() throws Exception
    {
        if (this.value > 50)
        {
            throw new IllegalArgumentException("Wert > 50");
        }

        getLogger().info("Started");

        setSubTitle(this.resourceMap.getString("fibonacci.start"));

        final long operations = this.fibonacciBP.getOperationCount(this.value);
        // System.out.println(this.operations);

        long result = this.fibonacciBP.fibonacci(this.value, value -> {
            setProgress(value, 0, operations);

            // Etwas auf die Bremse treten, damit die Demo etwas dauert.
            // try
            // {
            // Thread.sleep(300);
            // }
            // catch (Exception ex)
            // {
            // getLogger().error(null, ex);
            // }
        });

        setSubTitle(this.resourceMap.getString("fibonacci.finished"));

        getLogger().info("Finished");

        return Long.valueOf(result);
    }
}
