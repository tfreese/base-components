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
    String ALL = "alle";

    /**
     *
     */
    String EMPTY = "leer";

    /**
     *
     */
    String FILTER_CHANGED = "FILTER_CHANGED";

    /**
     *
     */
    String NOT_EMPTY = "nicht leer";

    /**
     * Setzt den Filter zurueck.
     */
    void reset();

    /**
     * Setzt den Wert mit dem der Filter arbeiten soll.
     *
     * @param filterValue Object[]
     */
    void setFilterValue(Object filterValue);
}
