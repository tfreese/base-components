package de.freese.base.swing.filter;

import java.beans.PropertyChangeSupport;

import de.freese.base.swing.eventlist.FilterableEventList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basisklasse eines Filters für eine {@link FilterableEventList}.
 *
 * @author Thomas Freese
 */
public abstract class AbstractFilter implements Filter
{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Object filterValue;

    private PropertyChangeSupport propertyChangeSupport;

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

    protected Object getFilterValue()
    {
        return this.filterValue;
    }

    protected final Logger getLogger()
    {
        return this.logger;
    }
}
