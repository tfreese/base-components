// Created: 25.01.2018
package de.freese.base.core.model.grid.column;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;

/**
 * @author Thomas Freese
 */
public class DateGridColumn extends AbstractGridColumn<Date> {
    public DateGridColumn() {
        super(Date.class);
    }

    public DateGridColumn(final String name) {
        super(Date.class);

        setName(name);
    }

    /**
     * @see de.freese.base.core.model.grid.column.GridColumn#getValue(java.lang.Object)
     */
    @Override
    public Date getValue(final Object object) {
        if (object == null) {
            return null;
        }

        return (Date) object;
    }

    /**
     * @see de.freese.base.core.model.grid.column.AbstractGridColumn#readNullSafe(java.io.DataInput)
     */
    @Override
    protected Date readNullSafe(final DataInput dataInput) throws IOException {
        long time = dataInput.readLong();

        return new Date(time);
    }

    /**
     * @see de.freese.base.core.model.grid.column.AbstractGridColumn#writeNullSafe(java.io.DataOutput, java.lang.Object)
     */
    @Override
    protected void writeNullSafe(final DataOutput dataOutput, final Date value) throws IOException {
        dataOutput.writeLong(value.getTime());
    }
}
