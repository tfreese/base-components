package de.freese.base.demo.example;

import de.freese.base.swing.task.AbstractSwingTask;
import de.freese.base.swing.task.DurationStatistikTaskListener;

/**
 * Task, der über den {@link DurationStatistikTaskListener} seine Ausführungsdauer protokolliert und durch diesen seinen Progress-Wert zyklisch setzt.
 *
 * @author Thomas Freese
 */
public class DurationStatistikTask extends AbstractSwingTask<Void, Void>
{
    /**
     * Erstellt ein neues {@link DurationStatistikTask} Object.
     */
    public DurationStatistikTask()
    {
        super();

        setTitle("TaskStatistik");
    }

    /**
     * @see javax.swing.SwingWorker#doInBackground()
     */
    @Override
    protected Void doInBackground() throws Exception
    {
        getLogger().info("Started");

        for (int i = 0; i < 50; i++)
        {
            Thread.sleep(100);
        }

        getLogger().info("Finished");

        return null;
    }
}