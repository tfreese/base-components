// Created: 25.01.2018
package de.freese.base.core.model.grid.builder;

import java.util.Objects;

import de.freese.base.core.model.grid.Grid;
import de.freese.base.core.model.grid.GridMetaData;
import de.freese.base.core.model.grid.column.GridColumn;

/**
 * @author Thomas Freese
 */
public class GridColumnBuilder {
    private final GridMetaData gridMetaData;

    private String comment;

    private int length = -1;

    private String name;

    private int precision = -1;
    
    private Class<?> type;

    GridColumnBuilder(final GridMetaData gridMetaData) {
        super();

        this.gridMetaData = Objects.requireNonNull(gridMetaData, "gridMetaData required");
    }

    public GridBuilder and() {
        GridColumn<?> gc = this.gridMetaData.getGridColumnFactory().getColumnForType(this.type);
        gc.setComment(this.comment);
        gc.setLength(this.length);
        gc.setName(this.name);
        gc.setPrecision(this.precision);

        this.gridMetaData.addColumn(gc);

        return new GridBuilder(this.gridMetaData);
    }

    public Grid build() {
        return and().build();
    }

    public GridColumnBuilder comment(final String comment) {
        this.comment = comment;

        return this;
    }

    public GridColumnBuilder length(final int length) {
        this.length = length;

        return this;
    }

    public GridColumnBuilder name(final String name) {
        this.name = name;

        return this;
    }

    public GridColumnBuilder precision(final int precision) {
        this.precision = precision;

        return this;
    }

    public GridColumnBuilder type(final Class<?> type) {
        this.type = type;

        return this;
    }
}
