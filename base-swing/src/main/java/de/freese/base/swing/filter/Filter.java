package de.freese.base.swing.filter;

import de.freese.base.swing.eventlist.FilterableEventList;

/**
 * Interface fuer einen Filter fuer die {@link FilterableEventList}.
 * 
 * @author Thomas Freese
 */
public interface Filter extends FilterCondition
{
	/**
	 * 
	 */
	public static final String ALL = "alle";

	/**
	 * 
	 */
	public static final String EMPTY = "leer";

	/**
	 * 
	 */
	public static final String FILTER_CHANGED = "FILTER_CHANGED";

	/**
	 * 
	 */
	public static final String NOT_EMPTY = "nicht leer";

	/**
	 * Setzt den Filter zurueck.
	 */
	public void reset();

	/**
	 * Setzt den Wert mit dem der Filter arbeiten soll.
	 * 
	 * @param filterValue Object[]
	 */
	public void setFilterValue(Object filterValue);
}
