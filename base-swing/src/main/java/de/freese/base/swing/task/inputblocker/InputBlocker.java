package de.freese.base.swing.task.inputblocker;

import java.beans.PropertyChangeListener;

import de.freese.base.swing.task.AbstractSwingTask;

/**
 * Interface für einen InputBlocker eines {@link AbstractSwingTask}.<br>
 * InputBlocker können für einen Task GUI-Elemente für die Eingabe blockieren.
 *
 * @author Thomas Freese
 */
public interface InputBlocker extends PropertyChangeListener
{
    void block();

    void unblock();
}
