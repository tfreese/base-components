// Created: 25.01.2018
package de.freese.base.core.model.grid.column;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serial;
import java.util.Date;

/**
 * Date-Spalte des Grids.
 *
 * @author Thomas Freese
 */
public class DateGridColumn extends AbstractGridColumn<Date>
{
    @Serial
    private static final long serialVersionUID = -3731527807869940447L;

    public DateGridColumn()
    {
        super();
    }

    public DateGridColumn(final String name)
    {
        super();

        setName(name);
    }

    /**
     * @see de.freese.base.core.model.grid.column.GridColumn#getValue(java.lang.Object)
     */
    @Override
    public Date getValue(final Object object)
    {
        if (object == null)
        {
            return null;
        }

        return (Date) object;
    }

    /**
     * @see de.freese.base.core.model.grid.column.AbstractGridColumn#readNullSafe(java.io.DataInput)
     */
    @Override
    protected Date readNullSafe(final DataInput dataInput) throws IOException
    {
        long time = dataInput.readLong();

        return new Date(time);
    }

    /**
     * @see de.freese.base.core.model.grid.column.AbstractGridColumn#writeNullSafe(java.io.DataOutput, java.lang.Object)
     */
    @Override
    protected void writeNullSafe(final DataOutput dataOutput, final Date value) throws IOException
    {
        dataOutput.writeLong(value.getTime());
    }
}
