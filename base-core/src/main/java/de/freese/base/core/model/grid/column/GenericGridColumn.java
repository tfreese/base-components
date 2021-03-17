// Created: 25.01.2018
package de.freese.base.core.model.grid.column;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Function;
import de.freese.base.core.function.ThrowingBiConsumer;
import de.freese.base.core.function.ThrowingFunction;

/**
 * Generische-Spalte des Grids.
 *
 * @author Thomas Freese
 * @param <T> Konkreter Spalten-Typ
 */
public class GenericGridColumn<T> extends AbstractGridColumn<T>
{
    /**
     *
     */
    private static final long serialVersionUID = -2794233911590975341L;

    /**
     *
     */
    private final Function<Object, T> mapper;

    /**
    *
    */
    private final ThrowingFunction<DataInput, T, IOException> reader;

    /**
    *
    */
    private final ThrowingBiConsumer<DataOutput, T, IOException> writer;

    /**
     * Erzeugt eine neue Instanz von {@link GenericGridColumn}.
     *
     * @param objectClazz Class
     * @param mapper {@link Function}
     * @param writer {@link ThrowingBiConsumer}
     * @param reader {@link ThrowingFunction}
     */
    public GenericGridColumn(final Class<T> objectClazz, final Function<Object, T> mapper, final ThrowingBiConsumer<DataOutput, T, IOException> writer,
            final ThrowingFunction<DataInput, T, IOException> reader)
    {
        super(Objects.requireNonNull(objectClazz, "objectClazz required"));

        this.mapper = Objects.requireNonNull(mapper, "mapper required");
        this.writer = Objects.requireNonNull(writer, "writer required");
        this.reader = Objects.requireNonNull(reader, "reader required");
    }

    /**
     * @see de.freese.base.core.model.grid.column.GridColumn#getValue(java.lang.Object)
     */
    @Override
    public T getValue(final Object object)
    {
        if (object == null)
        {
            return null;
        }

        return this.mapper.apply(object);
    }

    /**
     * @see de.freese.base.core.model.grid.column.AbstractGridColumn#readNullSafe(java.io.DataInput)
     */
    @Override
    protected T readNullSafe(final DataInput dataInput) throws IOException
    {
        return this.reader.apply(dataInput);
    }

    /**
     * @see de.freese.base.core.model.grid.column.AbstractGridColumn#writeNullSafe(java.io.DataOutput, java.lang.Object)
     */
    @Override
    protected void writeNullSafe(final DataOutput dataOutput, final T value) throws IOException
    {
        this.writer.accept(dataOutput, value);
    }
}
