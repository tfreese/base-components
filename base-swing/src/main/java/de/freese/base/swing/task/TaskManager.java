// Created: 21.05.2020
package de.freese.base.swing.task;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

import javax.swing.event.SwingPropertyChangeSupport;

/**
 * Diese Klasse verwaltet die einzelnen {@link AbstractSwingTask}s.
 *
 * @author Thomas Freese
 */
public class TaskManager
{
    /**
     * PropertyChangeListener auf den aktuell aktiven Task.
     *
     * @author Thomas Freese
     */
    private class ForegroundTaskPCL implements PropertyChangeListener
    {
        /**
         * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
         */
        @Override
        public void propertyChange(final PropertyChangeEvent event)
        {
            // Event an die Listener des TaskManagers weiterleiten.
            firePropertyChange(event);

            AbstractSwingTask<?, ?> task = (AbstractSwingTask<?, ?>) event.getSource();

            switch (event.getPropertyName())
            {
                case SwingTask.PROPERTY_CANCELLED, SwingTask.PROPERTY_FAILED, SwingTask.PROPERTY_SUCCEEDED ->
                {
                    removeForegroundTask(task);
                }
                default ->
                {
                    // Empty
                }
            }
        }
    }

    /**
     * PropertyChangeListener auf einen Task.
     *
     * @author Thomas Freese
     */
    private class TaskPCL implements PropertyChangeListener
    {
        /**
         * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
         */
        @Override
        public void propertyChange(final PropertyChangeEvent event)
        {
            AbstractSwingTask<?, ?> task = (AbstractSwingTask<?, ?>) event.getSource();

            switch (event.getPropertyName())
            {
                case SwingTask.PROPERTY_STARTED, SwingTask.PROPERTY_PROGRESS ->
                {
                    setForegroundTask(task);
                }
                default ->
                {
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

    public TaskManager(final ExecutorService executorService)
    {
        super();

        this.executorService = Objects.requireNonNull(executorService, "executorService required");
        this.propertyChangeSupport = new SwingPropertyChangeSupport(this, true);
        this.taskPCL = new TaskPCL();
        this.foregroundTaskPCL = new ForegroundTaskPCL();
    }

    public void addPropertyChangeListener(final PropertyChangeListener listener)
    {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void execute(final AbstractSwingTask<?, ?> task)
    {
        Objects.requireNonNull(task, "task required");

        if (!task.isPending())
        {
            throw new IllegalStateException("task is running or has already been executed");
        }

        task.addPropertyChangeListener(this.taskPCL);

        getExecutorService().execute(task);
    }

    public AbstractSwingTask<?, ?> getForegroundTask()
    {
        return this.foregroundTask;
    }

    public void removePropertyChangeListener(final PropertyChangeListener listener)
    {
        this.propertyChangeSupport.removePropertyChangeListener(listener);
    }

    protected void firePropertyChange(final PropertyChangeEvent event)
    {
        this.propertyChangeSupport.firePropertyChange(event);
    }

    protected void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue)
    {
        this.propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    protected ExecutorService getExecutorService()
    {
        return this.executorService;
    }

    protected void removeForegroundTask(final AbstractSwingTask<?, ?> task)
    {
        if (this.foregroundTask != null)
        {
            this.foregroundTask.removePropertyChangeListener(this.foregroundTaskPCL);
        }

        this.foregroundTask = null;
    }

    protected void setForegroundTask(final AbstractSwingTask<?, ?> task)
    {
        if (this.foregroundTask != null)
        {
            // Wenn es schon einen gibt, dann nicht neu zuweisen.
            return;
        }

        this.foregroundTask = task;
        this.foregroundTask.removePropertyChangeListener(this.taskPCL);
        this.foregroundTask.addPropertyChangeListener(this.foregroundTaskPCL);

        // Events wiederholen für die Listener des TaskManagers.
        firePropertyChange(SwingTask.PROPERTY_STARTED, null, true);
        firePropertyChange(SwingTask.PROPERTY_TITLE, null, this.foregroundTask.getTitle());
        firePropertyChange(SwingTask.PROPERTY_SUBTITLE, null, this.foregroundTask.getSubTitle());
        firePropertyChange(SwingTask.PROPERTY_PROGRESS, null, this.foregroundTask.getProgress());
    }
}
