package de.freese.base.swing.filter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

/**
 * Filter zum verknuepfen mehrerer Filter zu einer AND-Bedingung.<br>
 * Dieser Filter reagiert auf die Aenderungen seiner Filter und leitet das Event weiter.
 *
 * @author Thomas Freese
 */
public class AndFilter implements FilterCondition, PropertyChangeListener
{
    /**
     *
     */
    private List<FilterCondition> filter;

    /**
     *
     */
    private PropertyChangeSupport propertyChangeSupport;

    /**
     * Erstellt ein neues {@link AndFilter} Object.
     */
    public AndFilter()
    {
        super();
    }

    /**
     * Hinzufuegen eines oder mehrerer Filter.
     *
     * @param filters {@link FilterCondition}[]
     */
    public void addFilter(final FilterCondition...filters)
    {
        for (FilterCondition filter : filters)
        {
            if (!getFilter().contains(filter))
            {
                getFilter().add(filter);
                filter.getPropertyChangeSupport().addPropertyChangeListener(this);
            }
        }
    }

    /**
     * @return {@link List}<IFilter>
     */
    protected List<FilterCondition> getFilter()
    {
        if (this.filter == null)
        {
            this.filter = new ArrayList<>();
        }

        return this.filter;
    }

    /**
     * Liefert den Filter am Index.
     *
     * @param index int
     * @return {@link FilterCondition}
     */
    public FilterCondition getFilter(final int index)
    {
        return getFilter().get(index);
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
        return getFilter().size();
    }

    /**
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    @Override
    public void propertyChange(final PropertyChangeEvent evt)
    {
        // Falls sich die Bedingungen innerhalb eines Filters geaendert haben
        if (Filter.FILTER_CHANGED.equals(evt.getPropertyName()))
        {
            getPropertyChangeSupport().firePropertyChange(evt);
        }
    }

    /**
     * Entfernen eines Filter.
     *
     * @param filter {@link Filter}
     */
    public void removeFilter(final Filter filter)
    {
        getFilter().remove(filter);
        filter.getPropertyChangeSupport().removePropertyChangeListener(this);
    }

    /**
     * @see de.freese.base.swing.filter.FilterCondition#test(java.lang.Object)
     */
    @Override
    public boolean test(final Object object)
    {
        for (FilterCondition filterCondition : getFilter())
        {
            if (!filterCondition.test(object))
            {
                return false;
            }
        }

        return true;
    }
}
