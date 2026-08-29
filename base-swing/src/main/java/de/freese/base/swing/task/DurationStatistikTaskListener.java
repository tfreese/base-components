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
public final class DurationStatistikTaskListener implements PropertyChangeListener {
    private static final Map<String, TaskStatistic> CACHE = new HashMap<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(DurationStatistikTaskListener.class);

    private Timer timer;

    @Override
    public void propertyChange(final PropertyChangeEvent event) {
        final String propertyName = event.getPropertyName();

        if (SwingTask.PROPERTY_CANCELLED.equals(propertyName)) {
            stopTimer();
        }
        else if (SwingTask.PROPERTY_FAILED.equals(propertyName)) {
            stopTimer();
        }
        else if (SwingTask.PROPERTY_SUCCEEDED.equals(propertyName)) {
            final AbstractSwingTask<?, ?> task = (AbstractSwingTask<?, ?>) event.getSource();
            String taskName = task.getName();

            if (taskName == null || taskName.isEmpty()) {
                LOGGER.warn("\"{}\" has no TaskName !", task.getClass().getName());
                taskName = task.getClass().getName();
            }

            final TaskStatistic taskStatistic = getTaskStatistik(taskName);
            taskStatistic.measureDuration(task.getExecutionDuration(TimeUnit.MILLISECONDS));
            updateTaskStatistik(taskStatistic);
        }
        else if (SwingTask.PROPERTY_STARTED.equals(propertyName)) {
            final AbstractSwingTask<?, ?> task = (AbstractSwingTask<?, ?>) event.getSource();
            String taskName = task.getName();

            if (taskName == null || taskName.isEmpty()) {
                LOGGER.warn("\"{}\" has no TaskName !", task.getClass().getName());
                taskName = task.getClass().getName();
            }

            final TaskStatistic taskStatistic = getTaskStatistik(taskName);
            final long mittelwert = taskStatistic.getAvg();

            if (mittelwert > 0L) {
                timer = new Timer(250, evt -> {
                    if (mittelwert <= 0L) {
                        return;
                    }

                    final long execution = task.getCurrentDuration(TimeUnit.MILLISECONDS);
                    float prozent = execution / (float) mittelwert;

                    if (prozent > 0.99F) {
                        prozent = 0.99F;
                    }

                    task.setProgress(prozent);

                    if (prozent > 0.99F) {
                        stopTimer();
                    }
                });

                timer.start();
            }
        }
    }

    private TaskStatistic getTaskStatistik(final String taskName) {
        TaskStatistic taskStatistic = CACHE.get(taskName);

        if (taskStatistic == null) {
            taskStatistic = new TaskStatistic();
            taskStatistic.setTaskName(taskName);
        }

        return taskStatistic;
    }

    private void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
    }

    private void updateTaskStatistik(final TaskStatistic taskStatistic) {
        CACHE.put(taskStatistic.getTaskName(), taskStatistic);
    }
}
