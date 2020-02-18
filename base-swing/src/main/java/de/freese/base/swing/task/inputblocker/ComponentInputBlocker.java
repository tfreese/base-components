package de.freese.base.swing.task.inputblocker;

import java.awt.Component;

import de.freese.base.swing.task.AbstractTask;

/**
 * InputBlocker fuer eine {@link Component}.
 * 
 * @author Thomas Freese
 */
public class ComponentInputBlocker extends AbstractInputBlocker<Component>
{
	/**
	 * Erstellt ein neues {@link ComponentInputBlocker} Object.
	 * 
	 * @param task {@link AbstractTask}
	 * @param changeMouseCursor boolean
	 * @param source {@link Component}
	 * @param sources {@link Component}[]
	 */
	public ComponentInputBlocker(final AbstractTask<?, ?> task, final boolean changeMouseCursor,
			final Component source, final Component...sources)
	{
		super(task, source, sources);

		setChangeMouseCursor(changeMouseCursor);
	}

	/**
	 * Erstellt ein neues {@link ComponentInputBlocker} Object.
	 * 
	 * @param task {@link AbstractTask}
	 * @param source {@link Component}
	 * @param sources {@link Component}[]
	 */
	public ComponentInputBlocker(final AbstractTask<?, ?> task, final Component source,
			final Component...sources)
	{
		this(task, false, source, sources);
	}

	/**
	 * @see de.freese.base.swing.task.inputblocker.InputBlocker#block()
	 */
	@Override
	public void block()
	{
		setMouseCursorBusy(true);

		for (Component component : getTargets())
		{
			component.setEnabled(false);
		}
	}

	/**
	 * @see de.freese.base.swing.task.inputblocker.InputBlocker#unblock()
	 */
	@Override
	public void unblock()
	{
		for (Component component : getTargets())
		{
			component.setEnabled(true);
		}

		setMouseCursorBusy(false);
	}
}
