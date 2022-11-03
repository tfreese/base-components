// Created: 25.01.2018
package de.freese.base.core.model.grid.column;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

/**
 * Definiert eine Spalte des Grids.
 *
 * @param <T> Konkreter Spalten-Typ
 *
 * @author Thomas Freese
 */
public abstract class AbstractGridColumn<T> implements Serializable, GridColumn<T>
{
    @Serial
    private static final long serialVersionUID = -3866701962046000404L;

    private final Class<T> objectClazz;

    private String comment;

    private int length = -1;

    private String name;

    private int precision = -1;

    protected AbstractGridColumn()
    {
        super();

        // Das hier funktioniert nur, wenn die erbende Klasse nicht auch generisch ist !
        // z.B. : public class IntegerGridColumn extends AbstractGridColumn<Integer>
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();

        this.objectClazz = (Class<T>) parameterizedType.getActualTypeArguments()[0];
    }

    protected AbstractGridColumn(final Class<T> objectClazz)
    {
        super();

        this.objectClazz = objectClazz;
    }

    /**
     * @see de.freese.base.core.model.grid.column.GridColumn#getComment()
     */
    @Override
    public String getComment()
    {
        return this.comment;
    }

    /**
     * @see de.freese.base.core.model.grid.column.GridColumn#getLength()
     */
    @Override
    public int getLength()
    {
        return this.length;
    }

    /**
     * @see de.freese.base.core.model.grid.column.GridColumn#getName()
     */
    @Override
    public String getName()
    {
        return this.name;
    }

    /**
     * @see de.freese.base.core.model.grid.column.GridColumn#getObjectClazz()
     */
    @Override
    public Class<T> getObjectClazz()
    {
        return this.objectClazz;
    }

    /**
     * @see de.freese.base.core.model.grid.column.GridColumn#getPrecision()
     */
    @Override
    public int getPrecision()
    {
        return this.precision;
    }

    /**
     * @see de.freese.base.core.model.grid.column.GridColumn#read(java.io.DataInput)
     */
    @Override
    public final T read(final DataInput dataInput) throws IOException
    {
        boolean isNull = dataInput.readBoolean(); // NULL-Marker

        if (isNull)
        {
            return null;
        }

        return readNullSafe(dataInput);
    }

    /**
     * @see de.freese.base.core.model.grid.column.GridColumn#setComment(java.lang.String)
     */
    @Override
    public void setComment(final String comment)
    {
        this.comment = comment;
    }

    /**
     * @see de.freese.base.core.model.grid.column.GridColumn#setLength(int)
     */
    @Override
    public void setLength(final int length)
    {

        this.length = length;
    }

    /**
     * @see de.freese.base.core.model.grid.column.GridColumn#setName(java.lang.String)
     */
    @Override
    public void setName(final String name)
    {
        this.name = name;
    }

    /**
     * @see de.freese.base.core.model.grid.column.GridColumn#setPrecision(int)
     */
    @Override
    public void setPrecision(final int precision)
    {
        this.precision = precision;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("GridColumn [");
        builder.append("name=").append(this.name);
        builder.append(", objectClazz=").append(this.objectClazz.getSimpleName());
        builder.append(", comment=").append(this.comment);
        builder.append(", length=").append(this.length);
        builder.append(", precision=").append(this.precision);
        builder.append("]");

        return builder.toString();
    }

    /**
     * @see de.freese.base.core.model.grid.column.GridColumn#write(java.io.DataOutput, java.lang.Object)
     */
    @Override
    public final void write(final DataOutput dataOutput, final Object object) throws IOException
    {
        T value = getValue(object);

        dataOutput.writeBoolean(value == null); // NULL-Marker

        if (value == null)
        {
            return;
        }

        writeNullSafe(dataOutput, value);
    }

    /**
     * Liest den Wert aus dem Stream, der NULL-Marker wurde bereits in {@link #read(DataInput)} gelesen.
     */
    protected abstract T readNullSafe(final DataInput dataInput) throws IOException;

    /**
     * Schreibt den Wert in den Stream, der NULL-Marker wurde bereits in {@link #write(DataOutput, Object)} gesetzt.
     */
    protected abstract void writeNullSafe(final DataOutput dataOutput, final T value) throws IOException;
}
