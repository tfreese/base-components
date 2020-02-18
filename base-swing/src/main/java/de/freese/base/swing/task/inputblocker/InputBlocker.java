package de.freese.base.swing.task.inputblocker;

/**
 * Interface fuer einen InputBlocker eines AbstractTasks.<br>
 * InputBlocker koennen fuer einen Task GUI-Elemente fuer die Eingabe blockieren.
 * 
 * @author Thomas Freese
 */
public interface InputBlocker
{
	/**
	 * Blockiert das Target.
	 */
	public void block();

	/**
	 * Freigeben der Target Blockierung.
	 */
	public void unblock();
}