package de.freese.base.swing.task;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.swing.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TaskListener, der dessen Ausfuehrungsdauer protokolliert und daraus ueber einen {@link Timer} den Progresswert zyklisch setzt.<br>
 * Bei der ersten Ausfuehrung wird nur die Zeit gemessen.
 *
 * @author Thomas Freese
 */
@SuppressWarnings("rawtypes")
public final class DurationStatisikTaskListener implements TaskListener
{
    /**
     *
     */
    private static final Map<String, TaskStatistik> CACHE = new HashMap<>();

    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DurationStatisikTaskListener.class);

    /**
     *
     */
    private Timer timer = null;

    /**
     * Erstellt ein neues {@link DurationStatisikTaskListener} Object.
     */
    public DurationStatisikTaskListener()
    {
        super();
    }

    /**
     * @see de.freese.base.swing.task.TaskListener#cancelled(de.freese.base.swing.task.TaskEvent)
     */
    @Override
    public void cancelled(final TaskEvent event)
    {
        stopTimer();
    }

    /**
     * @see de.freese.base.swing.task.TaskListener#doInBackground(de.freese.base.swing.task.TaskEvent)
     */
    @Override
    public void doInBackground(final TaskEvent event)
    {
        final AbstractTask task = event.getSource();
        String taskName = task.getName();

        if ((taskName == null) || (taskName.length() == 0))
        {
            LOGGER.warn("\"{}\" has no TaskName !", task.getClass().getName());
            taskName = task.getClass().getName();
        }

        TaskStatistik taskStatistik = getTaskStatistik(taskName);
        final long mittelwert = taskStatistik.getAvg();

        if (mittelwert > 0)
        {
            this.timer = new Timer(250, e -> {
                if (mittelwert <= 0)
                {
                    return;
                }

                long execution = task.getCurrentDuration(TimeUnit.MILLISECONDS);
                float prozent = execution / (float) mittelwert;

                if (prozent > 0.99)
                {
                    prozent = 0.99F;
                }

                task.setProgress(prozent);

                if (prozent > 0.99)
                {
                    stopTimer();
                }
            });

            this.timer.start();
        }
        // else
        // {
        // // Trigger Event
        // task.setProgress(1);
        // }
    }

    /**
     * @see de.freese.base.swing.task.TaskListener#failed(de.freese.base.swing.task.TaskEvent)
     */
    @Override
    public void failed(final TaskEvent event)
    {
        stopTimer();
    }

    /**
     * @see de.freese.base.swing.task.TaskListener#finished(de.freese.base.swing.task.TaskEvent)
     */
    @Override
    public void finished(final TaskEvent event)
    {
        stopTimer();
    }

    /**
     * Liefert die Statistiken fuer den Task.
     *
     * @param taskName String
     * @return {@link TaskStatistik}
     */
    private TaskStatistik getTaskStatistik(final String taskName)
    {
        // TODO Hier speicherbar machen.
        TaskStatistik taskStatistik = CACHE.get(taskName);

        if (taskStatistik == null)
        {
            taskStatistik = new TaskStatistik();
            taskStatistik.setTaskName(taskName);
        }

        return taskStatistik;
    }

    /**
     * @see de.freese.base.swing.task.TaskListener#interrupted(de.freese.base.swing.task.TaskEvent)
     */
    @Override
    public void interrupted(final TaskEvent event)
    {
        stopTimer();
    }

    /**
     * @see de.freese.base.swing.task.TaskListener#process(de.freese.base.swing.task.TaskEvent)
     */
    @Override
    public void process(final TaskEvent event)
    {
        // LOGGER.info(event.getValue().toString());
    }

    /**
     * @see de.freese.base.swing.task.TaskListener#progress(de.freese.base.swing.task.TaskEvent)
     */
    @Override
    public void progress(final TaskEvent event)
    {
        // LOGGER.info(event.getValue().toString());
    }

    /**
     * Stoppen des Timers.
     */
    private void stopTimer()
    {
        if (this.timer != null)
        {
            this.timer.stop();
        }
    }

    /**
     * @see de.freese.base.swing.task.TaskListener#succeeded(de.freese.base.swing.task.TaskEvent)
     */
    @Override
    public void succeeded(final TaskEvent event)
    {
        AbstractTask<?, ?> task = event.getSource();
        String taskName = task.getName();

        if ((taskName == null) || (taskName.length() == 0))
        {
            LOGGER.warn("\"{}\" has no TaskName !", task.getClass().getName());
            taskName = task.getClass().getName();
        }

        TaskStatistik taskStatistik = getTaskStatistik(taskName);
        taskStatistik.measureDuration(task.getExecutionDuration(TimeUnit.MILLISECONDS));
        updateTaskStatistik(taskStatistik);
    }

    /**
     * Aktualisieren der TaskStatistik.
     *
     * @param taskStatistik {@link TaskStatistik}
     */
    private void updateTaskStatistik(final TaskStatistik taskStatistik)
    {
        // TODO Hier speicherbar machen.
        CACHE.put(taskStatistik.getTaskName(), taskStatistik);
    }
}
