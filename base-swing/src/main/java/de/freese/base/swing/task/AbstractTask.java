package de.freese.base.swing.task;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.base.swing.task.inputblocker.InputBlocker;

/**
 * Erweitert den {@link SwingWorker} um diverse Methoden und bietet die Moeglichkeit detailliertere Listener zu verwenden.<br>
 * Die {@link #done()} Methode ist NICHT nutzbar, diese wurde aufgeteilt in die Methoden:
 * <ul>
 * <li>{@link #succeeded(Object)}</li>
 * <li>{@link #failed(Throwable)}</li>
 * <li>{@link #cancelled()}</li>
 * <li>{@link #interrupted(InterruptedException)}</li>
 * </ul>
 * Es gibt weiterhin zusaetzliche PropertyChangeEvents zu den "progress" und "state" des {@link SwingWorker}s:
 * <ul>
 * <li>done</li>
 * <li>completed</li>
 * <li>started</li>
 * <li>title</li>
 * <li>subtitle</li>
 * </ul>
 *
 * @author Thomas Freese
 * @param <T> Typ der {@link #doInBackground()} Methode
 * @param <V> Typ der {@link #publish(Object...)} Methode
 */
public abstract class AbstractTask<T, V> extends SwingWorker<T, V>
{
    /**
     * PropertyChangeListener auf die Properties des {@link SwingWorker}s. Dieser wird durch den Swingworker im EDT gefeuert.<br>
     * Moegliche PropertyChangeEvents:
     * <ul>
     * <li>progress</li>
     * <li>state, @see {@link javax.swing.SwingWorker.StateValue}</li>
     * </ul>
     * 
     * @author Thomas Freese
     */
    private class SwingWorkerPCL implements PropertyChangeListener
    {
        /**
         * Erstellt ein neues {@link SwingWorkerPCL} Object.
         */
        public SwingWorkerPCL()
        {
            super();
        }

        /**
         * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
         */
        @Override
        public void propertyChange(final PropertyChangeEvent evt)
        {
            String propertyName = evt.getPropertyName();

            if ("state".equals(propertyName))
            {
                StateValue state = (StateValue) evt.getNewValue();
                AbstractTask<?, ?> task = (AbstractTask<?, ?>) evt.getSource();

                switch (state)
                {
                    case STARTED:
                        taskStarted(task);
                        break;
                    case DONE:
                        taskDone(task);
                        break;
                    case PENDING:
                        break;
                    default:
                        break;
                }
            }
            else if ("progress".equals(propertyName))
            {
                synchronized (AbstractTask.this)
                {
                    AbstractTask.this.progressPropertyIsValid = true;
                }

                int progress = (Integer) evt.getNewValue();
                fireProgressListeners(progress);
            }
        }

        /**
         * @param task {@link AbstractTask}
         */
        private void taskDone(final AbstractTask<?, ?> task)
        {
            synchronized (AbstractTask.this)
            {
                AbstractTask.this.doneTime = System.currentTimeMillis();
            }

            try
            {
                task.removePropertyChangeListener(this);
                firePropertyChange("done", Boolean.FALSE, Boolean.TRUE);
                fireCompletionListeners();
            }
            finally
            {
                firePropertyChange("completed", Boolean.FALSE, Boolean.TRUE);
            }

            if (getInputBlocker() != null)
            {
                getInputBlocker().unblock();
            }
        }

        /**
         * @param task {@link AbstractTask}
         */
        private void taskStarted(final AbstractTask<?, ?> task)
        {
            synchronized (AbstractTask.this)
            {
                AbstractTask.this.startTime = System.currentTimeMillis();
            }

            firePropertyChange("started", Boolean.FALSE, Boolean.TRUE);
            fireDoInBackgroundListeners();

            if (getInputBlocker() != null)
            {
                getInputBlocker().block();
            }
        }
    }

    /**
     * 
     */
    private long doneTime = -1L;

    /**
     * 
     */
    private InputBlocker inputBlocker = null;

    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Interner Name des Tasks.
     */
    private final String name;

    /**
     * 
     */
    private boolean progressPropertyIsValid = false;

    /**
     * 
     */
    private long startTime = -1L;

    /**
     * Untertitel des Tasks fuer ProgressBars oder aehnliches,.
     */
    private String subTitle = null;

    /**
     * 
     */
    private final List<TaskListener<T, V>> taskListeners = new ArrayList<>();

    /**
     * Titel des Tasks fuer ProgressBars oder aehnliches,.
     */
    private String title = null;

    /**
     * Erstellt ein neues {@link AbstractTask} Object.
     */
    public AbstractTask()
    {
        this(null);
    }

    /**
     * Erstellt ein neues {@link AbstractTask} Object.
     * 
     * @param name {@link String}, Systemweit Einheitliche ID oder aehnliches.
     */
    public AbstractTask(final String name)
    {
        super();

        if (name == null)
        {
            this.name = getClass().getName();
        }
        else
        {
            this.name = name;
        }

        addPropertyChangeListener(new SwingWorkerPCL());
    }

    /**
     * Adds a {@code ITaskListener} to this Task. The listener will be notified when the Task's state changes to {@code STARTED}, each time
     * the {@code process} method is called, and when the Task's state changes to {@code DONE}. All of the listener methods will run on the
     * event dispatching thread.
     * 
     * @param listener the {@code ITaskListener} to be added
     * @see #removeTaskListener
     */
    public void addTaskListener(final TaskListener<T, V> listener)
    {
        if (listener == null)
        {
            throw new IllegalArgumentException("listener is null");
        }

        this.taskListeners.add(listener);
    }

    /**
     * Liefert die Zeiteinheit, wie lange der Task bis jetzt laeuft.
     * 
     * @param unit {@link TimeUnit}
     * @return long
     */
    public long getCurrentDuration(final TimeUnit unit)
    {
        long startTime, currentTime = 0L;

        synchronized (this)
        {
            startTime = this.startTime;
            currentTime = System.currentTimeMillis();
        }

        return getDuration(unit, startTime, currentTime);
    }

    /**
     * Returns the length of time this Task has run. If the task hasn't started yet (i.e. if its state is still {@code StateValue.PENDING}),
     * then this method returns 0. Otherwise it returns the duration in the specified time units. For example, to learn how many seconds a
     * Task has run so far:
     * 
     * <pre>
     * long nSeconds = myTask.getExecutionDuration(TimeUnit.SECONDS);
     * </pre>
     * 
     * @param unit the time unit of the return value
     * @return the length of time this Task has run.
     * @see #execute
     */
    public long getExecutionDuration(final TimeUnit unit)
    {
        long startTime, doneTime = 0L;

        synchronized (this)
        {
            startTime = this.startTime;
            doneTime = this.doneTime;
        }

        return getDuration(unit, startTime, doneTime);
    }

    /**
     * @return {@link InputBlocker}
     */
    public InputBlocker getInputBlocker()
    {
        return this.inputBlocker;
    }

    /**
     * Liefert den Namen des Tasks, Systemweit Einheitliche ID oder aehnliches.
     * 
     * @return String
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Liefert den Untertitel des Tasks, zb. fuer ProgressBars etc.
     * 
     * @return String
     */
    public String getSubTitle()
    {
        return this.subTitle;
    }

    /**
     * Liefert den Titel des Tasks, zb. fuer ProgressBars etc.
     * 
     * @return String
     */
    public String getTitle()
    {
        return this.title;
    }

    /**
     * Equivalent to {@code getState() == StateValue.PENDING}.
     * <p>
     * When a pending Task's state changes to {@code StateValue.STARTED} a PropertyChangeEvent for the "started" property is fired.
     * Similarly when a started Task's state changes to {@code StateValue.DONE}, a "done" PropertyChangeEvent is fired.
     * 
     * @return boolean
     */
    public final boolean isPending()
    {
        return getState() == StateValue.PENDING;
    }

    /**
     * Returns true if the {@link #setProgress progress} property has been set. Some Tasks don't update the progress property because it's
     * difficult or impossible to determine how what percentage of the task has been completed. GUI elements that display Task progress,
     * like an application status bar, can use this property to set the @{link JProgressBar#indeterminate indeterminate} @{code
     * JProgressBar} property.
     * <p>
     * A task that does keep the progress property up to date should initialize it to 0, to ensure that {@code isProgressPropertyValid} is
     * always true.
     * 
     * @return true if the {@link #setProgress progress} property has been set.
     * @see #setProgress
     */
    public synchronized boolean isProgressPropertyValid()
    {
        return this.progressPropertyIsValid;
    }

    /**
     * Equivalent to {@code getState() == StateValue.STARTED}.
     * <p>
     * When a pending Task's state changes to {@code StateValue.STARTED} a PropertyChangeEvent for the "started" property is fired.
     * Similarly when a started Task's state changes to {@code StateValue.DONE}, a "done" PropertyChangeEvent is fired.
     * 
     * @return boolean
     */
    public final boolean isStarted()
    {
        return getState() == StateValue.STARTED;
    }

    /**
     * Removes a {@code ITaskListener} from this Task. If the specified listener doesn't exist, this method does nothing.
     * 
     * @param listener the {@code ITaskListener} to be added
     * @see #addTaskListener
     */
    public void removeTaskListener(final TaskListener<T, V> listener)
    {
        if (listener == null)
        {
            throw new IllegalArgumentException("listener is null");
        }

        this.taskListeners.remove(listener);
    }

    /**
     * @param inputBlocker {@link InputBlocker}
     */
    public void setInputBlocker(final InputBlocker inputBlocker)
    {
        this.inputBlocker = inputBlocker;
    }

    /**
     * This method runs on the EDT because it's called from StatePCL.
     */
    private void fireCancelledListeners()
    {
        TaskEvent<Void> event = new TaskEvent<>(this, null);

        for (TaskListener<T, V> listener : this.taskListeners)
        {
            listener.cancelled(event);
        }
    }

    /**
     * This method runs on the EDT because it's called from StatePCL (see below).
     */
    private void fireCompletionListeners()
    {
        try
        {
            if (isCancelled())
            {
                fireCancelledListeners();
            }
            else
            {
                try
                {
                    fireSucceededListeners(get());
                }
                catch (InterruptedException ex)
                {
                    fireInterruptedListeners(ex);
                }
                catch (ExecutionException ex)
                {
                    fireFailedListeners(ex.getCause());
                }
            }
        }
        finally
        {
            fireFinishedListeners();
        }
    }

    /**
     * This method runs on the EDT because it's called from StatePCL.
     */
    private void fireDoInBackgroundListeners()
    {
        TaskEvent<Void> event = new TaskEvent<>(this, null);

        for (TaskListener<T, V> listener : this.taskListeners)
        {
            listener.doInBackground(event);
        }
    }

    /**
     * This method runs on the EDT because it's called from StatePCL.
     * 
     * @param ex {@link Throwable}
     */
    private void fireFailedListeners(final Throwable ex)
    {
        TaskEvent<Throwable> event = new TaskEvent<>(this, ex);

        for (TaskListener<T, V> listener : this.taskListeners)
        {
            listener.failed(event);
        }
    }

    /**
     * This method runs on the EDT because it's called from StatePCL.
     */
    private void fireFinishedListeners()
    {
        TaskEvent<Void> event = new TaskEvent<>(this, null);

        for (TaskListener<T, V> listener : this.taskListeners)
        {
            listener.finished(event);
        }
    }

    /**
     * This method runs on the EDT because it's called from StatePCL.
     * 
     * @param ex {@link InterruptedException}
     */
    private void fireInterruptedListeners(final InterruptedException ex)
    {
        TaskEvent<InterruptedException> event = new TaskEvent<>(this, ex);

        for (TaskListener<T, V> listener : this.taskListeners)
        {
            listener.interrupted(event);
        }
    }

    /**
     * This method runs on the EDT, it's called from SwingWorker.process().
     * 
     * @param values {@link List}
     */
    private void fireProcessListeners(final List<V> values)
    {
        TaskEvent<List<V>> event = new TaskEvent<>(this, values);

        for (TaskListener<T, V> listener : this.taskListeners)
        {
            listener.process(event);
        }
    }

    /**
     * This method runs on the EDT because it's called from StatePCL.
     * 
     * @param progress int
     */
    private void fireProgressListeners(final int progress)
    {
        TaskEvent<Integer> event = new TaskEvent<>(this, progress);

        for (TaskListener<T, V> listener : this.taskListeners)
        {
            listener.progress(event);
        }
    }

    /**
     * This method runs on the EDT because it's called from StatePCL.
     * 
     * @param result Object
     */
    private void fireSucceededListeners(final T result)
    {
        TaskEvent<T> event = new TaskEvent<>(this, result);

        for (TaskListener<T, V> listener : this.taskListeners)
        {
            listener.succeeded(event);
        }
    }

    /**
     * Liefert die Zeiteinheit der Start und Endzeit.
     * 
     * @param unit {@link TimeUnit}
     * @param startTime long
     * @param endTime long
     * @return long
     */
    private long getDuration(final TimeUnit unit, final long startTime, final long endTime)
    {
        long dt = 0L;

        if (startTime == -1L)
        {
            dt = 0L;
        }
        else if (endTime == -1L)
        {
            dt = System.currentTimeMillis() - startTime;
        }
        else
        {
            dt = endTime - startTime;
        }

        return unit.convert(Math.max(0L, dt), TimeUnit.MILLISECONDS);
    }

    /**
     * Called when this Task has been cancelled by {@link #cancel(boolean)}.
     * <p>
     * This method runs on the EDT. It does nothing by default.
     * 
     * @see #done
     */
    protected void cancelled()
    {
        // NOOP
    }

    /**
     * @see javax.swing.SwingWorker#done()
     */
    @Override
    protected final void done()
    {
        Runnable runnable = () ->
        {
            try
            {
                if (isCancelled())
                {
                    cancelled();
                }
                else
                {
                    try
                    {
                        succeeded(get());
                    }
                    catch (InterruptedException ex)
                    {
                        interrupted(ex);
                    }
                    catch (ExecutionException ex)
                    {
                        failed(ex.getCause());
                    }
                }
            }
            finally
            {
                finished();
            }
        };

        SwingUtilities.invokeLater(runnable);
    }

    /**
     * Called when an execution of this Task fails and an {@code ExecutionExecption} is thrown by {@code get}.
     * <p>
     * This method runs on the EDT. It Logs an error message by default.
     * 
     * @param cause the {@link Throwable#getCause cause} of the {@code ExecutionException}
     * @see #done
     * @see #get
     * @see #failed
     */
    protected void failed(final Throwable cause)
    {
        getLogger().error(null, cause);
    }

    /**
     * Called unconditionally (in a {@code finally} clause) after one of the completion methods, {@code succeeded}, {@code failed},
     * {@code cancelled}, or {@code interrupted}, runs. Subclasses can override this method to cleanup before the {@code done} method
     * returns.
     * <p>
     * This method runs on the EDT. It does nothing by default.
     * 
     * @see #done
     * @see #get
     * @see #failed
     */
    protected void finished()
    {
        // NOOP
    }

    /**
     * @return {@link Logger}
     */
    protected final Logger getLogger()
    {
        return this.logger;
    }

    /**
     * Called if the Task's Thread is interrupted but not explicitly cancelled.
     * <p>
     * This method runs on the EDT. It does nothing by default.
     * 
     * @param ex the {@code InterruptedException} thrown by {@code get}
     * @see #cancel
     * @see #done
     * @see #get
     */
    protected void interrupted(final InterruptedException ex)
    {
        // NOOP
    }

    /**
     * @see javax.swing.SwingWorker#process(java.util.List)
     */
    @Override
    protected void process(final List<V> chunks)
    {
        fireProcessListeners(chunks);
    }

    /**
     * Convenience method that sets the {@code progress} property to <code>percentage * 100</code>.
     * 
     * @param percentage a value in the range 0.0 ... 1.0 inclusive
     * @see #setProgress(int)
     */
    protected final void setProgress(final float percentage)
    {
        if ((percentage < 0.0) || (percentage > 1.0))
        {
            throw new IllegalArgumentException("invalid percentage");
        }

        setProgress(Math.round(percentage * 100.0f));
    }

    /**
     * A convenience method that sets the {@code progress} property to the following ratio normalized to 0 .. 100.
     * 
     * <pre>
     * value - min / max - min
     * </pre>
     * 
     * @param value a value in the range min ... max, inclusive
     * @param min the minimum value of the range
     * @param max the maximum value of the range
     * @see #setProgress(int)
     */
    protected final void setProgress(final float value, final float min, final float max)
    {
        if (min >= max)
        {
            throw new IllegalArgumentException("invalid range: min >= max");
        }

        if ((value < min) || (value > max))
        {
            throw new IllegalArgumentException("invalid value");
        }

        float percentage = (value - min) / (max - min);
        setProgress(Math.round(percentage * 100.0f));
    }

    /**
     * A convenience method that sets the {@code progress} property to the following ratio normalized to 0 .. 100.
     * 
     * <pre>
     * value - min / max - min
     * </pre>
     * 
     * @param value a value in the range min ... max, inclusive
     * @param min the minimum value of the range
     * @param max the maximum value of the range
     * @see #setProgress(int)
     */
    protected final void setProgress(final int value, final int min, final int max)
    {
        if (min >= max)
        {
            throw new IllegalArgumentException("invalid range: min >= max");
        }

        if ((value < min) || (value > max))
        {
            throw new IllegalArgumentException("invalid value");
        }

        float percentage = (float) (value - min) / (float) (max - min);
        setProgress(Math.round(percentage * 100.0f));
    }

    /**
     * A convenience method that sets the {@code progress} property to the following ratio normalized to 0 .. 100.
     * 
     * <pre>
     * value - min / max - min
     * </pre>
     * 
     * @param value a value in the range min ... max, inclusive
     * @param min the minimum value of the range
     * @param max the maximum value of the range
     * @see #setProgress(int)
     */
    protected final void setProgress(final long value, final long min, final long max)
    {
        if (min >= max)
        {
            throw new IllegalArgumentException("invalid range: min >= max");
        }

        if ((value < min) || (value > max))
        {
            throw new IllegalArgumentException("invalid value");
        }

        float percentage = (float) (value - min) / (float) (max - min);
        setProgress(Math.round(percentage * 100.0f));
    }

    /**
     * Setzt den Untertitel des Tasks, zb. fuer ProgressBars etc.
     * 
     * @param subTitle String
     */
    protected void setSubTitle(final String subTitle)
    {
        String old = this.subTitle;

        this.subTitle = subTitle;

        firePropertyChange("subtitle", old, this.subTitle);
    }

    /**
     * Setzt den Titel des Tasks, zb. fuer ProgressBars etc.
     * 
     * @param title String
     */
    protected void setTitle(final String title)
    {
        String old = this.title;

        this.title = title;

        firePropertyChange("title", old, this.title);
    }

    /**
     * Called when this Task has successfully completed, i.e. when its {@code get} method returns a value. Tasks that compute a value should
     * override this method.
     * <p>
     * <p>
     * This method runs on the EDT. It does nothing by default.
     * 
     * @param result the value returned by the {@code get} method
     * @see #done
     * @see #get
     * @see #failed
     */
    protected void succeeded(final T result)
    {
        // NOOP
    }
}
