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
    public void addColumn(final AbstractGridColumn<?> column)
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
     * @param columnIndex int
     * @return String
     */
    public String getColumnComment(final int columnIndex)
    {
        return getGridMetaData().getColumnComment(columnIndex);
    }

    /**
     * @param columnIndex int
     * @return int
     */
    public int getColumnLength(final int columnIndex)
    {
        return getGridMetaData().getColumnLength(columnIndex);
    }

    /**
     * @param columnIndex int
     * @return String
     */
    public String getColumnName(final int columnIndex)
    {
        return getGridMetaData().getColumnName(columnIndex);
    }

    /**
     * @param columnIndex int
     * @return Class
     */
    public Class<?> getColumnObjectClazz(final int columnIndex)
    {
        return getGridMetaData().getColumnObjectClazz(columnIndex);
    }

    /**
     * @param columnIndex int
     * @return int
     */
    public int getColumnPrecision(final int columnIndex)
    {
        return getGridMetaData().getColumnPrecision(columnIndex);
    }

    /**
     * @param columnIndex int
     * @param rowIndex int
     * @return String
     */
    public <T> T getValue(final int columnIndex, final int rowIndex)
    {
        Object[] row = getRows().get(rowIndex);

        return getGridMetaData().getValue(columnIndex, row[columnIndex]);
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
        getGridMetaData().read(dataInput);

        // Anzahl Zeilen
        int rowCount = dataInput.readInt();

        for (int i = 0; i < rowCount; i++)
        {
            Object[] row = getGridMetaData().readRow(dataInput);

            getRows().add(row);
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
        getGridMetaData().read(resultSet.getMetaData());

        while (resultSet.next())
        {
            Object[] row = getGridMetaData().readRow(resultSet);

            getRows().add(row);
        }
    }

    /**
     * @param columnIndex int
     * @return {@link AbstractGridColumn}
     */
    public AbstractGridColumn<?> removeColumn(final int columnIndex)
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
        getGridMetaData().write(dataOutput);

        // Anzahl Zeilen
        dataOutput.writeInt(rowCount());

        for (Object[] row : getRows())
        {
            getGridMetaData().writeRow(dataOutput, row);
        }
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
}
