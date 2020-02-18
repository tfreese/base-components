package de.freese.base.swing.task;

import java.util.List;

import javax.swing.SwingWorker;

/**
 * Listener used for observing {@code Task} execution. A {@code ITaskListener} is particularly
 * useful for monitoring the the intermediate results {@link AbstractTask#publish published} by a
 * Task in situations where it's not practical to override the Task's {@link AbstractTask#process
 * process} method. Note that if what you really want to do is monitor a Task's state and progress,
 * a PropertyChangeListener is probably more appropriate.
 * <p>
 * The Task class runs all ITaskListener methods on the event dispatching thread and the source of
 * all TaskEvents is the Task object.
 * 
 * @see AbstractTask#addTaskListener
 * @see AbstractTask#removeTaskListener
 * @see AbstractTask#addPropertyChangeListener
 * @author Hans Muller (Hans.Muller@Sun.COM)
 * @author Thomas Freese
 * @param <T> the result type returned by this {@code SwingWorker's} {@code doInBackground} and
 *            {@code get} methods
 * @param <V> the type used for carrying out intermediate results by this {@code SwingWorker's}
 *            {@code publish} and {@code process} methods
 */
public interface TaskListener<T, V>
{
	/**
	 * Called after the Task's {@link AbstractTask#cancelled cancelled} method is called. The
	 * {@code event's} source is the Task and its value is null.
	 * 
	 * @param event a TaskEvent whose source is the {@code Task} object, value is null
	 * @see AbstractTask#cancelled
	 * @see TaskEvent#getSource
	 * @see TaskEvent#getValue
	 */
	public void cancelled(TaskEvent<Void> event);

	/**
	 * Called just before the Task's {@link AbstractTask#doInBackground doInBackground} method is
	 * called, i.e. just before the task begins running. The {@code event's} source is the Task and
	 * its value is null.
	 * 
	 * @param event a TaskEvent whose source is the {@code Task} object, value is null
	 * @see AbstractTask#doInBackground
	 * @see TaskEvent#getSource
	 * @see TaskEvent#getValue
	 */
	public void doInBackground(TaskEvent<Void> event);

	/**
	 * Called after the Task's {@link AbstractTask#failed failed} completion method is called. The
	 * event's value is the Throwable passed to {@code Task.failed()}.
	 * 
	 * @param event a TaskEvent whose source is the {@code Task} object, and whose value is the
	 *            Throwable passed to {@code Task.failed()}.
	 * @see AbstractTask#failed
	 * @see TaskEvent#getSource
	 * @see TaskEvent#getValue
	 */
	public void failed(TaskEvent<Throwable> event);

	/**
	 * Called after the Task's {@link AbstractTask#finished finished} method is called. The
	 * {@code event's} source is the Task and its value is null.
	 * 
	 * @param event a TaskEvent whose source is the {@code Task} object, value is null.
	 * @see AbstractTask#finished
	 * @see TaskEvent#getSource
	 * @see TaskEvent#getValue
	 */
	public void finished(TaskEvent<Void> event);

	/**
	 * Called after the Task's {@link AbstractTask#interrupted interrupted} method is called. The
	 * {@code event's} source is the Task and its value is the InterruptedException passed to
	 * {@code Task.interrupted()}.
	 * 
	 * @param event a TaskEvent whose source is the {@code Task} object, and whose value is the
	 *            InterruptedException passed to {@code Task.interrupted()}.
	 * @see AbstractTask#interrupted
	 * @see TaskEvent#getSource
	 * @see TaskEvent#getValue
	 */
	public void interrupted(TaskEvent<InterruptedException> event);

	/**
	 * Called each time the Task's {@link AbstractTask#process process} method is called. The value
	 * of the event is the list of values passed to the process method.
	 * 
	 * @param event a TaskEvent whose source is the {@code Task} object and whose value is a list of
	 *            the values passed to the {@code Task.process()} method
	 * @see AbstractTask#doInBackground
	 * @see AbstractTask#process
	 * @see TaskEvent#getSource
	 * @see TaskEvent#getValue
	 */
	public void process(TaskEvent<List<V>> event);

	/**
	 * Called after the Task's {@link SwingWorker#setProgress progress} method is called. The
	 * {@code event's} source is the Task and its value is an integer.
	 * 
	 * @param event a TaskEvent whose source is the {@code Task} object, value is null
	 * @see SwingWorker#setProgress
	 * @see TaskEvent#getSource
	 * @see TaskEvent#getValue
	 */
	public void progress(TaskEvent<Integer> event);

	/**
	 * Called after the Task's {@link AbstractTask#succeeded succeeded} completion method is called.
	 * The event's value is the value returned by the Task's {@code get} method, i.e. the value that
	 * is computed by {@link AbstractTask#doInBackground}.
	 * 
	 * @param event a TaskEvent whose source is the {@code Task} object, and whose value is the
	 *            value returned by {@code Task.get()}.
	 * @see AbstractTask#succeeded
	 * @see TaskEvent#getSource
	 * @see TaskEvent#getValue
	 */
	public void succeeded(TaskEvent<T> event);
}
