package de.freese.base.swing.filter;

import java.beans.PropertyChangeSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.base.swing.eventlist.FilterableEventList;

/**
 * Basisklasse eines Filters fuer eine {@link FilterableEventList}.
 *
 * @author Thomas Freese
 */
public abstract class AbstractFilter implements Filter
{
    /**
     *
     */
    private Object filterValue;
    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());
    /**
     *
     */
    private PropertyChangeSupport propertyChangeSupport;

    /**
     * Liefert die Werte mit dem der Filter arbeitet.
     *
     * @return Object
     */
    protected Object getFilterValue()
    {
        return this.filterValue;
    }

    /**
     * @return {@link Logger}
     */
    protected final Logger getLogger()
    {
        return this.logger;
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
     * @see de.freese.base.swing.filter.Filter#reset()
     */
    @Override
    public void reset()
    {
        Object old = this.filterValue;

        this.filterValue = null;

        getPropertyChangeSupport().firePropertyChange(FILTER_CHANGED, old, this.filterValue);
    }

    /**
     * @see de.freese.base.swing.filter.Filter#setFilterValue(java.lang.Object)
     */
    @Override
    public void setFilterValue(final Object filterValue)
    {
        Object old = this.filterValue;

        this.filterValue = filterValue;

        getPropertyChangeSupport().firePropertyChange(FILTER_CHANGED, old, this.filterValue);
    }
}
