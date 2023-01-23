// Created: 25.01.2018
package de.freese.base.core.model.grid.column;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @author Thomas Freese
 */
public class LongGridColumn extends AbstractGridColumn<Long>
{
    public LongGridColumn()
    {
        super(Long.class);
    }

    public LongGridColumn(final String name)
    {
        super(Long.class);

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
        return dataInput.readLong();
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
