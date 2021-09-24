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
public class GridBuilder
{
    /**
     * @return {@link GridBuilder}
     */
    public static GridBuilder create()
    {
        return new GridBuilder(new DefaultGridColumnFactory());
    }

    /**
     * @param gridColumnFactory {@link GridColumnFactory}
     *
     * @return {@link GridBuilder}
     */
    public static GridBuilder create(final GridColumnFactory gridColumnFactory)
    {
        return new GridBuilder(gridColumnFactory);
    }

    /**
     *
     */
    private final GridMetaData gridMetaData;

    /**
     * Erzeugt eine neue Instanz von {@link GridBuilder}.
     *
     * @param gridColumnFactory {@link GridColumnFactory}
     */
    GridBuilder(final GridColumnFactory gridColumnFactory)
    {
        this(new GridMetaData(Objects.requireNonNull(gridColumnFactory, "gridColumnFactory required")));
    }

    /**
     * Erzeugt eine neue Instanz von {@link GridBuilder}.
     *
     * @param gridMetaData {@link GridColumnFactory}
     */
    GridBuilder(final GridMetaData gridMetaData)
    {
        super();

        this.gridMetaData = Objects.requireNonNull(gridMetaData, "gridMetaData required");
    }

    /**
     * @return {@link Grid}
     */
    public Grid build()
    {
        return new Grid(this.gridMetaData);
    }

    /**
     * @param objectClazz {@link Class}
     *
     * @return {@link GridColumnBuilder}
     */
    public <T> GridColumnBuilder column(final Class<T> objectClazz)
    {
        GridColumnBuilder gcb = new GridColumnBuilder(this.gridMetaData);
        gcb.objectClazz(objectClazz);

        return gcb;
    }

    /**
     * @param column {@link GridColumn}
     *
     * @return {@link GridBuilder}
     */
    public <T> GridBuilder column(final GridColumn<T> column)
    {
        this.gridMetaData.addColumn(column);

        return this;
    }
}
