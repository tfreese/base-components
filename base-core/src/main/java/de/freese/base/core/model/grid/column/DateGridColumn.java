// Created: 25.01.2018
package de.freese.base.core.model.grid.column;

import java.util.Date;

/**
 * @author Thomas Freese
 */
public class DateGridColumn extends AbstractGridColumn<Date> {

    public DateGridColumn() {
        super(Date.class, "date", -1, -1, null);
    }

    public DateGridColumn(final String name) {
        super(Date.class, name, -1, -1, null);
    }

    public DateGridColumn(final String name, final String comment) {
        super(Date.class, name, -1, -1, comment);
    }

    /**
     * @see de.freese.base.core.model.grid.column.GridColumn#getValue(java.lang.Object)
     */
    @Override
    public Date getValue(final Object object) {
        if (object == null) {
            return null;
        }

        return (Date) object;
    }
}
