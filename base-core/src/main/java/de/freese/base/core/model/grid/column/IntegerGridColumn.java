// Created: 25.01.2018
package de.freese.base.core.model.grid.column;

/**
 * @author Thomas Freese
 */
public class IntegerGridColumn extends AbstractGridColumn<Integer> {

    public IntegerGridColumn() {
        super(Integer.class, "integer", -1, -1, null);
    }

    public IntegerGridColumn(final String name) {
        super(Integer.class, name, -1, -1, null);
    }

    public IntegerGridColumn(final String name, final String comment) {
        super(Integer.class, name, -1, -1, comment);
    }

    /**
     * @see de.freese.base.core.model.grid.column.GridColumn#getValue(java.lang.Object)
     */
    @Override
    public Integer getValue(final Object object) {
        if (object == null) {
            return null;
        }

        return (Integer) object;
    }
}
