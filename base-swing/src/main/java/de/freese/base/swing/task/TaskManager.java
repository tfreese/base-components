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
                case SwingTask.PROPERTY_CANCELLED:
                case SwingTask.PROPERTY_FAILED:
                case SwingTask.PROPERTY_SUCCEEDED:
                    removeForegroundTask(task);
                    return;
                default:
                    // NO-OP
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
                case SwingTask.PROPERTY_STARTED:
                case SwingTask.PROPERTY_PROGRESS:
                    setForegroundTask(task);
                    return;
                default:
                    // NO-OP
            }
        }
    }

    /**
    *
    */
    private final ExecutorService executorService;

    /**
    *
    */
    private AbstractSwingTask<?, ?> foregroundTask;

    /**
    *
    */
    private final PropertyChangeListener foregroundTaskPCL;

    /**
    *
    */
    private final PropertyChangeSupport propertyChangeSupport;

    /**
    *
    */
    private final PropertyChangeListener taskPCL;

    /**
     * Erstellt ein neues {@link TaskManager} Object.
     *
     * @param executorService {@link ExecutorService}
     */
    public TaskManager(final ExecutorService executorService)
    {
        super();

        this.executorService = Objects.requireNonNull(executorService, "executorService required");
        this.propertyChangeSupport = new SwingPropertyChangeSupport(this, true);
        this.taskPCL = new TaskPCL();
        this.foregroundTaskPCL = new ForegroundTaskPCL();
    }

    /**
     * Hinzufügen eines PropertyChangeListeners.
     *
     * @param listener {@link PropertyChangeListener}
     */
    public void addPropertyChangeListener(final PropertyChangeListener listener)
    {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Ausführen eines Tasks.
     *
     * @param task {@link AbstractSwingTask}
     */
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

    /**
     * Feuert ein PropertyChangeEvent.
     *
     * @param event {@link PropertyChangeEvent}
     */
    protected void firePropertyChange(final PropertyChangeEvent event)
    {
        this.propertyChangeSupport.firePropertyChange(event);
    }

    /**
     * Feuert ein PropertyChangeEvent.
     *
     * @param propertyName String
     * @param oldValue Object
     * @param newValue Object
     */
    protected void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue)
    {
        this.propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * @return {@link ExecutorService}
     */
    protected ExecutorService getExecutorService()
    {
        return this.executorService;
    }

    /**
     * @return {@link AbstractSwingTask}
     */
    public AbstractSwingTask<?, ?> getForegroundTask()
    {
        return this.foregroundTask;
    }

    /**
     * Entfernen des aktuell aktiven Tasks.
     *
     * @param task {@link AbstractSwingTask}
     */
    protected void removeForegroundTask(final AbstractSwingTask<?, ?> task)
    {
        if (this.foregroundTask != null)
        {
            this.foregroundTask.removePropertyChangeListener(this.foregroundTaskPCL);
        }

        this.foregroundTask = null;
    }

    /**
     * Entfernen eines PropertyChangeListeners.
     *
     * @param listener {@link PropertyChangeListener}
     */
    public void removePropertyChangeListener(final PropertyChangeListener listener)
    {
        this.propertyChangeSupport.removePropertyChangeListener(listener);
    }

    /**
     * Setzen des aktuell aktiven Tasks.
     *
     * @param task {@link AbstractSwingTask}
     */
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
