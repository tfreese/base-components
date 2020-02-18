package de.freese.base.swing.components.list.filter.callback;

import java.util.Collections;
import java.util.Set;

import de.freese.base.swing.components.list.filter.IListFilterAuswahlCallback;

/**
 * Leere Implementierung.
 * 
 * @author Thomas Freese
 */
public class EmptyListFilterAuswahlCallback implements IListFilterAuswahlCallback
{
	/**
	 * 
	 */
	private static final IListFilterAuswahlCallback INSTANCE = new EmptyListFilterAuswahlCallback();

	/**
	 * @return {@link IListFilterAuswahlCallback}
	 */
	public static IListFilterAuswahlCallback getInstance()
	{
		return INSTANCE;
	}

	/**
	 * Erstellt ein neues {@link EmptyListFilterAuswahlCallback} Objekt.
	 */
	private EmptyListFilterAuswahlCallback()
	{
		super();
	}

	/**
	 * @see de.freese.base.swing.components.list.filter.IListFilterAuswahlCallback#getChildIDs(java.util.Set)
	 */
	@Override
	public Set<Long> getChildIDs(final Set<Long> parentIDs)
	{
		return Collections.emptySet();
	}
}
