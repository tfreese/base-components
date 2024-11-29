// Created: 25.01.2018
package de.freese.base.core.model.grid.column;

/**
 * @author Thomas Freese
 */
public class BinaryGridColumn extends AbstractGridColumn<byte[]> {

    public BinaryGridColumn() {
        this("binary");
    }

    public BinaryGridColumn(final String name) {
        this(name, null);
    }

    public BinaryGridColumn(final String name, final String comment) {
        super(byte[].class, name, -1, -1, comment);
    }

    @Override
    @SuppressWarnings("java:S1168")
    public byte[] getValue(final Object object) {
        if (object == null) {
            return null;
        }

        final byte[] value = (byte[]) object;

        if (value.length == 0) {
            return null;
        }

        return value;
    }
}
