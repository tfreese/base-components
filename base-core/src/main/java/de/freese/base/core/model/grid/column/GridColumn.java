// Created: 18.03.2020
package de.freese.base.core.model.grid.column;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @author Thomas Freese
 *
 * @param <T> Konkreter Typ
 */
public interface GridColumn<T>
{
    /**
     * @return String
     */
    String getComment();

    /**
     * @return int
     */
    int getLength();

    /**
     * @return String
     */
    String getName();

    /**
     * Liefert den Typ der durch diese Spalte repräsentiert wird.
     *
     * @return Class
     */
    Class<T> getObjectClazz();

    /**
     * @return int
     */
    int getPrecision();

    /**
     * Konvertiert das Objekt in den Typ der durch diese Spalte repräsentiert wird.
     *
     * @param object Object
     *
     * @return Double
     */
    T getValue(Object object);

    /**
     * Liest den Wert aus dem Stream, ist der NULL-Marker true, wird null geliefert.
     *
     * @param dataInput {@link DataInput}
     *
     * @return Object
     *
     * @throws IOException Falls was schief geht.
     */
    T read(DataInput dataInput) throws IOException;

    /**
     * @param comment String
     */
    void setComment(String comment);

    /**
     * @param length int
     */
    void setLength(int length);

    /**
     * @param name String
     */
    void setName(String name);

    /**
     * @param precision int
     */
    void setPrecision(int precision);

    /**
     * Schreibt den Wert in den Stream, ist das Objekt null, wird nur der NULL-Marker geschrieben.
     *
     * @param dataOutput {@link DataOutput}
     * @param object Object
     *
     * @throws IOException Falls was schief geht.
     */
    void write(DataOutput dataOutput, Object object) throws IOException;
}
