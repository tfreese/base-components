package de.freese.base.swing.task;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.event.SwingPropertyChangeSupport;
import de.freese.base.core.concurrent.NamePreservingRunnable;

/**
 * Diese Klasse verwaltet die einzelnen {@link AbstractTask}s und dient als Model für GUI-Komponenten.
 *
 * @author Thomas Freese
 */
public final class TaskService
{
    /**
     * PropertyChangeListener auf einen {@link AbstractTask}.
     *
     * @author Thomas Freese
     */
    private class TaskPCL implements PropertyChangeListener
    {
        /**
         * Erstellt ein neues {@link TaskPCL} Object.
         */
        public TaskPCL()
        {
            super();
        }

        /**
         * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
         */
        @Override
        public void propertyChange(final PropertyChangeEvent event)
        {
            String propertyName = event.getPropertyName();

            if (SwingTask.PROPERTY_DONE.equals(propertyName))
            {
                AbstractTask<?, ?> task = (AbstractTask<?, ?>) event.getSource();

                removeTask(task);
            }
        }
    }

    /**
     * SPIELEREI !!! Statt pool-x steht TaskService-x im ThreadNamen, aber schoen schauts aus :-)
     *
     * @author Thomas Freese
     */
    public static class TaskServiceThreadFactory implements ThreadFactory
    {
        /**
         *
         */
        private static final AtomicInteger queueNumber = new AtomicInteger(1);

        /**
         *
         */
        private final ThreadGroup group;

        /**
         *
         */
        private final String namePrefix;

        /**
         *
         */
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        /**
         * Creates a new {@link TaskServiceThreadFactory} object.
         */
        private TaskServiceThreadFactory()
        {
            SecurityManager sm = System.getSecurityManager();

            this.group = (sm != null) ? sm.getThreadGroup() : Thread.currentThread().getThreadGroup();
            this.namePrefix = "TaskService-" + TaskServiceThreadFactory.queueNumber.getAndIncrement() + "-thread-";
        }

        /**
         * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
         */
        @Override
        public Thread newThread(final Runnable r)
        {
            Thread t = new Thread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement(), 0);

            if (t.isDaemon())
            {
                t.setDaemon(false);
            }

            if (t.getPriority() != Thread.NORM_PRIORITY)
            {
                t.setPriority(Thread.NORM_PRIORITY);
            }

            return t;
        }
    }

    /**
     *
     */
    private final boolean createdExecutor;

    /**
     *
     */
    private ExecutorService executorService = null;

    /**
     *
     */
    private final PropertyChangeSupport propertyChangeSupport;

    /**
     *
     */
    private final PropertyChangeListener taskPCL;

    /**
     *
     */
    private final List<AbstractTask<?, ?>> tasks = new ArrayList<>();

    /**
     * Erstellt ein neues {@link TaskService} Object.
     */
    public TaskService()
    {
        this(null);
    }

    /**
     * Erstellt ein neues {@link TaskService} Object.
     *
     * @param executorService {@link ExecutorService}
     */
    public TaskService(final ExecutorService executorService)
    {
        super();

        if (executorService == null)
        {
            this.executorService = Executors.newCachedThreadPool(new TaskServiceThreadFactory());
            this.createdExecutor = true;
        }
        else
        {
            this.executorService = executorService;
            this.createdExecutor = false;
        }

        this.taskPCL = new TaskPCL();

        this.propertyChangeSupport = new SwingPropertyChangeSupport(this, true);
    }

    /**
     * Hinzufuegen eines PropertyChangeListeners.
     *
     * @param listener {@link PropertyChangeListener}
     */
    public void addPropertyChangeListener(final PropertyChangeListener listener)
    {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Hinzufügen eines Tasks und feuern des Events.
     *
     * @param task {@link AbstractTask}
     */
    private void addTask(final AbstractTask<?, ?> task)
    {
        List<AbstractTask<?, ?>> oldTaskList = null;
        List<AbstractTask<?, ?>> newTaskList = null;

        synchronized (getTasks())
        {
            oldTaskList = copyTasksList();
            getTasks().add(task);
            newTaskList = copyTasksList();

            task.addPropertyChangeListener(this.taskPCL);
        }

        firePropertyChange("tasks", oldTaskList, newTaskList);
    }

    /**
     * Liefert eine Kopie der TaskListe.
     *
     * @return {@link List}
     */
    private List<AbstractTask<?, ?>> copyTasksList()
    {
        synchronized (getTasks())
        {
            if (getTasks().isEmpty())
            {
                return Collections.emptyList();
            }

            return new ArrayList<>(getTasks());
        }
    }

    /**
     * Ausfuehren eines Tasks am Ende der ThreadQueue.
     *
     * @param task {@link AbstractTask}
     */
    public void execute(final AbstractTask<?, ?> task)
    {
        if (task == null)
        {
            throw new NullPointerException("task");
        }

        if (!task.isPending())
        {
            throw new IllegalStateException("task has already been executed");
        }

        addTask(task);

        getExecutorService().execute(new NamePreservingRunnable(task, task.getClass().getSimpleName()));
    }

    /**
     * Feuert ein PropertyChangeEvent.
     *
     * @param propertyName String
     * @param oldValue Object
     * @param newValue Object
     */
    private void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue)
    {
        this.propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * @return {@link ExecutorService}
     */
    public ExecutorService getExecutorService()
    {
        return this.executorService;
    }

    /**
     * Liefert alle Tasks.
     *
     * @return {@link List}
     */
    private List<AbstractTask<?, ?>> getTasks()
    {
        return this.tasks;
    }

    /**
     * @return boolean
     */
    public final boolean isShutdown()
    {
        return getExecutorService().isShutdown();
    }

    /**
     * @return boolean
     */
    public final boolean isTerminated()
    {
        return getExecutorService().isTerminated();
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
     * Hinzufuegen eines Tasks und feuern des Events.
     *
     * @param task {@link AbstractTask}
     */
    private void removeTask(final AbstractTask<?, ?> task)
    {
        List<AbstractTask<?, ?>> oldTaskList = null;
        List<AbstractTask<?, ?>> newTaskList = null;

        synchronized (getTasks())
        {
            oldTaskList = copyTasksList();
            getTasks().remove(task);
            newTaskList = copyTasksList();

            task.removePropertyChangeListener(this.taskPCL);
        }

        firePropertyChange("tasks", oldTaskList, newTaskList);
    }

    /**
     * @param executorService {@link ExecutorService}
     */
    public void setExecutorService(final ExecutorService executorService)
    {
        if (executorService == null)
        {
            throw new NullPointerException("executorService");
        }

        if (this.executorService != null)
        {
            this.executorService.shutdownNow();
            this.executorService = null;
        }

        this.executorService = executorService;
    }

    /**
     * Beenden des {@link Executors}.
     */
    public final void shutdown()
    {
        if (this.createdExecutor)
        {
            getExecutorService().shutdown();

            while (!isTerminated())
            {
                try
                {
                    getExecutorService().awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
                }
                catch (InterruptedException ex)
                {
                    // Ignore
                }
            }
        }
    }

    // /**
    // * Sofortiges Beenden des {@link Executors}.
    // *
    // * @return {@link List} de verbliebenen {@link Runnable}s.
    // */
    // public final List<Runnable> shutdownNow()
    // {
    // return getExecutorService().shutdownNow();
    // }
}
