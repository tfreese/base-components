/**
 * Created: 24.01.2018
 */

package de.freese.base.core.model.grid;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import de.freese.base.core.model.grid.column.AbstractGridColumn;
import de.freese.base.core.model.grid.column.BinaryGridColumn;
import de.freese.base.core.model.grid.column.BooleanGridColumn;
import de.freese.base.core.model.grid.column.DateGridColumn;
import de.freese.base.core.model.grid.column.DoubleGridColumn;
import de.freese.base.core.model.grid.column.IntegerGridColumn;
import de.freese.base.core.model.grid.column.LongGridColumn;
import de.freese.base.core.model.grid.column.StringGridColumn;

/**
 * Definiert die Struktur / Spalten des Grids.
 *
 * @author Thomas Freese
 */
public class GridMetaData implements Serializable, Iterable<AbstractGridColumn<?>>
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
     * Liefert für den SQL-Type die entsprechende Column.
     *
     * @param sqlType int
     * @return {@link AbstractGridColumn}
     */
    public static AbstractGridColumn<?> getColumnForSQL(final int sqlType)
    {
        AbstractGridColumn<?> column = null;

        switch (sqlType)
        {
            case Types.BINARY:
            case Types.VARBINARY:
                column = new BinaryGridColumn();
                break;

            case Types.BOOLEAN:
                column = new BooleanGridColumn();
                break;

            case Types.DATE:
                column = new DateGridColumn();
                break;

            case Types.FLOAT:
            case Types.DOUBLE:
                column = new DoubleGridColumn();
                break;

            case Types.TINYINT:
            case Types.SMALLINT:
            case Types.INTEGER:
                column = new IntegerGridColumn();
                break;

            case Types.BIGINT:
            case Types.DECIMAL:
            case Types.NUMERIC:
                column = new LongGridColumn();
                break;

            case Types.VARCHAR:
                column = new StringGridColumn();
                break;

            default:
                throw new UnsupportedOperationException("sqlType " + sqlType + " is not supported");
        }

        return column;
    }

    /**
     * Liefert für den Object-Typ die entsprechende Column.
     *
     * @param objectClazz Class
     * @return {@link AbstractGridColumn}
     */
    public static AbstractGridColumn<?> getColumnForType(final Class<?> objectClazz)
    {
        AbstractGridColumn<?> column = null;

        if (byte[].class.equals(objectClazz))
        {
            column = new BinaryGridColumn();
        }
        else if (Boolean.class.equals(objectClazz))
        {
            column = new BooleanGridColumn();
        }
        else if (Date.class.equals(objectClazz))
        {
            column = new DateGridColumn();
        }
        else if (Double.class.equals(objectClazz))
        {
            column = new DoubleGridColumn();
        }
        else if (Integer.class.equals(objectClazz))
        {
            column = new IntegerGridColumn();
        }
        else if (Long.class.equals(objectClazz))
        {
            column = new LongGridColumn();
        }
        else if (String.class.equals(objectClazz))
        {
            column = new StringGridColumn();
        }
        else
        {
            throw new UnsupportedOperationException("objectClass " + objectClazz.getName() + " is not supported");
        }

        return column;
    }

    /**
     *
     */
    private List<AbstractGridColumn<?>> columns = new ArrayList<>();

    /**
     * Erstellt ein neues {@link GridMetaData} Object.
     */
    public GridMetaData()
    {
        super();
    }

    /**
     * @param column {@link AbstractGridColumn}
     */
    public void addColumn(final AbstractGridColumn<?> column)
    {
        Class<?> clazz = column.getObjectClazz();

        // StreamSupport.stream(spliterator(), false);
        Optional<AbstractGridColumn<?>> optional = this.columns.stream().filter(c -> clazz.equals(c.getObjectClazz())).findAny();

        if (optional.isPresent())
        {
            String msg = String.format("column for class %s already exist: %s", clazz.getName(), optional.get().getClass().getName());
            throw new IllegalArgumentException(msg);
        }

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
     * @param columnIndex int
     * @return String
     */
    public String getColumnComment(final int columnIndex)
    {
        return getColumns().get(columnIndex).getComment();
    }

    /**
     * @param columnIndex int
     * @return int
     */
    public int getColumnLength(final int columnIndex)
    {
        return getColumns().get(columnIndex).getLength();
    }

    /**
     * @param columnIndex int
     * @return String
     */
    public String getColumnName(final int columnIndex)
    {
        return getColumns().get(columnIndex).getName();
    }

    /**
     * @param columnIndex int
     * @return Class
     */
    public Class<?> getColumnObjectClazz(final int columnIndex)
    {
        return getColumns().get(columnIndex).getObjectClazz();
    }

    /**
     * @param columnIndex int
     * @return int
     */
    public int getColumnPrecision(final int columnIndex)
    {
        return getColumns().get(columnIndex).getPrecision();
    }

    /**
     * @param columnIndex int
     * @param object Object
     * @return String
     */
    @SuppressWarnings("unchecked")
    public <T> T getValue(final int columnIndex, final Object object)
    {
        return (T) getColumns().get(columnIndex).getValue(object);
    }

    /**
     * @see java.lang.Iterable#iterator()
     */
    @Override
    public Iterator<AbstractGridColumn<?>> iterator()
    {
        return getColumns().iterator();
    }

    /**
     * Liest die Grid-Struktur aus dem Stream.
     *
     * @param dataInput {@link DataInput}
     * @throws IOException Falls was schief geht.
     * @throws ClassNotFoundException Falls was schief geht.
     */
    public void read(final DataInput dataInput) throws IOException, ClassNotFoundException
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
            AbstractGridColumn<?> column = getColumnForType(objectClazz);

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

    /**
     * Liest die Grid-Struktur aus dem {@link ResultSetMetaData}.
     *
     * @param metaData {@link ResultSetMetaData}
     * @throws SQLException Falls was schief geht.
     */
    public void read(final ResultSetMetaData metaData) throws SQLException
    {
        // Anzahl Spalten
        int columnCount = metaData.getColumnCount();

        for (int c = 1; c <= columnCount; c++)
        {
            AbstractGridColumn<?> column = getColumnForSQL(metaData.getColumnType(c));
            column.setName(metaData.getColumnLabel(c));
            column.setLength(metaData.getColumnDisplaySize(c));
            column.setPrecision(metaData.getPrecision(c));
            // column.setScale(metaData.getScale(c));

            getColumns().add(column);
        }
    }

    /**
     * Liest eine aus dem Stream.
     *
     * @param dataInput {@link DataInput}
     * @return Object[]
     * @throws IOException Falls was schief geht.
     * @throws ClassNotFoundException Falls was schief geht.
     */
    public Object[] readRow(final DataInput dataInput) throws IOException, ClassNotFoundException
    {
        Object[] row = new Object[columnCount()];

        for (int c = 0; c < columnCount(); c++)
        {
            row[c] = getColumns().get(c).read(dataInput);
        }

        return row;
    }

    /**
     * Liest eine aus dem Stream.
     *
     * @param resultSet {@link ResultSet}
     * @return Object[]
     * @throws SQLException Falls was schief geht.
     */
    public Object[] readRow(final ResultSet resultSet) throws SQLException
    {
        Object[] row = new Object[columnCount()];

        for (int c = 1; c <= columnCount(); c++)
        {
            row[c - 1] = resultSet.getObject(c);
        }

        return row;
    }

    /**
     * @param columnIndex int
     * @return {@link AbstractGridColumn}
     */
    public AbstractGridColumn<?> removeColumn(final int columnIndex)
    {
        AbstractGridColumn<?> column = getColumns().remove(columnIndex);

        return column;
    }

    /**
     * Schreibt die Grid-Struktur in den Stream.
     *
     * @param dataOutput {@link DataOutput}
     * @throws IOException Falls was schief geht.
     */
    public void write(final DataOutput dataOutput) throws IOException
    {
        // Anzahl Spalten
        dataOutput.writeInt(columnCount());

        for (AbstractGridColumn<?> column : getColumns())
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
     * Schreibt eine Row in den Stream.
     *
     * @param dataOutput {@link DataOutput}
     * @param row Object[]
     * @throws IOException Falls was schief geht.
     */
    public void writeRow(final DataOutput dataOutput, final Object[] row) throws IOException
    {
        for (int c = 0; c < columnCount(); c++)
        {
            getColumns().get(c).write(dataOutput, row[c]);
        }
    }

    /**
     * @return {@link List}
     */
    protected List<AbstractGridColumn<?>> getColumns()
    {
        return this.columns;
    }
}
