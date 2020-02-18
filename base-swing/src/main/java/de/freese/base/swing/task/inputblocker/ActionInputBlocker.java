package de.freese.base.swing.task.inputblocker;

import javax.swing.Action;

import de.freese.base.swing.task.AbstractTask;

/**
 * InputBlocker fuer eine {@link Action}.
 * 
 * @author Thomas Freese
 */
public class ActionInputBlocker extends AbstractInputBlocker<Action>
{
	/**
	 * Erstellt ein neues {@link ActionInputBlocker} Object.
	 * 
	 * @param task {@link AbstractTask}
	 * @param target {@link Action}
	 * @param targets {@link Action}[]
	 */
	public ActionInputBlocker(final AbstractTask<?, ?> task, final Action target,
			final Action...targets)
	{
		this(task, false, target, targets);
	}

	/**
	 * Erstellt ein neues {@link ActionInputBlocker} Object.
	 * 
	 * @param task {@link AbstractTask}
	 * @param changeMouseCursor boolean
	 * @param target {@link Action}
	 * @param targets {@link Action}[]
	 */
	public ActionInputBlocker(final AbstractTask<?, ?> task, final boolean changeMouseCursor,
			final Action target, final Action...targets)
	{
		super(task, target, targets);

		setChangeMouseCursor(changeMouseCursor);
	}

	/**
	 * @see de.freese.base.swing.task.inputblocker.InputBlocker#block()
	 */
	@Override
	public void block()
	{
		setMouseCursorBusy(true);

		for (Action action : getTargets())
		{
			action.setEnabled(false);
		}
	}

	/**
	 * @see de.freese.base.swing.task.inputblocker.InputBlocker#unblock()
	 */
	@Override
	public void unblock()
	{
		for (Action action : getTargets())
		{
			action.setEnabled(true);
		}

		setMouseCursorBusy(false);
	}
}
