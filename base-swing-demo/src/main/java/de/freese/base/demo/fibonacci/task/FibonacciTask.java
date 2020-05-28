package de.freese.base.demo.fibonacci.task;

import java.util.Objects;
import de.freese.base.demo.fibonacci.controller.FibonacciController;
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
    private final FibonacciController controller;

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
     * @param controller {@link FibonacciController}
     * @param resourceMap {@link ResourceMap}
     */
    public FibonacciTask(final int value, final FibonacciController controller, final ResourceMap resourceMap)
    {
        super();

        this.value = value;
        this.controller = Objects.requireNonNull(controller, "controller required");
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

        final long operations = this.controller.getOperationCount(this.value);
        // System.out.println(this.operations);

        long result = this.controller.fibonacci(this.value, value -> {
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
