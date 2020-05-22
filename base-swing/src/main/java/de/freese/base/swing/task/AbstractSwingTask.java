package de.freese.base.swing.task;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.base.swing.task.inputblocker.InputBlocker;

/**
 * Erweitert den {@link SwingWorker} um diverse Methoden und bietet die Möglichkeit detailliertere Listener zu verwenden.<br>
 * Die {@link #done()} Methode ist NICHT nutzbar, diese wurde aufgeteilt in die Methoden:
 * <ul>
 * <li>{@link #succeeded(Object)}</li>
 * <li>{@link #failed(Throwable)}</li>
 * <li>{@link #cancelled()}</li>
 * </ul>
 * Es gibt weiterhin zusätzliche PropertyChangeEvents zu den "progress" und "state" des {@link SwingWorker}s:
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
public abstract class AbstractSwingTask<T, V> extends SwingWorker<T, V> implements SwingTask
{
    /**
     * PropertyChangeListener auf die Properties des {@link SwingWorker}s. Dieser wird durch den Swingworker im EDT gefeuert.<br>
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
        public void propertyChange(final PropertyChangeEvent event)
        {
            String propertyName = event.getPropertyName();

            if ("state".equals(propertyName))
            {
                StateValue state = (StateValue) event.getNewValue();
                AbstractSwingTask<?, ?> task = (AbstractSwingTask<?, ?>) event.getSource();

                switch (state)
                {
                    case STARTED:
                        taskStarted(task);
                        break;
                    case DONE:
                        taskDone(task);
                        break;
                    default:
                        break;
                }
            }
            else if (PROPERTY_PROGRESS.equals(propertyName))
            {
                synchronized (AbstractSwingTask.this)
                {
                    AbstractSwingTask.this.progressPropertyIsValid = true;
                }
            }
        }

        /**
         * @param task {@link AbstractSwingTask}
         */
        private void taskDone(final AbstractSwingTask<?, ?> task)
        {
            synchronized (AbstractSwingTask.this)
            {
                AbstractSwingTask.this.doneTime = System.currentTimeMillis();
            }

            try
            {
                task.removePropertyChangeListener(this);
            }
            finally
            {
                if (getInputBlocker() != null)
                {
                    getInputBlocker().unblock();
                }
            }
        }

        /**
         * @param task {@link AbstractSwingTask}
         */
        private void taskStarted(final AbstractSwingTask<?, ?> task)
        {
            synchronized (AbstractSwingTask.this)
            {
                AbstractSwingTask.this.startTime = System.currentTimeMillis();
            }

            firePropertyChange(PROPERTY_STARTED, null, true);

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
     * Titel des Tasks fuer ProgressBars oder aehnliches,.
     */
    private String title = null;

    /**
     * Erstellt ein neues {@link AbstractSwingTask} Object.
     */
    public AbstractSwingTask()
    {
        this(null);
    }

    /**
     * Erstellt ein neues {@link AbstractSwingTask} Object.
     *
     * @param name {@link String}, Systemweit Einheitliche ID oder aehnliches.
     */
    public AbstractSwingTask(final String name)
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
     * Called when this Task has been cancelled by {@link #cancel(boolean)}.
     * <p>
     * This method runs on the EDT. It does nothing by default.
     *
     * @see #done
     */
    protected void cancelled()
    {
        // NO-OP
    }

    /**
     * @see javax.swing.SwingWorker#done()
     */
    @Override
    protected final void done()
    {
        Runnable runnable = () -> {
            // try
            // {
            if (isCancelled())
            {
                firePropertyChange(PROPERTY_CANCELLED, null, true);
                cancelled();
            }
            else
            {
                try
                {
                    T result = get();

                    firePropertyChange(PROPERTY_SUCCEEDED, null, result);
                    succeeded(result);
                }
                // catch (InterruptedException ex)
                // {
                // firePropertyChange(PROPERTY_INTERRUPTED, null, ex);
                // interrupted(ex);
                // }
                catch (InterruptedException | ExecutionException ex)
                {
                    firePropertyChange(PROPERTY_FAILED, null, ex);
                    failed(ex.getCause());
                }
            }
            // }
            // finally
            // {
            // firePropertyChange(PROPERTY_FINISHED, null, true);
            // finished();
            // }
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

    // /**
    // * Called unconditionally (in a {@code finally} clause) after one of the completion methods, {@code succeeded}, {@code failed}, {@code cancelled}, or
    // * {@code interrupted}, runs. Subclasses can override this method to cleanup before the {@code done} method returns.
    // * <p>
    // * This method runs on the EDT. It does nothing by default.
    // *
    // * @see #done
    // * @see #get
    // * @see #failed
    // */
    // protected void finished()
    // {
    // // NO-OP
    // }

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
     * Returns the length of time this Task has run. If the task hasn't started yet (i.e. if its state is still {@code StateValue.PENDING}), then this method
     * returns 0. Otherwise it returns the duration in the specified time units. For example, to learn how many seconds a Task has run so far:
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
     * @return {@link Logger}
     */
    protected final Logger getLogger()
    {
        return this.logger;
    }

    /**
     * Liefert den Namen des Tasks, Systemweit Einheitliche ID oder ähnliches.
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

    // /**
    // * Called if the Task's Thread is interrupted but not explicitly cancelled.
    // * <p>
    // * This method runs on the EDT. It does nothing by default.
    // *
    // * @param ex the {@code InterruptedException} thrown by {@code get}
    // * @see #cancel
    // * @see #done
    // * @see #get
    // */
    // protected void interrupted(final InterruptedException ex)
    // {
    // getLogger().error(null, ex);
    // }

    /**
     * Equivalent to {@code getState() == StateValue.PENDING}.
     * <p>
     * When a pending Task's state changes to {@code StateValue.STARTED} a PropertyChangeEvent for the "started" property is fired. Similarly when a started
     * Task's state changes to {@code StateValue.DONE}, a "done" PropertyChangeEvent is fired.
     *
     * @return boolean
     */
    public final boolean isPending()
    {
        return getState() == StateValue.PENDING;
    }

    /**
     * Returns true if the {@link #setProgress progress} property has been set. Some Tasks don't update the progress property because it's difficult or
     * impossible to determine how what percentage of the task has been completed. GUI elements that display Task progress, like an application status bar, can
     * use this property to set the @{link JProgressBar#indeterminate indeterminate} @{code JProgressBar} property.
     * <p>
     * A task that does keep the progress property up to date should initialize it to 0, to ensure that {@code isProgressPropertyValid} is always true.
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
     * When a pending Task's state changes to {@code StateValue.STARTED} a PropertyChangeEvent for the "started" property is fired. Similarly when a started
     * Task's state changes to {@code StateValue.DONE}, a "done" PropertyChangeEvent is fired.
     *
     * @return boolean
     */
    public final boolean isStarted()
    {
        return getState() == StateValue.STARTED;
    }

    /**
     * @see javax.swing.SwingWorker#process(java.util.List)
     */
    @Override
    protected void process(final List<V> chunks)
    {
        firePropertyChange(PROPERTY_PROCESS, null, chunks);
    }

    /**
     * @param inputBlocker {@link InputBlocker}
     */
    public void setInputBlocker(final InputBlocker inputBlocker)
    {
        this.inputBlocker = Objects.requireNonNull(inputBlocker, "inputBlocker required");

        addPropertyChangeListener(this.inputBlocker);
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

        firePropertyChange(PROPERTY_SUBTITLE, old, this.subTitle);
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

        firePropertyChange(PROPERTY_TITLE, old, this.title);
    }

    /**
     * Called when this Task has successfully completed, i.e. when its {@code get} method returns a value. Tasks that compute a value should override this
     * method.
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
        // NO-OP
    }
}
