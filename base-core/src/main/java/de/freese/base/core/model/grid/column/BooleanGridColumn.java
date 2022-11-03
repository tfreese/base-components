// Created: 25.01.2018
package de.freese.base.core.model.grid.column;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serial;

/**
 * Boolean-Spalte des Grids.
 *
 * @author Thomas Freese
 */
public class BooleanGridColumn extends AbstractGridColumn<Boolean>
{
    @Serial
    private static final long serialVersionUID = 9093089212970979017L;

    public BooleanGridColumn()
    {
        super();
    }

    public BooleanGridColumn(final String name)
    {
        super();

        setName(name);
    }

    /**
     * @see de.freese.base.core.model.grid.column.GridColumn#getValue(java.lang.Object)
     */
    @Override
    public Boolean getValue(final Object object)
    {
        if (object == null)
        {
            return null;
        }

        return (Boolean) object;
    }

    /**
     * @see de.freese.base.core.model.grid.column.AbstractGridColumn#readNullSafe(java.io.DataInput)
     */
    @Override
    protected Boolean readNullSafe(final DataInput dataInput) throws IOException
    {
        return dataInput.readBoolean();
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
