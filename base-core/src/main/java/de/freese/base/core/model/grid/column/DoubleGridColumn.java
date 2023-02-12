// Created: 25.01.2018
package de.freese.base.core.model.grid.column;

/**
 * @author Thomas Freese
 */
public class DoubleGridColumn extends AbstractGridColumn<Double> {

    public DoubleGridColumn() {
        super(Double.class, "double", -1, -1, null);
    }

    public DoubleGridColumn(final String name) {
        super(Double.class, name, -1, -1, null);
    }

    public DoubleGridColumn(final String name, final int length, final int precision) {
        super(Double.class, name, length, precision, null);
    }

    public DoubleGridColumn(final String name, final int length, final int precision, final String comment) {
        super(Double.class, name, length, precision, comment);
    }

    /**
     * @see de.freese.base.core.model.grid.column.GridColumn#getValue(java.lang.Object)
     */
    @Override
    public Double getValue(final Object object) {
        if (object == null) {
            return null;
        }

        return (Double) object;
    }
}
