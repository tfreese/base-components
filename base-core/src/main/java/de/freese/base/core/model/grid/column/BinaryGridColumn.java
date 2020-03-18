// Created: 25.01.2018
package de.freese.base.core.model.grid.column;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Binary-Spalte des Grids.
 *
 * @author Thomas Freese
 */
public class BinaryGridColumn extends AbstractGridColumn<byte[]>
{
    /**
     *
     */
    private static final long serialVersionUID = -728952526278052497L;

    /**
     * Erzeugt eine neue Instanz von {@link BinaryGridColumn}.
     */
    public BinaryGridColumn()
    {
        super();
    }

    /**
     * Erzeugt eine neue Instanz von {@link BinaryGridColumn}.
     *
     * @param name String
     */
    public BinaryGridColumn(final String name)
    {
        super();

        setName(name);
    }

    /**
     * @see de.freese.base.core.model.grid.column.GridColumn#getValue(java.lang.Object)
     */
    @Override
    public byte[] getValue(final Object object)
    {
        if (object == null)
        {
            return null;
        }

        byte[] value = (byte[]) object;

        if (value.length == 0)
        {
            return null;
        }

        return value;
    }

    /**
     * /**
     *
     * @see de.freese.base.core.model.grid.column.AbstractGridColumn#readNullSafe(java.io.DataInput)
     */
    @Override
    protected byte[] readNullSafe(final DataInput dataInput) throws IOException
    {
        int size = dataInput.readInt();

        byte[] value = new byte[size];

        dataInput.readFully(value);

        return value;
    }

    /**
     * @see de.freese.base.core.model.grid.column.AbstractGridColumn#writeNullSafe(java.io.DataOutput, java.lang.Object)
     */
    @Override
    protected void writeNullSafe(final DataOutput dataOutput, final byte[] value) throws IOException
    {
        dataOutput.writeInt(value.length);
        dataOutput.write(value);
    }
}
