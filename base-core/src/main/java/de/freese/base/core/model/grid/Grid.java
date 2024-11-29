// Created: 25.01.2018
package de.freese.base.core.model.grid;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Freese
 */
@SuppressWarnings("java:S1452")
public class Grid {

    private final List<GridColumn<?>> columns = new ArrayList<>();
    private final List<GridRow> rows = new ArrayList<>();

    public void addColumn(final GridColumn<?> column) {
        getColumns().add(column);
    }

    public void addRow(final GridRow row) {
        getRows().add(row);
    }

    public void clearRows() {
        getRows().clear();
    }

    public GridColumn<?> getColumn(final int columnIndex) {
        if (columnIndex >= getColumnCount()) {
            return null;
        }

        return getColumns().get(columnIndex);
    }

    public int getColumnCount() {
        return getColumns().size();
    }

    public List<GridColumn<?>> getCopyOfColumns() {
        return new ArrayList<>(getColumns());
    }

    public GridRow getRow(final int rowIndex) {
        if (rowIndex >= getRowCount()) {
            return null;
        }

        return getRows().get(rowIndex);
    }

    public int getRowCount() {
        return getRows().size();
    }

    public <T> T getValue(final Class<T> type, final int rowIndex, final int columnIndex) {
        final GridRow gridRow = getRow(rowIndex);
        final GridColumn<?> gridColumn = getColumn(columnIndex);

        if (gridRow == null || gridColumn == null) {
            return null;
        }

        final Object value = gridColumn.getValue(gridRow.getObject(columnIndex));

        return type.cast(value);
    }

    public GridColumn<?> removeColumn(final int columnIndex) {
        if (columnIndex >= getColumnCount()) {
            return null;
        }

        return getColumns().remove(columnIndex);
    }

    public GridRow removeRow(final int rowIndex) {
        if (rowIndex >= getRowCount()) {
            return null;
        }

        return getRows().remove(rowIndex);
    }

    protected List<GridColumn<?>> getColumns() {
        return this.columns;
    }

    protected List<GridRow> getRows() {
        return this.rows;
    }
}
