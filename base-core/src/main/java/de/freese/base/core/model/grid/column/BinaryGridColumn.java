// Created: 25.01.2018
package de.freese.base.core.model.grid.column;

/**
 * @author Thomas Freese
 */
public class BinaryGridColumn extends AbstractGridColumn<byte[]> {

    public BinaryGridColumn() {
        super(byte[].class, "binary", -1, -1, null);
    }

    public BinaryGridColumn(final String name) {
        super(byte[].class, name, -1, -1, null);
    }

    public BinaryGridColumn(final String name, final String comment) {
        super(byte[].class, name, -1, -1, comment);
    }

    /**
     * @see de.freese.base.core.model.grid.column.GridColumn#getValue(java.lang.Object)
     */
    @Override
    public byte[] getValue(final Object object) {
        if (object == null) {
            return null;
        }

        byte[] value = (byte[]) object;

        if (value.length == 0) {
            return null;
        }

        return value;
    }
}
