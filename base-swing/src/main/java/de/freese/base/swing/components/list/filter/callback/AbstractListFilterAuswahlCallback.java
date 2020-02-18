package de.freese.base.swing.components.list.filter.callback;

import de.freese.base.swing.components.list.filter.IListFilterAuswahlCallback;
import de.freese.base.swing.components.list.filter.IStammdatenModelProvider;
import de.freese.base.swing.components.list.filter.ListFilterAuswahlSelectionListener;

/**
 * Basisklasse fuer den Callback des {@link ListFilterAuswahlSelectionListener} zur Bereitstellung
 * relevanter ChildIDs fuer eine Auswahl von Parent Objekten.
 * 
 * @author Thomas Freese
 * @param <T> Konkreter Typ des Models
 */
public abstract class AbstractListFilterAuswahlCallback<T> implements IListFilterAuswahlCallback
{
	/**
	 * 
	 */
	private final IStammdatenModelProvider modelProvider;

	/**
	 * Erstellt ein neues {@link AbstractListFilterAuswahlCallback} Objekt.
	 * 
	 * @param modelProvider {@link IStammdatenModelProvider}
	 */
	public AbstractListFilterAuswahlCallback(final IStammdatenModelProvider modelProvider)
	{
		super();

		this.modelProvider = modelProvider;
	}

	/**
	 * Liefert das konkret gecastete Datenmodel.
	 * 
	 * @return Object
	 */
	@SuppressWarnings("unchecked")
	protected final T getModel()
	{
		return (T) this.modelProvider.getStammdatenModel();
	}
}
