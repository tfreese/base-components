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
     * @see de.freese.base.core.model.grid.column.AbstractGridColumn#isNull(java.lang.Object)
     */
    @Override
    public boolean isNull(final Object object)
    {
        if (object == null)
        {
            return true;
        }

        byte[] value = getValueImpl(object);

        if (value.length == 0)
        {
            return true;
        }

        return false;
    }

    /**
     * @see de.freese.base.core.model.grid.column.AbstractGridColumn#getValueImpl(java.lang.Object)
     */
    @Override
    protected byte[] getValueImpl(final Object object)
    {
        return (byte[]) object;
    }

    /**
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
