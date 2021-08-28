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
 * TaskListener, der dessen Ausführungsdauer protokolliert und daraus über einen {@link Timer} den Progresswert zyklisch setzt.<br>
 * Bei der ersten Ausführung wird nur die Zeit gemessen.
 *
 * @author Thomas Freese
 */
public final class DurationStatisikTaskListener implements PropertyChangeListener
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
    private Timer timer;

    /**
     * Liefert die Statistiken für den Task.
     *
     * @param taskName String
     *
     * @return {@link TaskStatistik}
     */
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
                this.timer = new Timer(250, evt -> {
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
     * Aktualisieren der TaskStatistik.
     *
     * @param taskStatistik {@link TaskStatistik}
     */
    private void updateTaskStatistik(final TaskStatistik taskStatistik)
    {
        CACHE.put(taskStatistik.getTaskName(), taskStatistik);
    }
}
