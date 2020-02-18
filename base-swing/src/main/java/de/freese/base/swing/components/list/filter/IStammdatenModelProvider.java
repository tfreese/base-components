package de.freese.base.swing.components.list.filter;

import de.freese.base.swing.components.list.filter.callback.AbstractListFilterAuswahlCallback;

/**
 * Interface fuer den {@link IListFilterAuswahlCallback} zur Lieferung der verwendeten Stammdaten.
 * 
 * @author Thomas Freese
 */
public interface IStammdatenModelProvider
{
	/**
	 * Liefert die Stammdaten dessen Objekt im {@link AbstractListFilterAuswahlCallback} generisch
	 * gecastet wird.
	 * 
	 * @return Object
	 * @see AbstractListFilterAuswahlCallback getModel()
	 */
	public Object getStammdatenModel();
}
