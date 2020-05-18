package de.freese.base.swing.task.inputblocker;

import java.awt.Component;

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
     * @param changeMouseCursor boolean
     * @param source {@link Component}
     * @param sources {@link Component}[]
     */
    public ComponentInputBlocker(final boolean changeMouseCursor, final Component source, final Component...sources)
    {
        super(source, sources);

        setChangeMouseCursor(changeMouseCursor);
    }

    /**
     * Erstellt ein neues {@link ComponentInputBlocker} Object.
     *
     * @param source {@link Component}
     * @param sources {@link Component}[]
     */
    public ComponentInputBlocker(final Component source, final Component...sources)
    {
        this(false, source, sources);
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
