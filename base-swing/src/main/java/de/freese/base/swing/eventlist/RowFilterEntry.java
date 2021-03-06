package de.freese.base.swing.eventlist;

import javax.swing.RowFilter;
import javax.swing.RowFilter.Entry;

/**
 * Objekt fuer die Nutzung der {@link RowFilter}-API.
 *
 * @author Thomas Freese
 * @param <M> Konkreter Typ des zu filternden Objektes.
 */
public class RowFilterEntry<M> extends Entry<M, Integer>
{
    /**
     * 
     */
    private final int column;

    /**
     * 
     */
    private final M model;

    /**
     * Erstellt ein neues {@link RowFilterEntry} Object.
     * 
     * @param model Object
     * @param column int
     */
    public RowFilterEntry(final M model, final int column)
    {
        super();

        this.model = model;
        this.column = column;
    }

    /**
     * @see javax.swing.RowFilter.Entry#getIdentifier()
     */
    @Override
    public Integer getIdentifier()
    {
        return Integer.valueOf(this.column);
    }

    /**
     * @see javax.swing.RowFilter.Entry#getModel()
     */
    @Override
    public M getModel()
    {
        return this.model;
    }

    /**
     * @see javax.swing.RowFilter.Entry#getValue(int)
     */
    @Override
    public Object getValue(final int index)
    {
        throw new UnsupportedOperationException("getValue(int)");
    }

    /**
     * @see javax.swing.RowFilter.Entry#getValueCount()
     */
    @Override
    public int getValueCount()
    {
        return 1;
    }
}
