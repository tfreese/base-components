// Created: 25.01.2018
package de.freese.base.core.model.grid.column;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serial;

import de.freese.base.core.model.grid.GridMetaData;

/**
 * String-Spalte des Grids.
 *
 * @author Thomas Freese
 */
public class StringGridColumn extends AbstractGridColumn<String>
{
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -6201540763441206227L;

    /**
     * Erzeugt eine neue Instanz von {@link StringGridColumn}.
     */
    public StringGridColumn()
    {
        super();
    }

    /**
     * Erzeugt eine neue Instanz von {@link StringGridColumn}.
     *
     * @param name String
     */
    public StringGridColumn(final String name)
    {
        super();

        setName(name);
    }

    /**
     * @see de.freese.base.core.model.grid.column.GridColumn#getValue(java.lang.Object)
     */
    @Override
    public String getValue(final Object object)
    {
        if (object == null)
        {
            return null;
        }

        return (String) object;
    }

    /**
     * @see de.freese.base.core.model.grid.column.AbstractGridColumn#readNullSafe(java.io.DataInput)
     */
    @Override
    protected String readNullSafe(final DataInput dataInput) throws IOException
    {
        int length = dataInput.readInt();
        byte[] bytes = new byte[length];

        dataInput.readFully(bytes);

        String value = new String(bytes, GridMetaData.DEFAULT_CHARSET);

        return value;
    }

    /**
     * @see de.freese.base.core.model.grid.column.AbstractGridColumn#writeNullSafe(java.io.DataOutput, java.lang.Object)
     */
    @Override
    protected void writeNullSafe(final DataOutput dataOutput, final String value) throws IOException
    {
        byte[] bytes = value.getBytes(GridMetaData.DEFAULT_CHARSET);

        dataOutput.writeInt(bytes.length);
        dataOutput.write(bytes);
    }
}
