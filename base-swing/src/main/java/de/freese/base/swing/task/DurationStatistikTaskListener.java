package de.freese.base.swing.task;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.swing.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TaskListener, der dessen Ausführungsdauer protokolliert und daraus über einen {@link Timer} den Progress-Wert zyklisch setzt.<br>
 * Bei der ersten Ausführung wird nur die Zeit gemessen.
 *
 * @author Thomas Freese
 */
public final class DurationStatistikTaskListener implements PropertyChangeListener
{
    private static final Map<String, TaskStatistik> CACHE = new HashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(DurationStatistikTaskListener.class);

    private Timer timer;

    /**
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    @Override
    public void propertyChange(final PropertyChangeEvent event)
    {
        String propertyName = event.getPropertyName();

        if (SwingTask.PROPERTY_CANCELLED.equals(propertyName))
        {
            stopTimer();
        }
        else if (SwingTask.PROPERTY_FAILED.equals(propertyName))
        {
            stopTimer();
        }
        else if (SwingTask.PROPERTY_SUCCEEDED.equals(propertyName))
        {
            AbstractSwingTask<?, ?> task = (AbstractSwingTask<?, ?>) event.getSource();
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
        else if (SwingTask.PROPERTY_STARTED.equals(propertyName))
        {
            AbstractSwingTask<?, ?> task = (AbstractSwingTask<?, ?>) event.getSource();
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
                this.timer = new Timer(250, evt ->
                {
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
        }
    }

    private TaskStatistik getTaskStatistik(final String taskName)
    {
        TaskStatistik taskStatistik = CACHE.get(taskName);

        if (taskStatistik == null)
        {
            taskStatistik = new TaskStatistik();
            taskStatistik.setTaskName(taskName);
        }

        return taskStatistik;
    }

    private void stopTimer()
    {
        if (this.timer != null)
        {
            this.timer.stop();
        }
    }

    private void updateTaskStatistik(final TaskStatistik taskStatistik)
    {
        CACHE.put(taskStatistik.getTaskName(), taskStatistik);
    }
}
