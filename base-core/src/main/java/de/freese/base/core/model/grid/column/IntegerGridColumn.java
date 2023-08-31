// Created: 25.01.2018
package de.freese.base.core.model.grid.column;

/**
 * @author Thomas Freese
 */
public class IntegerGridColumn extends AbstractGridColumn<Integer> {

    public IntegerGridColumn() {
        this("integer");
    }

    public IntegerGridColumn(final String name) {
        this(name, null);
    }

    public IntegerGridColumn(final String name, final String comment) {
        super(Integer.class, name, -1, -1, comment);
    }

    @Override
    public Integer getValue(final Object object) {
        if (object == null) {
            return null;
        }

        return (Integer) object;
    }
}
