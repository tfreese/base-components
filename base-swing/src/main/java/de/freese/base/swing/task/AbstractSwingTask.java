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
 * @param <T> Typ der {@link #doInBackground()} Methode
 * @param <V> Typ der {@link #publish(Object...)} Methode
 *
 * @author Thomas Freese
 */
public abstract class AbstractSwingTask<T, V> extends SwingWorker<T, V> implements SwingTask {
    /**
     * PropertyChangeListener auf die Properties des {@link SwingWorker}s.<br/>
     * Dieser wird durch den SwingWorker im EDT gefeuert.<br>
     *
     * @author Thomas Freese
     */
    private final class SwingWorkerPCL implements PropertyChangeListener {
        @Override
        public void propertyChange(final PropertyChangeEvent event) {
            final String propertyName = event.getPropertyName();

            if ("state".equals(propertyName)) {
                final StateValue state = (StateValue) event.getNewValue();
                final AbstractSwingTask<?, ?> task = (AbstractSwingTask<?, ?>) event.getSource();

                switch (state) {
                    case STARTED -> taskStarted(task);
                    case DONE -> taskDone(task);
                    default -> {
                        // Empty
                    }
                }
            }
            else if (PROPERTY_PROGRESS.equals(propertyName)) {
                synchronized (AbstractSwingTask.this) {
                    AbstractSwingTask.this.progressPropertyIsValid = true;
                }
            }
        }

        private void taskDone(final AbstractSwingTask<?, ?> task) {
            synchronized (AbstractSwingTask.this) {
                AbstractSwingTask.this.doneTime = System.currentTimeMillis();
            }

            try {
                task.removePropertyChangeListener(this);
            }
            finally {
                if (getInputBlocker() != null) {
                    getInputBlocker().unblock();
                }
            }
        }

        private void taskStarted(final AbstractSwingTask<?, ?> task) {
            synchronized (AbstractSwingTask.this) {
                AbstractSwingTask.this.startTime = System.currentTimeMillis();
            }

            firePropertyChange(PROPERTY_STARTED, null, true);

            if (getInputBlocker() != null) {
                getInputBlocker().block();
            }
        }
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final String name;

    private long doneTime = -1L;
    private InputBlocker inputBlocker;
    private boolean progressPropertyIsValid;
    private long startTime = -1L;
    private String subTitle;
    private String title;

    protected AbstractSwingTask() {
        this(null);
    }

    protected AbstractSwingTask(final String name) {
        super();

        this.name = Objects.requireNonNullElse(name, getClass().getName());

        addPropertyChangeListener(new SwingWorkerPCL());
    }

    /**
     * Liefert die Zeiteinheit, wie lange der Task bis jetzt läuft.
     */
    public long getCurrentDuration(final TimeUnit unit) {
        final long sTime;
        final long currentTime;

        synchronized (this) {
            sTime = this.startTime;
            currentTime = System.currentTimeMillis();
        }

        return getDuration(unit, sTime, currentTime);
    }

    /**
     * Returns the length of time this Task has run.<br/>
     * If the task hasn't started yet (i.e. if its state is still {@code StateValue.PENDING}), then this method returns 0.<br/>
     * Otherwise, it returns the duration in the specified time units.<br/>
     * For example, to learn how many seconds a Task has run so far:
     *
     * <pre>
     * long nSeconds = myTask.getExecutionDuration(TimeUnit.SECONDS);
     * </pre>
     *
     * @param unit the time unit of the return value
     *
     * @return the length of time this Task has run.
     */
    public long getExecutionDuration(final TimeUnit unit) {
        final long sTime;
        final long dTime;

        synchronized (this) {
            sTime = this.startTime;
            dTime = this.doneTime;
        }

        return getDuration(unit, sTime, dTime);
    }

    public InputBlocker getInputBlocker() {
        return this.inputBlocker;
    }

    public String getName() {
        return this.name;
    }

    public String getSubTitle() {
        return this.subTitle;
    }

    public String getTitle() {
        return this.title;
    }

    /**
     * Equivalent to {@code getState() == StateValue.PENDING}.<br/>
     * When a pending Task's state changes to {@code StateValue.STARTED} a PropertyChangeEvent for the "started" property is fired.<br/>
     * Similarly, when a started Task's state changes to {@code StateValue.DONE}, a "done" PropertyChangeEvent is fired.
     */
    public final boolean isPending() {
        return getState() == StateValue.PENDING;
    }

    /**
     * Returns true if the {@link #setProgress progress} property has been set.<br/>
     * Some Tasks don't update the progress property because it's difficult or impossible to determine how what percentage of the task has been completed.<br/>
     * GUI elements that display Task progress, like an application status bar, can use this property to set
     * the @{link JProgressBar#indeterminate indeterminate} @{code JProgressBar} property.<br/>
     * A task that does keep the progress property up to date should initialize it to 0, to ensure that {@code isProgressPropertyValid} is always true.
     *
     * @return true if the {@link #setProgress progress} property has been set.
     */
    public synchronized boolean isProgressPropertyValid() {
        return this.progressPropertyIsValid;
    }

    /**
     * Equivalent to {@code getState() == StateValue.STARTED}.<br/>
     * When a pending Task's state changes to {@code StateValue.STARTED} a PropertyChangeEvent for the "started" property is fired. Similarly, when a started
     * Task's state changes to {@code StateValue.DONE}, a "done" PropertyChangeEvent is fired.
     */
    public final boolean isStarted() {
        return getState() == StateValue.STARTED;
    }

    public void setInputBlocker(final InputBlocker inputBlocker) {
        this.inputBlocker = Objects.requireNonNull(inputBlocker, "inputBlocker required");

        addPropertyChangeListener(this.inputBlocker);
    }

    /**
     * Called when this Task has been cancelled by {@link #cancel(boolean)}.<br/>
     * This method runs on the EDT. It does nothing by default.
     */
    protected void cancelled() {
        // Empty
    }

    // /**
    // * Called if the Task's Thread is interrupted but not explicitly cancelled.<br/>
    // * This method runs on the EDT. It does nothing by default.
    // *
    // * @param ex the {@code InterruptedException} thrown by {@code get}
    // */
    // protected void interrupted(final InterruptedException ex)
    // {
    // getLogger().error(ex.getMessage(), ex);
    // }

    @Override
    protected final void done() {
        final Runnable runnable = () -> {
            // try
            // {
            if (isCancelled()) {
                firePropertyChange(PROPERTY_CANCELLED, null, true);
                cancelled();
            }
            else {
                try {
                    final T result = get();

                    firePropertyChange(PROPERTY_SUCCEEDED, null, result);
                    succeeded(result);
                }
                // catch (InterruptedException ex) {
                // firePropertyChange(PROPERTY_INTERRUPTED, null, ex);
                // interrupted(ex);
                // }
                catch (InterruptedException | ExecutionException ex) {
                    if (ex instanceof InterruptedException) {
                        // Restore interrupted state.
                        Thread.currentThread().interrupt();
                    }

                    firePropertyChange(PROPERTY_FAILED, null, ex);
                    failed(ex.getCause());
                }
            }
            // }
            // finally {
            // firePropertyChange(PROPERTY_FINISHED, null, true);
            // finished();
            // }
        };

        SwingUtilities.invokeLater(runnable);
    }

    /**
     * Called when an execution of this Task fails and an {@code ExecutionException} is thrown by {@code get}.<br/>
     * This method runs on the EDT. It Logs an error message by default.
     *
     * @param cause the {@link Throwable#getCause cause} of the {@code ExecutionException}
     */
    protected void failed(final Throwable cause) {
        getLogger().error(cause.getMessage(), cause);
    }

    protected final Logger getLogger() {
        return this.logger;
    }

    @Override
    protected void process(final List<V> chunks) {
        firePropertyChange(PROPERTY_PROCESS, null, chunks);
    }

    /**
     * Convenience method that sets the {@code progress} property to <code>percentage * 100</code>.
     *
     * @param percentage a value in the range 0.0 ... 1.0 inclusive
     */
    protected final void setProgress(final float percentage) {
        if (percentage < 0.0F || percentage > 1.0F) {
            throw new IllegalArgumentException("invalid percentage");
        }

        setProgress(Math.round(percentage * 100.0F));
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
     */
    protected final void setProgress(final float value, final float min, final float max) {
        if (min >= max) {
            throw new IllegalArgumentException("invalid range: min >= max");
        }

        if (value < min || value > max) {
            throw new IllegalArgumentException("invalid value");
        }

        final float percentage = (value - min) / (max - min);
        setProgress(Math.round(percentage * 100.0F));
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
     */
    protected final void setProgress(final int value, final int min, final int max) {
        if (min >= max) {
            throw new IllegalArgumentException("invalid range: min >= max");
        }

        if (value < min || value > max) {
            throw new IllegalArgumentException("invalid value");
        }

        final float percentage = (float) (value - min) / (float) (max - min);
        setProgress(Math.round(percentage * 100.0F));
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
     */
    protected final void setProgress(final long value, final long min, final long max) {
        if (min >= max) {
            throw new IllegalArgumentException("invalid range: min >= max");
        }

        if (value < min || value > max) {
            throw new IllegalArgumentException("invalid value");
        }

        final float percentage = (float) (value - min) / (float) (max - min);
        setProgress(Math.round(percentage * 100.0F));
    }

    protected void setSubTitle(final String subTitle) {
        final String old = this.subTitle;

        this.subTitle = subTitle;

        firePropertyChange(PROPERTY_SUBTITLE, old, this.subTitle);
    }

    protected void setTitle(final String title) {
        final String old = this.title;

        this.title = title;

        firePropertyChange(PROPERTY_TITLE, old, this.title);
    }

    /**
     * Called when this Task has successfully completed, i.e. when its {@code get} method returns a value. Tasks that compute a value should override this method.<br/>
     * This method runs on the EDT. It does nothing by default.
     *
     * @param result the value returned by the {@code get} method
     */
    protected void succeeded(final T result) {
        // Empty
    }

    private long getDuration(final TimeUnit unit, final long startTime, final long endTime) {
        final long dt;

        if (startTime == -1L) {
            dt = 0L;
        }
        else if (endTime == -1L) {
            dt = System.currentTimeMillis() - startTime;
        }
        else {
            dt = endTime - startTime;
        }

        return unit.convert(Math.max(0L, dt), TimeUnit.MILLISECONDS);
    }
}
