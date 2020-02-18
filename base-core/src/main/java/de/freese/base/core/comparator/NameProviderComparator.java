package de.freese.base.core.comparator;

import de.freese.base.core.model.NameProvider;
import java.io.Serializable;
import java.util.Comparator;

/**
 * Comparator fuer Objekte mit {@link NameProvider} Interface.
 * 
 * @author Thomas Freese
 */
public class NameProviderComparator implements Comparator<NameProvider>, Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = -8050987895414171984L;

	/**
	 * Erstellt ein neues {@link NameProviderComparator} Objekt.
	 */
	public NameProviderComparator()
	{
		super();
	}

	/**
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(final NameProvider o1, final NameProvider o2)
	{
		return o1.getName().compareTo(o2.getName());
	}
}
