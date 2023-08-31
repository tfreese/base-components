package de.freese.base.swing.task.inputblocker;

import java.awt.Component;
import java.util.function.Consumer;

import javax.swing.Action;

/**
 * InputBlocker für eine GUI-Komponente.
 *
 * @author Thomas Freese
 */
public class DefaultInputBlocker extends AbstractInputBlocker<Object> {
    public DefaultInputBlocker() {
        this(true);
    }

    public DefaultInputBlocker(final boolean changeMouseCursor) {
        super();

        setChangeMouseCursor(changeMouseCursor);
    }

    public DefaultInputBlocker add(final Action action, final Action... actions) {
        addTarget(action);

        for (Action a : actions) {
            addTarget(a);
        }

        return this;
    }

    public DefaultInputBlocker add(final Component component, final Component... components) {
        addTarget(component);

        for (Component c : components) {
            addTarget(c);
        }

        return this;
    }

    @SuppressWarnings("unchecked")
    public DefaultInputBlocker add(final Consumer<Boolean> consumer, final Consumer<Boolean>... consumers) {
        addTarget(consumer);

        for (Consumer<Boolean> c : consumers) {
            addTarget(c);
        }

        return this;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void block() {
        setMouseCursorBusy(true);

        boolean enabled = false;

        for (Object target : getTargets()) {
            if (target instanceof Component c) {
                c.setEnabled(enabled);
            }
            else if (target instanceof Action a) {
                a.setEnabled(enabled);
            }
            else if (target instanceof Consumer c) {
                c.accept(enabled);
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void unblock() {
        boolean enabled = true;

        for (Object target : getTargets()) {
            if (target instanceof Component c) {
                c.setEnabled(enabled);
            }
            else if (target instanceof Action a) {
                a.setEnabled(enabled);
            }
            else if (target instanceof Consumer c) {
                c.accept(enabled);
            }
        }

        setMouseCursorBusy(false);
    }
}
