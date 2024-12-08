// Created: 21.05.2020
package de.freese.base.swing.task;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

import javax.swing.event.SwingPropertyChangeSupport;

/**
 * @author Thomas Freese
 */
public class TaskManager {
    /**
     * @author Thomas Freese
     */
    private final class ForegroundTaskPCL implements PropertyChangeListener {
        @Override
        public void propertyChange(final PropertyChangeEvent event) {
            firePropertyChange(event);

            final AbstractSwingTask<?, ?> task = (AbstractSwingTask<?, ?>) event.getSource();

            final String propertyName = event.getPropertyName();

            if (propertyName.equals(SwingTask.PROPERTY_CANCELLED)
                    || propertyName.equals(SwingTask.PROPERTY_FAILED)
                    || propertyName.equals(SwingTask.PROPERTY_SUCCEEDED)) {
                removeForegroundTask(task);
            }
        }
    }

    /**
     * @author Thomas Freese
     */
    private final class TaskPCL implements PropertyChangeListener {
        @Override
        public void propertyChange(final PropertyChangeEvent event) {
            final AbstractSwingTask<?, ?> task = (AbstractSwingTask<?, ?>) event.getSource();

            final String propertyName = event.getPropertyName();

            if (propertyName.equals(SwingTask.PROPERTY_STARTED)
                    || propertyName.equals(SwingTask.PROPERTY_PROGRESS)) {
                setForegroundTask(task);
            }
        }
    }

    private final ExecutorService executorService;
    private final PropertyChangeListener foregroundTaskPCL;
    private final PropertyChangeSupport propertyChangeSupport;
    private final PropertyChangeListener taskPCL;

    private AbstractSwingTask<?, ?> foregroundTask;

    public TaskManager(final ExecutorService executorService) {
        super();

        this.executorService = Objects.requireNonNull(executorService, "executorService required");
        propertyChangeSupport = new SwingPropertyChangeSupport(this, true);
        taskPCL = new TaskPCL();
        foregroundTaskPCL = new ForegroundTaskPCL();
    }

    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void execute(final AbstractSwingTask<?, ?> task) {
        Objects.requireNonNull(task, "task required");

        if (!task.isPending()) {
            throw new IllegalStateException("task is running or has already been executed");
        }

        task.addPropertyChangeListener(taskPCL);

        getExecutorService().execute(task);
    }

    public AbstractSwingTask<?, ?> getForegroundTask() {
        return foregroundTask;
    }

    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    protected void firePropertyChange(final PropertyChangeEvent event) {
        propertyChangeSupport.firePropertyChange(event);
    }

    protected void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    protected ExecutorService getExecutorService() {
        return executorService;
    }

    protected void removeForegroundTask(final AbstractSwingTask<?, ?> task) {
        if (foregroundTask != null) {
            foregroundTask.removePropertyChangeListener(foregroundTaskPCL);
        }

        foregroundTask = null;
    }

    protected void setForegroundTask(final AbstractSwingTask<?, ?> task) {
        if (foregroundTask != null) {
            return;
        }

        foregroundTask = task;
        foregroundTask.removePropertyChangeListener(taskPCL);
        foregroundTask.addPropertyChangeListener(foregroundTaskPCL);

        firePropertyChange(SwingTask.PROPERTY_STARTED, null, true);
        firePropertyChange(SwingTask.PROPERTY_TITLE, null, foregroundTask.getTitle());
        firePropertyChange(SwingTask.PROPERTY_SUBTITLE, null, foregroundTask.getSubTitle());
        firePropertyChange(SwingTask.PROPERTY_PROGRESS, null, foregroundTask.getProgress());
    }
}
