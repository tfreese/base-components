// Created: 25.01.2018
package de.freese.base.core.model.grid;

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

    public <T> T getValue(final Class<T> type, final int rowIndex, final int columnIndex) {
        Object[] row = getRows().get(rowIndex);
        Object value = getColumn(columnIndex).getValue(row[columnIndex]);

        return type.cast(value);
    }

    public GridColumn<?> removeColumn(final int columnIndex) {
        return getGridMetaData().removeColumn(columnIndex);
    }

    protected GridMetaData getGridMetaData() {
        return this.gridMetaData;
    }

    protected List<Object[]> getRows() {
        return this.rows;
    }
}
