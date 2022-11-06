package de.freese.base.swing.filter;

import de.freese.base.swing.eventlist.FilterableEventList;

/**
 * Interface für einen Filter für die {@link FilterableEventList}.
 *
 * @author Thomas Freese
 */
public interface Filter extends FilterCondition
{
    String ALL = "alle";

    String EMPTY = "leer";

    String FILTER_CHANGED = "FILTER_CHANGED";

    String NOT_EMPTY = "nicht leer";

    void reset();

    void setFilterValue(Object filterValue);
}
