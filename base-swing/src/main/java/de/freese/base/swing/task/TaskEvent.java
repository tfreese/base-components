package de.freese.base.swing.task;

import java.util.EventObject;

/**
 * An encapsulation of the value produced one of the {@code Task} execution methods:
 * {@code doInBackground()}, {@code process}, {@code done}. The source of a {@code TaskEvent} is the
 * {@code Task} that produced the value.
 * 
 * @author Thomas Freese
 * @param <T> Konkreter Typ des Objektes.
 * @see TaskListener
 * @see AbstractTask
 */
public final class TaskEvent<T> extends EventObject
{
	/**
	 *
	 */
	private static final long serialVersionUID = 2520464231674477344L;

	/**
	 * 
	 */
	private final T value;

	/**
	 * Construct a {@code TaskEvent}.
	 * 
	 * @param source the {@code Task} that produced the value.
	 * @param value the value, null if type {@code T} is {@code Void}.
	 */
	public TaskEvent(final AbstractTask<?, ?> source, final T value)
	{
		super(source);

		this.value = value;
	}

	/**
	 * @see java.util.EventObject#getSource()
	 */
	@Override
	public AbstractTask<?, ?> getSource()
	{
		return (AbstractTask<?, ?>) super.getSource();
	}

	/**
	 * Returns the value this event represents.
	 * 
	 * @return the {@code value} constructor argument.
	 */
	public final T getValue()
	{
		return this.value;
	}
}
