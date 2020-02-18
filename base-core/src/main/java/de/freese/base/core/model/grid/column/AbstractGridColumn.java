// Created: 25.01.2018
package de.freese.base.core.model.grid.column;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Objects;

/**
 * Definiert eine Spalte des Grids.
 *
 * @author Thomas Freese
 * @param <T> Konkreter Spalten-Typ
 */
public abstract class AbstractGridColumn<T> implements Serializable
{
    /**
     *
     */
    private static final long serialVersionUID = -3866701962046000404L;

    /**
    *
    */
    private String comment = null;

    /**
    *
    */
    private int length = -1;

    /**
    *
    */
    private String name = null;

    /**
     *
     */
    private final Class<T> objectClazz;

    /**
    *
    */
    private int precision = -1;

    /**
     * Erzeugt eine neue Instanz von {@link AbstractGridColumn}.
     */
    @SuppressWarnings("unchecked")
    public AbstractGridColumn()
    {
        super();

        // Das hier funktioniert nur, wenn die erbende Klasse nicht auch generisch ist !
        // z.B. : public class IntegerGridColumn extends AbstractGridColumn<Integer>
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();

        this.objectClazz = (Class<T>) parameterizedType.getActualTypeArguments()[0];
    }

    /**
     * Erzeugt eine neue Instanz von {@link AbstractGridColumn}.
     *
     * @param objectClazz Class
     */
    public AbstractGridColumn(final Class<T> objectClazz)
    {
        super();

        this.objectClazz = objectClazz;
    }

    /**
     * @param comment String
     * @return {@link AbstractGridColumn}
     */
    public AbstractGridColumn<T> comment(final String comment)
    {
        setComment(comment);

        return this;
    }

    /**
     * @return String
     */
    public String getComment()
    {
        return this.comment;
    }

    /**
     * @return int
     */
    public int getLength()
    {
        return this.length;
    }

    /**
     * @return String
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Liefert den Typ der durch diese Spalte repräsentiert wird.
     *
     * @return Class
     */
    public Class<T> getObjectClazz()
    {
        return this.objectClazz;
    }

    /**
     * @return int
     */
    public int getPrecision()
    {
        return this.precision;
    }

    /**
     * Konvertiert das Objekt in den Typ der durch diese Spalte repräsentiert wird.
     *
     * @param object Object
     * @return Double
     */
    public T getValue(final Object object)
    {
        if (isNull(object))
        {
            return null;
        }

        if (getObjectClazz().equals(object.getClass()))
        {
            return getValueImpl(object);
        }

        String msg = String.format("Can not convert %s to %s.", object.getClass().getName(), getObjectClazz().getName());
        throw new IllegalStateException(msg);
    }

    /**
     * @param object Object
     * @return boolean
     */
    public boolean isNull(final Object object)
    {
        if (object == null)
        {
            return true;
        }

        return false;
    }

    /**
     * @param length int
     * @return {@link AbstractGridColumn}
     */
    public AbstractGridColumn<T> length(final int length)
    {
        setLength(length);

        return this;
    }

    /**
     * @param name String
     * @return {@link AbstractGridColumn}
     */
    public AbstractGridColumn<T> name(final String name)
    {
        setName(name);

        return this;
    }

    /**
     * @param precision int
     * @return {@link AbstractGridColumn}
     */
    public AbstractGridColumn<T> precision(final int precision)
    {
        setPrecision(precision);

        return this;
    }

    /**
     * Liest den Wert aus dem Stream, ist der NULL-Marker true, wird null geliefert.
     *
     * @param dataInput {@link DataInput}
     * @return Object
     * @throws IOException Falls was schief geht.
     */
    public final T read(final DataInput dataInput) throws IOException
    {
        boolean isNull = dataInput.readBoolean(); // NULL-Marker

        if (isNull)
        {
            return null;
        }

        T value = readNullSafe(dataInput);

        return value;
    }

    /**
     * @param comment String
     */
    public void setComment(final String comment)
    {
        this.comment = comment;
    }

    /**
     * @param length int
     */
    public void setLength(final int length)
    {

        this.length = length;
    }

    /**
     * @param name String
     */
    public void setName(final String name)
    {
        this.name = Objects.requireNonNull(name, "name required");
    }

    /**
     * @param precision int
     */
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
        builder.append("GridColumn [objectClazz=");
        builder.append(this.objectClazz);
        builder.append(", name=");
        builder.append(this.name);
        builder.append(", comment=");
        builder.append(this.comment);
        builder.append(", length=");
        builder.append(this.length);
        builder.append(", precision=");
        builder.append(this.precision);
        builder.append("]");

        return builder.toString();
    }

    /**
     * Schreibt den Wert in den Stream, ist das Objekt null, wird nur der NULL-Marker geschrieben.
     *
     * @param dataOutput {@link DataOutput}
     * @param object Object
     * @throws IOException Falls was schief geht.
     */
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
     * Konvertiert das Objekt in den Typ der durch diese Spalte repräsentiert wird.
     *
     * @param object Object
     * @return Double
     */
    protected abstract T getValueImpl(final Object object);

    /**
     * Liest den Wert aus dem Stream, der NULL-Marker wurde bereits in {@link #read(DataInput)} gelesen.
     *
     * @param dataInput {@link DataInput}
     * @return Object
     * @throws IOException Falls was schief geht.
     */
    protected abstract T readNullSafe(final DataInput dataInput) throws IOException;

    /**
     * Schreibt den Wert in den Stream, der NULL-Marker wurde bereits in {@link #write(DataOutput, Object)} gesetzt.
     *
     * @param dataOutput {@link DataOutput}
     * @param value Object
     * @throws IOException Falls was schief geht.
     */
    protected abstract void writeNullSafe(final DataOutput dataOutput, final T value) throws IOException;
}
