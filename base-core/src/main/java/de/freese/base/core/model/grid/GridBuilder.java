// Created: 25.01.2018
package de.freese.base.core.model.grid;

import de.freese.base.core.model.grid.column.AbstractGridColumn;

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
        return new GridBuilder(new GridMetaData());
    }

    /**
     *
     */
    private final GridMetaData gridMetaData;

    /**
     * Erzeugt eine neue Instanz von {@link GridBuilder}.
     *
     * @param gridMetaData {@link GridMetaData}
     */
    private GridBuilder(final GridMetaData gridMetaData)
    {
        super();

        this.gridMetaData = gridMetaData;
    }

    /**
     * @return {@link Grid}
     */
    public Grid build()
    {
        return new Grid(this.gridMetaData);
    }

    /**
     * @param column {@link AbstractGridColumn}
     * @return {@link GridBuilder}
     */
    public <T> GridBuilder column(final AbstractGridColumn<T> column)
    {
        this.gridMetaData.addColumn(column);

        return this;
    }

    /**
     * @param objectClazz {@link Class}
     * @return {@link GridColumnBuilder}
     */
    public <T> GridColumnBuilder column(final Class<T> objectClazz)
    {
        GridColumnBuilder gcb = new GridColumnBuilder(this);
        gcb.objectClazz(objectClazz);

        return gcb;
    }

    // /**
    // * @return {@link GridMetaData}
    // */
    // GridMetaData getGridMetaData()
    // {
    // return this.gridMetaData;
    // }
}
