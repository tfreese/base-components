// Created: 24.01.2018
package de.freese.base.core.model.grid;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import de.freese.base.core.model.grid.column.GridColumn;

/**
 * @author Thomas Freese
 */
public class GridMetaData {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private final List<GridColumn<?>> columns = new ArrayList<>();

    public void addColumn(final GridColumn<?> column) {
        getColumns().add(column);
    }

    public GridColumn<?> getColumn(final int columnIndex) {
        return getColumns().get(columnIndex);
    }

    public int getColumnCount() {
        return getColumns().size();
    }

    public GridColumn<?> removeColumn(final int columnIndex) {
        return getColumns().remove(columnIndex);
    }

    protected List<GridColumn<?>> getColumns() {
        return this.columns;
    }
}
