package de.freese.base.demo.fibonacci.task;

import de.freese.base.demo.fibonacci.bp.IFibonacciBP;
import de.freese.base.resourcemap.IResourceMap;
import de.freese.base.swing.task.AbstractTask;

/**
 * Task fuer die Fibonacci Demo.
 *
 * @author Thomas Freese
 */
public class FibonacciTask extends AbstractTask<Long, Void>
{
    /**
     *
     */
    private final IFibonacciBP fibonacciBP;

    /**
     *
     */
    private final IResourceMap resourceMap;

    /**
     *
     */
    private int value = 0;

    /**
     * Erstellt ein neues {@link FibonacciTask} Object.
     * 
     * @param fibonacciBP {@link IFibonacciBP}
     * @param resourceMap {@link IResourceMap}
     */
    public FibonacciTask(final IFibonacciBP fibonacciBP, final IResourceMap resourceMap)
    {
        super();

        this.fibonacciBP = fibonacciBP;
        this.resourceMap = resourceMap;
        setTitle(this.resourceMap.getString("fibonacci.title"));
    }

    /**
     * Setzt den Wert fuer die Fibonacci Berechnung.
     * 
     * @param value int
     */
    public void setValue(final int value)
    {
        this.value = value;
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

        final long operations = this.fibonacciBP.operations(this.value);
        // System.out.println(this.operations);

        long result = this.fibonacciBP.fibonacci(this.value, value ->
        {
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
