package de.freese.base.swing.task.inputblocker;

import java.beans.PropertyChangeListener;

/**
 * Interface fuer einen InputBlocker eines AbstractTasks.<br>
 * InputBlocker koennen fuer einen Task GUI-Elemente fuer die Eingabe blockieren.
 *
 * @author Thomas Freese
 */
public interface InputBlocker extends PropertyChangeListener
{
    /**
     * Blockiert das Target.
     */
    public void block();

    /**
     * Freigeben des Targets.
     */
    public void unblock();
}