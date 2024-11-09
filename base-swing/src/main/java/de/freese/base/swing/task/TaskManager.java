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

            switch (event.getPropertyName()) {
                case SwingTask.PROPERTY_CANCELLED, SwingTask.PROPERTY_FAILED, SwingTask.PROPERTY_SUCCEEDED -> removeForegroundTask(task);
                default -> {
                    // Empty
                }
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

            switch (event.getPropertyName()) {
                case SwingTask.PROPERTY_STARTED, SwingTask.PROPERTY_PROGRESS -> setForegroundTask(task);
                default -> {
                    // Empty
                }
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
        this.propertyChangeSupport = new SwingPropertyChangeSupport(this, true);
        this.taskPCL = new TaskPCL();
        this.foregroundTaskPCL = new ForegroundTaskPCL();
    }

    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void execute(final AbstractSwingTask<?, ?> task) {
        Objects.requireNonNull(task, "task required");

        if (!task.isPending()) {
            throw new IllegalStateException("task is running or has already been executed");
        }

        task.addPropertyChangeListener(this.taskPCL);

        getExecutorService().execute(task);
    }

    public AbstractSwingTask<?, ?> getForegroundTask() {
        return this.foregroundTask;
    }

    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        this.propertyChangeSupport.removePropertyChangeListener(listener);
    }

    protected void firePropertyChange(final PropertyChangeEvent event) {
        this.propertyChangeSupport.firePropertyChange(event);
    }

    protected void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue) {
        this.propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    protected ExecutorService getExecutorService() {
        return this.executorService;
    }

    protected void removeForegroundTask(final AbstractSwingTask<?, ?> task) {
        if (this.foregroundTask != null) {
            this.foregroundTask.removePropertyChangeListener(this.foregroundTaskPCL);
        }

        this.foregroundTask = null;
    }

    protected void setForegroundTask(final AbstractSwingTask<?, ?> task) {
        if (this.foregroundTask != null) {
            return;
        }

        this.foregroundTask = task;
        this.foregroundTask.removePropertyChangeListener(this.taskPCL);
        this.foregroundTask.addPropertyChangeListener(this.foregroundTaskPCL);

        firePropertyChange(SwingTask.PROPERTY_STARTED, null, true);
        firePropertyChange(SwingTask.PROPERTY_TITLE, null, this.foregroundTask.getTitle());
        firePropertyChange(SwingTask.PROPERTY_SUBTITLE, null, this.foregroundTask.getSubTitle());
        firePropertyChange(SwingTask.PROPERTY_PROGRESS, null, this.foregroundTask.getProgress());
    }
}
