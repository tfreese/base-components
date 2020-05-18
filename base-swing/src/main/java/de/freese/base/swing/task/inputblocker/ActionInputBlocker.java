package de.freese.base.swing.task.inputblocker;

import javax.swing.Action;

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
     * @param target {@link Action}
     * @param targets {@link Action}[]
     */
    public ActionInputBlocker(final Action target, final Action...targets)
    {
        this(false, target, targets);
    }

    /**
     * Erstellt ein neues {@link ActionInputBlocker} Object.
     *
     * @param changeMouseCursor boolean
     * @param target {@link Action}
     * @param targets {@link Action}[]
     */
    public ActionInputBlocker(final boolean changeMouseCursor, final Action target, final Action...targets)
    {
        super(target, targets);

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
