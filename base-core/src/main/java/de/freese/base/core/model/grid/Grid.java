// Created: 25.01.2018
package de.freese.base.core.model.grid;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.freese.base.core.model.grid.column.GridColumn;

/**
 * Dynamische Tabelle-Struktur.
 *
 * @author Thomas Freese
 */
public class Grid
{
    private final GridMetaData gridMetaData;

    private final List<Object[]> rows = new ArrayList<>();

    public Grid()
    {
        this(new GridMetaData());
    }

    public Grid(final GridMetaData gridMetaData)
    {
        super();

        this.gridMetaData = Objects.requireNonNull(gridMetaData, "gridMetaData required");
    }

    public void addColumn(final GridColumn<?> column)
    {
        getGridMetaData().addColumn(column);
    }

    public void addRow(final Object[] row)
    {
        getRows().add(row);
    }

    public void clearRows()
    {
        getRows().clear();
    }

    public int columnCount()
    {
        return getGridMetaData().columnCount();
    }

    public GridColumn<?> getColumn(final int index)
    {
        return getGridMetaData().getColumn(index);
    }

    public String getComment(final int index)
    {
        return getColumn(index).getComment();
    }

    public int getLength(final int index)
    {
        return getColumn(index).getLength();
    }

    public String getName(final int index)
    {
        return getColumn(index).getName();
    }

    public Class<?> getObjectClazz(final int index)
    {
        return getColumn(index).getObjectClazz();
    }

    public int getPrecision(final int index)
    {
        return getColumn(index).getPrecision();
    }

    public <T> T getValue(final int columnIndex, final int rowIndex)
    {
        Object[] row = getRows().get(rowIndex);

        return (T) getColumn(columnIndex).getValue(row[columnIndex]);
    }

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

    public GridColumn<?> removeColumn(final int columnIndex)
    {
        return getGridMetaData().removeColumn(columnIndex);
    }

    public int rowCount()
    {
        return getRows().size();
    }

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

    protected GridMetaData getGridMetaData()
    {
        return this.gridMetaData;
    }

    protected List<Object[]> getRows()
    {
        return this.rows;
    }
}
