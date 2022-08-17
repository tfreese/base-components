// Created: 25.01.2018
package de.freese.base.core.model.grid.column;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serial;

/**
 * Long-Spalte des Grids.
 *
 * @author Thomas Freese
 */
public class LongGridColumn extends AbstractGridColumn<Long>
{
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 8557737117908767551L;

    /**
     * Erzeugt eine neue Instanz von {@link LongGridColumn}.
     */
    public LongGridColumn()
    {
        super();
    }

    /**
     * Erzeugt eine neue Instanz von {@link LongGridColumn}.
     *
     * @param name String
     */
    public LongGridColumn(final String name)
    {
        super();

        setName(name);
    }

    /**
     * @see de.freese.base.core.model.grid.column.GridColumn#getValue(java.lang.Object)
     */
    @Override
    public Long getValue(final Object object)
    {
        if (object == null)
        {
            return null;
        }

        return (Long) object;
    }

    /**
     * @see de.freese.base.core.model.grid.column.AbstractGridColumn#readNullSafe(java.io.DataInput)
     */
    @Override
    protected Long readNullSafe(final DataInput dataInput) throws IOException
    {
        long value = dataInput.readLong();

        return value;
    }

    /**
     * @see de.freese.base.core.model.grid.column.AbstractGridColumn#writeNullSafe(java.io.DataOutput, java.lang.Object)
     */
    @Override
    protected void writeNullSafe(final DataOutput dataOutput, final Long value) throws IOException
    {
        dataOutput.writeLong(value);
    }
}
