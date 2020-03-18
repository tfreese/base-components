/**
 * Created: 18.03.2020
 */

package de.freese.base.core.model.grid.column;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @author Thomas Freese
 * @param <T> Konkreter Typ
 */
public interface GridColumn<T>
{
    /**
     * @return String
     */
    public String getComment();

    /**
     * @return int
     */
    public int getLength();

    /**
     * @return String
     */
    public String getName();

    /**
     * Liefert den Typ der durch diese Spalte repräsentiert wird.
     *
     * @return Class
     */
    public Class<T> getObjectClazz();

    /**
     * @return int
     */
    public int getPrecision();

    /**
     * Konvertiert das Objekt in den Typ der durch diese Spalte repräsentiert wird.
     *
     * @param object Object
     * @return Double
     */
    public T getValue(Object object);

    /**
     * Liest den Wert aus dem Stream, ist der NULL-Marker true, wird null geliefert.
     *
     * @param dataInput {@link DataInput}
     * @return Object
     * @throws IOException Falls was schief geht.
     */
    public T read(DataInput dataInput) throws IOException;

    /**
     * @param comment String
     */
    public void setComment(String comment);

    /**
     * @param length int
     */
    public void setLength(int length);

    /**
     * @param name String
     */
    public void setName(String name);

    /**
     * @param precision int
     */
    public void setPrecision(int precision);

    /**
     * Schreibt den Wert in den Stream, ist das Objekt null, wird nur der NULL-Marker geschrieben.
     *
     * @param dataOutput {@link DataOutput}
     * @param object Object
     * @throws IOException Falls was schief geht.
     */
    public void write(DataOutput dataOutput, Object object) throws IOException;
}