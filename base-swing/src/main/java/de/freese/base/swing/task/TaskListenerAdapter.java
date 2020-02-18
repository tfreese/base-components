package de.freese.base.swing.task;

import java.util.List;

/**
 * Adapterklasse fer den {@link TaskListener}.
 * 
 * @author Thomas Freese
 * @param <T> Typ der AbstractTask.doInBackground() Methode
 * @param <V> Typ der AbstractTask.publish(Object...) Methode
 */
public class TaskListenerAdapter<T, V> implements TaskListener<T, V>
{
	/**
	 * Erstellt ein neues {@link TaskListenerAdapter} Object.
	 */
	public TaskListenerAdapter()
	{
		super();
	}

	/**
	 * @see de.freese.base.swing.task.TaskListener#cancelled(de.freese.base.swing.task.TaskEvent)
	 */
	@Override
	public void cancelled(final TaskEvent<Void> event)
	{
		// NOOP
	}

	/**
	 * @see de.freese.base.swing.task.TaskListener#doInBackground(de.freese.base.swing.task.TaskEvent)
	 */
	@Override
	public void doInBackground(final TaskEvent<Void> event)
	{
		// NOOP
	}

	/**
	 * @see de.freese.base.swing.task.TaskListener#failed(de.freese.base.swing.task.TaskEvent)
	 */
	@Override
	public void failed(final TaskEvent<Throwable> event)
	{
		// NOOP
	}

	/**
	 * @see de.freese.base.swing.task.TaskListener#finished(de.freese.base.swing.task.TaskEvent)
	 */
	@Override
	public void finished(final TaskEvent<Void> event)
	{
		// NOOP
	}

	/**
	 * @see de.freese.base.swing.task.TaskListener#interrupted(de.freese.base.swing.task.TaskEvent)
	 */
	@Override
	public void interrupted(final TaskEvent<InterruptedException> event)
	{
		// NOOP
	}

	/**
	 * @see de.freese.base.swing.task.TaskListener#process(de.freese.base.swing.task.TaskEvent)
	 */
	@Override
	public void process(final TaskEvent<List<V>> event)
	{
		// NOOP
	}

	/**
	 * @see de.freese.base.swing.task.TaskListener#progress(de.freese.base.swing.task.TaskEvent)
	 */
	@Override
	public void progress(final TaskEvent<Integer> event)
	{
		// NOOP
	}

	/**
	 * @see de.freese.base.swing.task.TaskListener#succeeded(de.freese.base.swing.task.TaskEvent)
	 */
	@Override
	public void succeeded(final TaskEvent<T> event)
	{
		// NOOP
	}
}
