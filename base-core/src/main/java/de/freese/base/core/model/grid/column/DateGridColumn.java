// Created: 25.01.2018
package de.freese.base.core.model.grid.column;

import java.util.Date;

/**
 * @author Thomas Freese
 */
public class DateGridColumn extends AbstractGridColumn<Date> {

    public DateGridColumn() {
        this("date");
    }

    public DateGridColumn(final String name) {
        this(name, null);
    }

    public DateGridColumn(final String name, final String comment) {
        super(Date.class, name, -1, -1, comment);
    }

    @Override
    public Date getValue(final Object object) {
        if (object == null) {
            return null;
        }

        return (Date) object;
    }
}
