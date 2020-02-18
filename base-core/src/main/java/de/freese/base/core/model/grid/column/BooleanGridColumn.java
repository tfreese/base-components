// Created: 25.01.2018
package de.freese.base.core.model.grid.column;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Boolean-Spalte des Grids.
 *
 * @author Thomas Freese
 */
public class BooleanGridColumn extends AbstractGridColumn<Boolean>
{
    /**
     *
     */
    private static final long serialVersionUID = 9093089212970979017L;

    /**
     * Erzeugt eine neue Instanz von {@link BooleanGridColumn}.
     */
    public BooleanGridColumn()
    {
        super();
    }

    /**
     * @see de.freese.base.core.model.grid.column.AbstractGridColumn#getValueImpl(java.lang.Object)
     */
    @Override
    protected Boolean getValueImpl(final Object object)
    {
        return (Boolean) object;
    }

    /**
     * @see de.freese.base.core.model.grid.column.AbstractGridColumn#readNullSafe(java.io.DataInput)
     */
    @Override
    protected Boolean readNullSafe(final DataInput dataInput) throws IOException
    {
        boolean value = dataInput.readBoolean();

        return value;
    }

    /**
     * @see de.freese.base.core.model.grid.column.AbstractGridColumn#writeNullSafe(java.io.DataOutput, java.lang.Object)
     */
    @Override
    protected void writeNullSafe(final DataOutput dataOutput, final Boolean value) throws IOException
    {
        dataOutput.writeBoolean(value);
    }
}
