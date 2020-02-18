package de.freese.base.swing.components.list.filter;

import java.util.Set;

/**
 * Callback Interface des {@link ListFilterAuswahlSelectionListener} zur Bereitstellung relevanter
 * ChildIDs fuer eine Auswahl von Parent Objekten.
 * 
 * @author Thomas Freese
 * @see ListFilterAuswahlSelectionListener
 */
public interface IListFilterAuswahlCallback
{
	/**
	 * Liefert fuer eine Auswahl von Parent Objekte, die IDs der relevanten Childobjekte.
	 * 
	 * @param parentIDs {@link Set}
	 * @return {@link Set}, darf auch null sein
	 */
	public Set<Long> getChildIDs(Set<Long> parentIDs);
}
