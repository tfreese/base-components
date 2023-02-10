// Created: 25.01.2018
package de.freese.base.core.model.grid.column;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @author Thomas Freese
 */
public class IntegerGridColumn extends AbstractGridColumn<Integer> {
    public IntegerGridColumn() {
        super(Integer.class);
    }

    public IntegerGridColumn(final String name) {
        super(Integer.class);

        setName(name);
    }

    /**
     * @see de.freese.base.core.model.grid.column.GridColumn#getValue(java.lang.Object)
     */
    @Override
    public Integer getValue(final Object object) {
        if (object == null) {
            return null;
        }

        return (Integer) object;
    }

    /**
     * @see de.freese.base.core.model.grid.column.AbstractGridColumn#readNullSafe(java.io.DataInput)
     */
    @Override
    protected Integer readNullSafe(final DataInput dataInput) throws IOException {
        return dataInput.readInt();
    }

    /**
     * @see de.freese.base.core.model.grid.column.AbstractGridColumn#writeNullSafe(java.io.DataOutput, java.lang.Object)
     */
    @Override
    protected void writeNullSafe(final DataOutput dataOutput, final Integer value) throws IOException {
        dataOutput.writeInt(value);
    }
}
