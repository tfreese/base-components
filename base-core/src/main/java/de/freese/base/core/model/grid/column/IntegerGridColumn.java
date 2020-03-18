// Created: 25.01.2018
package de.freese.base.core.model.grid.column;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Integer-Spalte des Grids.
 *
 * @author Thomas Freese
 */
public class IntegerGridColumn extends AbstractGridColumn<Integer>
{
    /**
     *
     */
    private static final long serialVersionUID = -1817847484639082368L;

    /**
     * Erzeugt eine neue Instanz von {@link IntegerGridColumn}.
     */
    public IntegerGridColumn()
    {
        super();
    }

    /**
     * Erzeugt eine neue Instanz von {@link IntegerGridColumn}.
     *
     * @param name String
     */
    public IntegerGridColumn(final String name)
    {
        super();

        setName(name);
    }

    /**
     * @see de.freese.base.core.model.grid.column.GridColumn#getValue(java.lang.Object)
     */
    @Override
    public Integer getValue(final Object object)
    {
        if (object == null)
        {
            return null;
        }

        return (Integer) object;
    }

    /**
     * @see de.freese.base.core.model.grid.column.AbstractGridColumn#readNullSafe(java.io.DataInput)
     */
    @Override
    protected Integer readNullSafe(final DataInput dataInput) throws IOException
    {
        int value = dataInput.readInt();

        return value;
    }

    /**
     * @see de.freese.base.core.model.grid.column.AbstractGridColumn#writeNullSafe(java.io.DataOutput, java.lang.Object)
     */
    @Override
    protected void writeNullSafe(final DataOutput dataOutput, final Integer value) throws IOException
    {
        dataOutput.writeInt(value);
    }
}
