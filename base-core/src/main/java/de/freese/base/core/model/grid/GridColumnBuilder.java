// Created: 25.01.2018
package de.freese.base.core.model.grid;

import de.freese.base.core.model.grid.column.AbstractGridColumn;

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
    private final GridBuilder gridBuilder;

    /**
    *
    */
    private int length = -1;

    /**
    *
    */
    private String name = "";

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
     * @param gridBuilder {@link GridBuilder}
     */
    GridColumnBuilder(final GridBuilder gridBuilder)
    {
        super();

        this.gridBuilder = gridBuilder;
    }

    /**
     * Erzeugt eine neue Spalte, fügt diese {@link GridBuilder} hinzu und liefert den {@link GridBuilder} zurück.
     *
     * @return {@link GridBuilder}
     */
    public GridBuilder and()
    {
        //@formatter:off
        AbstractGridColumn<?> gc = GridMetaData.getColumnForType(this.objectClazz)
                .comment(this.comment)
                .length(this.length)
                .name(this.name)
                .precision(this.precision);
        //@formatter:off

        return this.gridBuilder.column(gc);
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
