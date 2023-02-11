// Created: 25.01.2018
package de.freese.base.core.model.grid.builder;

import java.util.Objects;

import de.freese.base.core.model.grid.Grid;
import de.freese.base.core.model.grid.GridMetaData;
import de.freese.base.core.model.grid.column.GridColumn;
import de.freese.base.core.model.grid.factory.DefaultGridColumnFactory;
import de.freese.base.core.model.grid.factory.GridColumnFactory;

/**
 * @author Thomas Freese
 */
public class GridBuilder {
    public static GridBuilder create() {
        return new GridBuilder(new DefaultGridColumnFactory());
    }

    public static GridBuilder create(final GridColumnFactory gridColumnFactory) {
        return new GridBuilder(gridColumnFactory);
    }

    private final GridMetaData gridMetaData;

    GridBuilder(final GridColumnFactory gridColumnFactory) {
        this(new GridMetaData(Objects.requireNonNull(gridColumnFactory, "gridColumnFactory required")));
    }

    GridBuilder(final GridMetaData gridMetaData) {
        super();

        this.gridMetaData = Objects.requireNonNull(gridMetaData, "gridMetaData required");
    }

    public Grid build() {
        return new Grid(this.gridMetaData);
    }

    public <T> GridColumnBuilder column(final Class<T> type) {
        GridColumnBuilder gcb = new GridColumnBuilder(this.gridMetaData);
        gcb.type(type);

        return gcb;
    }

    public <T> GridBuilder column(final GridColumn<T> column) {
        this.gridMetaData.addColumn(column);

        return this;
    }
}
