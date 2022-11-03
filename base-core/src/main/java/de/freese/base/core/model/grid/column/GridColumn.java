// Created: 18.03.2020
package de.freese.base.core.model.grid.column;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @param <T> Konkreter Typ
 *
 * @author Thomas Freese
 */
public interface GridColumn<T>
{
    String getComment();

    int getLength();

    String getName();

    /**
     * Liefert den Typ der durch diese Spalte repräsentiert wird.
     */
    Class<T> getObjectClazz();

    int getPrecision();

    /**
     * Konvertiert das Objekt in den Typ der durch diese Spalte repräsentiert wird.
     */
    T getValue(Object object);

    /**
     * Liest den Wert aus dem Stream, ist der NULL-Marker true, wird null geliefert.
     */
    T read(DataInput dataInput) throws IOException;

    void setComment(String comment);

    void setLength(int length);

    void setName(String name);

    void setPrecision(int precision);

    /**
     * Schreibt den Wert in den Stream, ist das Objekt null, wird nur der NULL-Marker geschrieben.
     */
    void write(DataOutput dataOutput, Object object) throws IOException;
}
