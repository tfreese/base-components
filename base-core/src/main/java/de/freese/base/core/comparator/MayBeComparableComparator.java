/**
 * Created: 06.04.2012
 */

package de.freese.base.core.comparator;

import java.util.Comparator;

/**
 * {@link Comparator} für Objekte welche VIELLEICHT {@link Comparable} sind.
 * 
 * @author Thomas Freese
 */
public class MayBeComparableComparator implements Comparator<Object>
{
	/**
	 * Erstellt ein neues {@link MayBeComparableComparator} Object.
	 */
	public MayBeComparableComparator()
	{
		super();
	}

	/**
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public int compare(final Object o1, final Object o2)
	{
		if (o1 instanceof Comparable)
		{
			return ((Comparable<Object>) o1).compareTo(o2);
		}

		return 0;
	}
}
