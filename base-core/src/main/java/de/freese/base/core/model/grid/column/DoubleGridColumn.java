// Created: 25.01.2018
package de.freese.base.core.model.grid.column;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @author Thomas Freese
 */
public class DoubleGridColumn extends AbstractGridColumn<Double> {
    public DoubleGridColumn() {
        super(Double.class);
    }

    public DoubleGridColumn(final String name) {
        super(Double.class);

        setName(name);
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

    /**
     * @see de.freese.base.core.model.grid.column.AbstractGridColumn#readNullSafe(java.io.DataInput)
     */
    @Override
    protected Double readNullSafe(final DataInput dataInput) throws IOException {
        return dataInput.readDouble();
    }

    /**
     * @see de.freese.base.core.model.grid.column.AbstractGridColumn#writeNullSafe(java.io.DataOutput, java.lang.Object)
     */
    @Override
    protected void writeNullSafe(final DataOutput dataOutput, final Double value) throws IOException {
        dataOutput.writeDouble(value);
    }
}
