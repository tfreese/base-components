// Created: 25.01.2018
package de.freese.base.core.model.grid.column;

import de.freese.base.core.model.grid.GridColumn;

/**
 * @author Thomas Freese
 */
public class BooleanGridColumn extends AbstractGridColumn<Boolean> {

    public BooleanGridColumn() {
        this("boolean");
    }

    public BooleanGridColumn(final String name) {
        super(Boolean.class, name, -1, -1, null);
    }

    /**
     * @see GridColumn#getValue(java.lang.Object)
     */
    @Override
    public Boolean getValue(final Object object) {
        if (object == null) {
            return null;
        }

        return (Boolean) object;
    }
}
