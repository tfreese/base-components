package de.freese.base.swing.filter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

/**
 * Filter zum Verknüpfen mehrerer Filter zu einer AND-Bedingung.<br>
 * Dieser Filter reagiert auf die Änderungen seiner Filter und leitet das Event weiter.
 *
 * @author Thomas Freese
 */
public class AndFilter implements FilterCondition, PropertyChangeListener
{
    /**
     *
     */
    private List<FilterCondition> filters;
    /**
     *
     */
    private PropertyChangeSupport propertyChangeSupport;

    /**
     * Hinzufügen eines oder mehrerer Filter.
     *
     * @param filters {@link FilterCondition}[]
     */
    public void addFilter(final FilterCondition... filters)
    {
        for (FilterCondition filter : filters)
        {
            if (!getFilters().contains(filter))
            {
                getFilters().add(filter);
                filter.getPropertyChangeSupport().addPropertyChangeListener(this);
            }
        }
    }

    /**
     * Liefert den Filter am Index.
     *
     * @param index int
     *
     * @return {@link FilterCondition}
     */
    public FilterCondition getFilter(final int index)
    {
        return getFilters().get(index);
    }

    /**
     * @see de.freese.base.swing.SupportsPropertyChange#getPropertyChangeSupport()
     */
    @Override
    public final PropertyChangeSupport getPropertyChangeSupport()
    {
        if (this.propertyChangeSupport == null)
        {
            this.propertyChangeSupport = new PropertyChangeSupport(this);
        }

        return this.propertyChangeSupport;
    }

    /**
     * Liefert die Anzahl der Filter.
     *
     * @return int
     */
    public int getSize()
    {
        return getFilters().size();
    }

    /**
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    @Override
    public void propertyChange(final PropertyChangeEvent evt)
    {
        // Falls sich die Bedingungen innerhalb eines Filters geändert haben
        if (Filter.FILTER_CHANGED.equals(evt.getPropertyName()))
        {
            getPropertyChangeSupport().firePropertyChange(evt);
        }
    }

    /**
     * Entfernen eines Filters.
     *
     * @param filter {@link Filter}
     */
    public void removeFilter(final Filter filter)
    {
        getFilters().remove(filter);
        filter.getPropertyChangeSupport().removePropertyChangeListener(this);
    }

    /**
     * @see de.freese.base.swing.filter.FilterCondition#test(java.lang.Object)
     */
    @Override
    public boolean test(final Object object)
    {
        for (FilterCondition filterCondition : getFilters())
        {
            if (!filterCondition.test(object))
            {
                return false;
            }
        }

        return true;
    }

    /**
     * @return {@link List}<IFilter>
     */
    protected List<FilterCondition> getFilters()
    {
        if (this.filters == null)
        {
            this.filters = new ArrayList<>();
        }

        return this.filters;
    }
}
