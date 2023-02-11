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
 * @author Thomas Freese
 */
public class Grid {
    private final GridMetaData gridMetaData;

    private final List<Object[]> rows = new ArrayList<>();

    public Grid() {
        this(new GridMetaData());
    }

    public Grid(final GridMetaData gridMetaData) {
        super();

        this.gridMetaData = Objects.requireNonNull(gridMetaData, "gridMetaData required");
    }

    public void addColumn(final GridColumn<?> column) {
        getGridMetaData().addColumn(column);
    }

    public void addRow(final Object[] row) {
        getRows().add(row);
    }

    public void clearRows() {
        getRows().clear();
    }

    public GridColumn<?> getColumn(final int columnIndex) {
        return getGridMetaData().getColumn(columnIndex);
    }

    public int getColumnCount() {
        return getGridMetaData().getColumnCount();
    }

    public String getComment(final int columnIndex) {
        return getColumn(columnIndex).getComment();
    }

    public int getLength(final int columnIndex) {
        return getColumn(columnIndex).getLength();
    }

    public String getName(final int columnIndex) {
        return getColumn(columnIndex).getName();
    }

    public int getPrecision(final int columnIndex) {
        return getColumn(columnIndex).getPrecision();
    }

    public int getRowCount() {
        return getRows().size();
    }

    public Class<?> getType(final int columnIndex) {
        return getColumn(columnIndex).getType();
    }

    public <T> T getValue(final Class<T> type, final int columnIndex, final int rowIndex) {
        Object[] row = getRows().get(rowIndex);
        Object value = getColumn(columnIndex).getValue(row[columnIndex]);

        return type.cast(value);
    }

    public void read(final DataInput dataInput) throws IOException, ClassNotFoundException {
        getGridMetaData().readMetaData(dataInput);

        // Anzahl Zeilen
        int rowCount = dataInput.readInt();

        for (int i = 0; i < rowCount; i++) {
            Object[] row = new Object[getColumnCount()];

            for (int c = 0; c < getColumnCount(); c++) {
                row[c] = getColumn(c).read(dataInput);
            }

            addRow(row);
        }
    }

    public void read(final ResultSet resultSet) throws SQLException {
        getGridMetaData().readMetaData(resultSet.getMetaData());

        while (resultSet.next()) {
            Object[] row = new Object[getColumnCount()];

            for (int c = 1; c <= getColumnCount(); c++) {
                row[c - 1] = resultSet.getObject(c);
            }

            addRow(row);
        }
    }

    public GridColumn<?> removeColumn(final int columnIndex) {
        return getGridMetaData().removeColumn(columnIndex);
    }

    public void write(final DataOutput dataOutput) throws IOException {
        getGridMetaData().writeMetaData(dataOutput);

        // Anzahl Zeilen
        dataOutput.writeInt(getRowCount());

        for (Object[] row : getRows()) {
            for (int c = 0; c < getColumnCount(); c++) {
                getColumn(c).write(dataOutput, row[c]);
            }
        }
    }

    protected GridMetaData getGridMetaData() {
        return this.gridMetaData;
    }

    protected List<Object[]> getRows() {
        return this.rows;
    }
}
