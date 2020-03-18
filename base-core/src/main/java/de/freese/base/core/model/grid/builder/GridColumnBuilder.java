// Created: 25.01.2018
package de.freese.base.core.model.grid.builder;

import java.util.Objects;
import de.freese.base.core.model.grid.Grid;
import de.freese.base.core.model.grid.GridMetaData;
import de.freese.base.core.model.grid.column.GridColumn;

/**
 * @author Thomas Freese
 */
public class GridColumnBuilder
{
    /**
    *
    */
    private String comment = null;

    /**
     *
     */
    private final GridMetaData gridMetaData;

    /**
    *
    */
    private int length = -1;

    /**
    *
    */
    private String name = null;

    /**
     *
     */
    private Class<?> objectClazz = null;

    /**
    *
    */
    private int precision = -1;

    /**
     * Erzeugt eine neue Instanz von {@link GridColumnBuilder}.
     *
     * @param gridMetaData {@link GridMetaData}
     */
    GridColumnBuilder(final GridMetaData gridMetaData)
    {
        super();

        this.gridMetaData = Objects.requireNonNull(gridMetaData, "gridMetaData required");
    }

    /**
     * Erzeugt eine neue Spalte, fügt diese {@link GridBuilder} hinzu und liefert den {@link GridBuilder} zurück.
     *
     * @return {@link GridBuilder}
     */
    public GridBuilder and()
    {
        GridColumn<?> gc = this.gridMetaData.getGridColumnFactory().getColumnForType(this.objectClazz);
        gc.setComment(this.comment);
        gc.setLength(this.length);
        gc.setName(this.name);
        gc.setPrecision(this.precision);

        this.gridMetaData.addColumn(gc);

        return new GridBuilder(this.gridMetaData);
    }

    /**
     * Erzeugt eine neue Spalte, fügt die {@link GridBuilder} hinzu und ruft {@link GridBuilder#build()} auf.
     *
     * @return {@link Grid}
     */
    public Grid build()
    {
        return and().build();
    }

    /**
     * @param comment String
     * @return {@link GridColumnBuilder}
     */
    public GridColumnBuilder comment(final String comment)
    {
        this.comment = comment;

        return this;
    }

    /**
     * @param length int
     * @return {@link GridColumnBuilder}
     */
    public GridColumnBuilder length(final int length)
    {
        this.length = length;

        return this;
    }

    /**
     * @param name String
     * @return {@link GridColumnBuilder}
     */
    public GridColumnBuilder name(final String name)
    {
        this.name = name;

        return this;
    }

    /**
     * @param objectClazz Class
     * @return {@link GridColumnBuilder}
     */
    public GridColumnBuilder objectClazz(final Class<?> objectClazz)
    {
        this.objectClazz = objectClazz;

        return this;
    }

    /**
     * @param precision int
     * @return {@link GridColumnBuilder}
     */
    public GridColumnBuilder precision(final int precision)
    {
        this.precision = precision;

        return this;
    }
}
