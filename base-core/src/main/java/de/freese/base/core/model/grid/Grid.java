// Created: 25.01.2018
package de.freese.base.core.model.grid;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import de.freese.base.core.model.grid.column.AbstractGridColumn;
import de.freese.base.core.model.grid.column.GridColumn;

/**
 * Dynamische Tabelle-Struktur.
 *
 * @author Thomas Freese
 */
public class Grid implements Serializable
{
    /**
     *
     */
    private static final long serialVersionUID = -6649946412204459833L;

    /**
    *
    */
    private final GridMetaData gridMetaData;

    /**
     *
     */
    private final List<Object[]> rows = new ArrayList<>();

    /**
     * Erzeugt eine neue Instanz von {@link Grid}.
     */
    public Grid()
    {
        this(new GridMetaData());
    }

    /**
     * Erzeugt eine neue Instanz von {@link Grid}.
     *
     * @param gridMetaData {@link GridMetaData}
     */
    public Grid(final GridMetaData gridMetaData)
    {
        super();

        this.gridMetaData = Objects.requireNonNull(gridMetaData, "gridMetaData required");
    }

    /**
     * @param column {@link AbstractGridColumn}
     */
    public void addColumn(final GridColumn<?> column)
    {
        getGridMetaData().addColumn(column);
    }

    /**
     * @param row Object[]
     */
    public void addRow(final Object[] row)
    {
        getRows().add(row);
    }

    /**
     * Löscht alle Datensätze.
     */
    public void clearRows()
    {
        getRows().clear();
    }

    /**
     * @return int
     */
    public int columnCount()
    {
        return getGridMetaData().columnCount();
    }

    /**
     * @param index int
     * @return {@link GridColumn}
     */
    public GridColumn<?> getColumn(final int index)
    {
        return getGridMetaData().getColumn(index);
    }

    /**
     * @param index int
     * @return String
     */
    public String getComment(final int index)
    {
        return getColumn(index).getComment();
    }

    /**
     * @param index int
     * @return int
     */
    public int getLength(final int index)
    {
        return getColumn(index).getLength();
    }

    /**
     * @param index int
     * @return String
     */
    public String getName(final int index)
    {
        return getColumn(index).getName();
    }

    /**
     * @param index int
     * @return Class
     */
    public Class<?> getObjectClazz(final int index)
    {
        return getColumn(index).getObjectClazz();
    }

    /**
     * @param index int
     * @return int
     */
    public int getPrecision(final int index)
    {
        return getColumn(index).getPrecision();
    }

    /**
     * @return {@link GridMetaData}
     */
    protected GridMetaData getGridMetaData()
    {
        return this.gridMetaData;
    }

    /**
     * @return {@link List}
     */
    protected List<Object[]> getRows()
    {
        return this.rows;
    }

    /**
     * @param columnIndex int
     * @param rowIndex int
     * @return String
     */
    @SuppressWarnings("unchecked")
    public <T> T getValue(final int columnIndex, final int rowIndex)
    {
        Object[] row = getRows().get(rowIndex);

        return (T) getColumn(columnIndex).getValue(row[columnIndex]);
    }

    /**
     * Liest das Grid aus dem Stream.
     *
     * @param dataInput {@link DataInput}
     * @throws IOException Falls was schief geht.
     * @throws ClassNotFoundException Falls was schief geht.
     */
    public void read(final DataInput dataInput) throws IOException, ClassNotFoundException
    {
        getGridMetaData().readMetaData(dataInput);

        // Anzahl Zeilen
        int rowCount = dataInput.readInt();

        for (int i = 0; i < rowCount; i++)
        {
            Object[] row = new Object[columnCount()];

            for (int c = 0; c < columnCount(); c++)
            {
                row[c] = getColumn(c).read(dataInput);
            }

            addRow(row);
        }
    }

    /**
     * Liest das Grid aus dem {@link ResultSet}.
     *
     * @param resultSet {@link ResultSet}
     * @throws SQLException Falls was schief geht.
     */
    public void read(final ResultSet resultSet) throws SQLException
    {
        getGridMetaData().readMetaData(resultSet.getMetaData());

        while (resultSet.next())
        {
            Object[] row = new Object[columnCount()];

            for (int c = 1; c <= columnCount(); c++)
            {
                row[c - 1] = resultSet.getObject(c);
            }

            addRow(row);
        }
    }

    /**
     * @param columnIndex int
     * @return {@link AbstractGridColumn}
     */
    public GridColumn<?> removeColumn(final int columnIndex)
    {
        return getGridMetaData().removeColumn(columnIndex);
    }

    /**
     * @return int
     */
    public int rowCount()
    {
        return getRows().size();
    }

    /**
     * Schreibt das Grid in den Stream.
     *
     * @param dataOutput {@link DataOutput}
     * @throws IOException Falls was schief geht.
     */
    public void write(final DataOutput dataOutput) throws IOException
    {
        getGridMetaData().writeMetaData(dataOutput);

        // Anzahl Zeilen
        dataOutput.writeInt(rowCount());

        for (Object[] row : getRows())
        {
            for (int c = 0; c < columnCount(); c++)
            {
                getColumn(c).write(dataOutput, row[c]);
            }
        }
    }
}
