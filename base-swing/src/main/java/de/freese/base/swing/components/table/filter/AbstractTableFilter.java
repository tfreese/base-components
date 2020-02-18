package de.freese.base.swing.components.table.filter;

import java.util.ArrayList;
import java.util.List;
import de.freese.base.core.model.provider.TableContentProvider;
import de.freese.base.swing.eventlist.FilterableEventList;
import de.freese.base.swing.filter.AndFilter;
import de.freese.base.swing.filter.Filter;
import de.freese.base.swing.filter.FilterCondition;

/**
 * Diese Klasse ermoeglicht das Spaltenweise Filtern der Zeilen einer Tabelle.<br>
 * Der Tablefilter wird in die {@link FilterableEventList} gesetzt und beinhaltet alle Spaltenfilter.
 *
 * @author Thomas Freese
 */
public abstract class AbstractTableFilter extends AndFilter implements TableContentProvider
{
    /**
     *
     */
    private List<FilterCondition> filter = null;

    /**
     * Erstellt ein neues {@link AbstractTableFilter} Objekt.
     */
    public AbstractTableFilter()
    {
        super();
    }

    /**
     * @see de.freese.base.swing.filter.AndFilter#getFilter()
     */
    @Override
    protected List<FilterCondition> getFilter()
    {
        if (this.filter == null)
        {
            this.filter = new ArrayList<>();
        }

        return this.filter;
    }

    /**
     * Setzt den Filter fuer eine bestimmte Spalte.
     *
     * @param column int
     * @param filter IFilter
     */
    public void setFilter(final int column, final Filter filter)
    {
        if (getSize() <= column)
        {
            // Hinten Mit Nulls auffuellen
            for (int i = 0; i < ((column - getSize()) + 1); i++)
            {
                getFilter().add(null);
            }
        }

        getFilter().set(column, filter);
        filter.getPropertyChangeSupport().addPropertyChangeListener(this);
    }

    /**
     * @param column int
     * @param filterValue Object
     */
    public void setFilterValue(final int column, final Object filterValue)
    {
        FilterCondition columnFilter = getFilter(column);

        if ((columnFilter != null) && (columnFilter instanceof Filter))
        {
            Filter filter = (Filter) columnFilter;
            filter.setFilterValue(filterValue);
            // fireChanged(null, filterValue);
        }
    }

    /**
     * @see de.freese.base.swing.filter.AndFilter#test(java.lang.Object)
     */
    @Override
    public boolean test(final Object object)
    {
        for (int column = 0; column < getSize(); column++)
        {
            FilterCondition filterCondition = getFilter(column);

            if (filterCondition == null)
            {
                continue;
            }

            Object cellValue = getValueAt(object, column);

            if (!filterCondition.test(cellValue))
            {
                return false;
            }
        }

        return true;
    }
}
