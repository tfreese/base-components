package de.freese.base.swing.components.table;

import javax.swing.RowFilter.Entry;

/**
 * @author Thomas Freese
 */
public class RowFilterEntry<M> extends Entry<M, Integer>
{
    private final int column;

    private final M model;

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
        return this.column;
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