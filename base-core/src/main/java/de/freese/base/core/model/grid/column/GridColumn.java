// Created: 18.03.2020
package de.freese.base.core.model.grid.column;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @author Thomas Freese
 */
public interface GridColumn<T> {
    String getComment();

    int getLength();

    String getName();

    int getPrecision();

    Class<T> getType();

    T getValue(Object object);

    T read(DataInput dataInput) throws IOException;

    void setComment(String comment);

    void setLength(int length);

    void setName(String name);

    void setPrecision(int precision);

    void write(DataOutput dataOutput, Object object) throws IOException;
}
