// Created: 24.01.2018
package de.freese.base.core.model.grid;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import de.freese.base.core.model.grid.column.GridColumn;
import de.freese.base.core.model.grid.factory.DefaultGridColumnFactory;
import de.freese.base.core.model.grid.factory.GridColumnFactory;

/**
 * Definiert die Spalten des Grids.
 *
 * @author Thomas Freese
 */
public class GridMetaData implements Serializable// , Iterable<GridColumn<?>>
{
    /**
     *
     */
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    /**
     *
     */
    private static final long serialVersionUID = 4337541530394314432L;
    /**
     *
     */
    private final List<GridColumn<?>> columns = new ArrayList<>();
    /**
     *
     */
    private final GridColumnFactory gridColumnFactory;

    /**
     * Erstellt ein neues {@link GridMetaData} Object.
     */
    public GridMetaData()
    {
        this(new DefaultGridColumnFactory());
    }

    /**
     * Erstellt ein neues {@link GridMetaData} Object.
     *
     * @param gridColumnFactory {@link GridColumnFactory}
     */
    public GridMetaData(final GridColumnFactory gridColumnFactory)
    {
        super();

        this.gridColumnFactory = Objects.requireNonNull(gridColumnFactory, "gridColumnFactory required");
    }

    /**
     * @param column {@link GridColumn}
     */
    public void addColumn(final GridColumn<?> column)
    {
        // StreamSupport.stream(spliterator(), false);
        // boolean clazzExist = getColumns().stream().anyMatch(c -> column.getObjectClazz().equals(c.getObjectClazz()));
        //
        // if (clazzExist)
        // {
        // String msg = String.format("column for class %s already exist: %s", clazz.getName(), optional.get().getClass().getName());
        // throw new IllegalArgumentException(msg);
        // }

        getColumns().add(column);
    }

    /**
     * @return int
     */
    public int columnCount()
    {
        return getColumns().size();
    }

    /**
     * @param index int
     *
     * @return {@link GridColumn}
     */
    public GridColumn<?> getColumn(final int index)
    {
        return getColumns().get(index);
    }

    /**
     * @return {@link GridColumnFactory}
     */
    public GridColumnFactory getGridColumnFactory()
    {
        return this.gridColumnFactory;
    }

    /**
     * Liest die Grid-Struktur aus dem Stream.
     *
     * @param dataInput {@link DataInput}
     *
     * @throws IOException Falls was schief geht.
     * @throws ClassNotFoundException Falls was schief geht.
     */
    public void readMetaData(final DataInput dataInput) throws IOException, ClassNotFoundException
    {
        // Anzahl Spalten
        int columnCount = dataInput.readInt();

        for (int i = 0; i < columnCount; i++)
        {
            // Object-Class
            int length = dataInput.readInt();
            byte[] bytes = new byte[length];
            dataInput.readFully(bytes);

            String objectClazzName = new String(bytes, DEFAULT_CHARSET);
            Class<?> objectClazz = Class.forName(objectClazzName);
            GridColumn<?> column = this.gridColumnFactory.getColumnForType(objectClazz);

            getColumns().add(column);

            // Name
            boolean isNull = dataInput.readBoolean(); // NULL-Marker

            if (!isNull)
            {
                length = dataInput.readInt();
                bytes = new byte[length];
                dataInput.readFully(bytes);

                String name = new String(bytes, GridMetaData.DEFAULT_CHARSET);
                column.setName(name);
            }

            // Länge
            length = dataInput.readInt();
            column.setLength(length);

            // Precision
            int precision = dataInput.readInt();
            column.setPrecision(precision);

            // Comments
            isNull = dataInput.readBoolean(); // NULL-Marker

            if (!isNull)
            {
                length = dataInput.readInt();
                bytes = new byte[length];
                dataInput.readFully(bytes);

                String comment = new String(bytes, GridMetaData.DEFAULT_CHARSET);
                column.setComment(comment);
            }
        }
    }

    // /**
    // * @see java.lang.Iterable#iterator()
    // */
    // @Override
    // public Iterator<GridColumn<?>> iterator()
    // {
    // return getColumns().iterator();
    // }

    /**
     * Liest die Grid-Struktur aus dem {@link ResultSetMetaData}.
     *
     * @param metaData {@link ResultSetMetaData}
     *
     * @throws SQLException Falls was schiefgeht.
     */
    public void readMetaData(final ResultSetMetaData metaData) throws SQLException
    {
        // Anzahl Spalten
        int columnCount = metaData.getColumnCount();

        for (int c = 1; c <= columnCount; c++)
        {
            GridColumn<?> column = this.gridColumnFactory.getColumnForSQL(metaData.getColumnType(c));
            column.setName(metaData.getColumnLabel(c));
            column.setLength(metaData.getColumnDisplaySize(c));
            column.setPrecision(metaData.getPrecision(c));
            // column.setScale(metaData.getScale(c));

            getColumns().add(column);
        }
    }

    /**
     * @param columnIndex int
     *
     * @return {@link GridColumn}
     */
    public GridColumn<?> removeColumn(final int columnIndex)
    {
        GridColumn<?> column = getColumns().remove(columnIndex);

        return column;
    }

    /**
     * Schreibt die Grid-Struktur in den Stream.
     *
     * @param dataOutput {@link DataOutput}
     *
     * @throws IOException Falls was schief geht.
     */
    public void writeMetaData(final DataOutput dataOutput) throws IOException
    {
        // Anzahl Spalten
        dataOutput.writeInt(columnCount());

        for (GridColumn<?> column : getColumns())
        {
            // Object-Class
            byte[] bytes = column.getObjectClazz().getName().getBytes(DEFAULT_CHARSET);
            dataOutput.writeInt(bytes.length);
            dataOutput.write(bytes);

            // Name
            String name = Optional.ofNullable(column.getName()).orElse("").trim();
            name = name.length() == 0 ? null : name;

            dataOutput.writeBoolean(name == null); // NULL-Marker

            if (name != null)
            {
                bytes = name.getBytes(DEFAULT_CHARSET);
                dataOutput.writeInt(bytes.length);
                dataOutput.write(bytes);
            }

            // Länge
            dataOutput.writeInt(column.getLength());

            // Precision
            dataOutput.writeInt(column.getPrecision());

            // Comments
            String comment = Optional.ofNullable(column.getComment()).orElse("").trim();
            comment = comment.length() == 0 ? null : comment;

            dataOutput.writeBoolean(comment == null); // NULL-Marker

            if (comment != null)
            {
                bytes = comment.getBytes(DEFAULT_CHARSET);
                dataOutput.writeInt(bytes.length);
                dataOutput.write(bytes);
            }
        }
    }

    /**
     * @return {@link List}
     */
    protected List<GridColumn<?>> getColumns()
    {
        return this.columns;
    }
}
