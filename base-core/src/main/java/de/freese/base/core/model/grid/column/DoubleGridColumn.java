// Created: 25.01.2018
package de.freese.base.core.model.grid.column;

/**
 * @author Thomas Freese
 */
public class DoubleGridColumn extends AbstractGridColumn<Double> {

    public DoubleGridColumn() {
        this("double");
    }

    public DoubleGridColumn(final String name) {
        this(name, -1, -1);
    }

    public DoubleGridColumn(final String name, final int length, final int precision) {
        this(name, length, precision, null);
    }

    public DoubleGridColumn(final String name, final int length, final int precision, final String comment) {
        super(Double.class, name, length, precision, comment);
    }

    @Override
    public Double getValue(final Object object) {
        if (object == null) {
            return null;
        }

        return (Double) object;
    }
}
