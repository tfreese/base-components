// Created: 25.01.2018
package de.freese.base.core.model.grid.column;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Double-Spalte des Grids.
 *
 * @author Thomas Freese
 */
public class DoubleGridColumn extends AbstractGridColumn<Double>
{
    /**
     *
     */
    private static final long serialVersionUID = 5426214923097786498L;

    /**
     * Erzeugt eine neue Instanz von {@link DoubleGridColumn}.
     */
    public DoubleGridColumn()
    {
        super();
    }

    /**
     * @see de.freese.base.core.model.grid.column.AbstractGridColumn#getValueImpl(java.lang.Object)
     */
    @Override
    protected Double getValueImpl(final Object object)
    {
        return (Double) object;
    }

    /**
     * @see de.freese.base.core.model.grid.column.AbstractGridColumn#readNullSafe(java.io.DataInput)
     */
    @Override
    protected Double readNullSafe(final DataInput dataInput) throws IOException
    {
        double value = dataInput.readDouble();

        return value;
    }

    /**
     * @see de.freese.base.core.model.grid.column.AbstractGridColumn#writeNullSafe(java.io.DataOutput, java.lang.Object)
     */
    @Override
    protected void writeNullSafe(final DataOutput dataOutput, final Double value) throws IOException
    {
        dataOutput.writeDouble(value);
    }
}
