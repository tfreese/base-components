package de.freese.base.swing.components.table;

import javax.swing.RowFilter.Entry;

/**
 * @author Thomas Freese
 */
public class RowFilterEntry<M> extends Entry<M, Integer> {
    private final int column;

    private final M model;

    public RowFilterEntry(final M model, final int column) {
        super();

        this.model = model;
        this.column = column;
    }

    @Override
    public Integer getIdentifier() {
        return this.column;
    }

    @Override
    public M getModel() {
        return this.model;
    }

    @Override
    public Object getValue(final int index) {
        throw new UnsupportedOperationException("getValue(int)");
    }

    @Override
    public int getValueCount() {
        return 1;
    }
}
