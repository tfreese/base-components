package de.freese.base.swing.task.inputblocker;

import java.awt.Component;
import java.util.function.Consumer;
import javax.swing.Action;

/**
 * InputBlocker für eine GUI-Komponenten.
 *
 * @author Thomas Freese
 */
public class DefaultInputBlocker extends AbstractInputBlocker<Object>
{
    /**
     * Erstellt ein neues {@link DefaultInputBlocker} Object.
     */
    public DefaultInputBlocker()
    {
        this(true);
    }

    /**
     * Erstellt ein neues {@link DefaultInputBlocker} Object.
     *
     * @param changeMouseCursor boolean
     */
    public DefaultInputBlocker(final boolean changeMouseCursor)
    {
        super();

        setChangeMouseCursor(changeMouseCursor);
    }

    /**
     * @param action {@link Action}
     * @param actions {@link Action}[]
     * @return {@link DefaultInputBlocker}
     */
    public DefaultInputBlocker add(final Action action, final Action...actions)
    {
        addTarget(action);

        for (Action a : actions)
        {
            addTarget(a);
        }

        return this;
    }

    /**
     * @param component {@link Component}
     * @param components {@link Component}[]
     * @return {@link DefaultInputBlocker}
     */
    public DefaultInputBlocker add(final Component component, final Component...components)
    {
        addTarget(component);

        for (Component c : components)
        {
            addTarget(c);
        }

        return this;
    }

    /**
     * @param consumer {@link Consumer}
     * @param consumers {@link Consumer}[]
     * @return {@link DefaultInputBlocker}
     */
    @SuppressWarnings("unchecked")
    public DefaultInputBlocker add(final Consumer<Boolean> consumer, final Consumer<Boolean>...consumers)
    {
        addTarget(consumer);

        for (Consumer<Boolean> c : consumers)
        {
            addTarget(c);
        }

        return this;
    }

    /**
     * @see de.freese.base.swing.task.inputblocker.InputBlocker#block()
     */
    @SuppressWarnings("unchecked")
    @Override
    public void block()
    {
        setMouseCursorBusy(true);

        boolean enabled = false;

        for (Object target : getTargets())
        {
            if (target instanceof Component)
            {
                ((Component) target).setEnabled(enabled);
            }
            else if (target instanceof Action)
            {
                ((Action) target).setEnabled(enabled);
            }
            else if (target instanceof Consumer)
            {
                ((Consumer<Boolean>) target).accept(enabled);
            }
        }
    }

    /**
     * @see de.freese.base.swing.task.inputblocker.InputBlocker#unblock()
     */
    @SuppressWarnings("unchecked")
    @Override
    public void unblock()
    {
        boolean enabled = true;

        for (Object target : getTargets())
        {
            if (target instanceof Component)
            {
                ((Component) target).setEnabled(enabled);
            }
            else if (target instanceof Action)
            {
                ((Action) target).setEnabled(enabled);
            }
            else if (target instanceof Consumer)
            {
                ((Consumer<Boolean>) target).accept(enabled);
            }
        }

        setMouseCursorBusy(false);
    }
}
