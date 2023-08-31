// Created: 25.01.2018
package de.freese.base.core.model.grid.column;

/**
 * @author Thomas Freese
 */
public class LongGridColumn extends AbstractGridColumn<Long> {

    public LongGridColumn() {
        this("long");
    }

    public LongGridColumn(final String name) {
        this(name, null);
    }

    public LongGridColumn(final String name, final String comment) {
        super(Long.class, name, -1, -1, comment);
    }

    @Override
    public Long getValue(final Object object) {
        if (object == null) {
            return null;
        }

        return (Long) object;
    }
}
