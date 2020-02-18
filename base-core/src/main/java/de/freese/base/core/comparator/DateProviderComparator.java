package de.freese.base.core.comparator;

import java.util.Comparator;

import de.freese.base.core.model.Dateable;

/**
 * Comparator fuer Objekte mit {@link Dateable} Interface.
 * 
 * @author Thomas Freese
 */
public class DateProviderComparator implements Comparator<Dateable>
{
	/**
	 * 
	 */
	public static DateProviderComparator COMPARATOR = new DateProviderComparator();

	/**
	 * Erstellt ein neues {@link DateProviderComparator} Object.
	 */
	private DateProviderComparator()
	{
		super();
	}

	/**
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(final Dateable date1, final Dateable date2)
	{
		return date1.getDate().compareTo(date2.getDate());
	}
}
