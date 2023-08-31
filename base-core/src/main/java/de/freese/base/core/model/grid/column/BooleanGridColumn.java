// Created: 25.01.2018
package de.freese.base.core.model.grid.column;

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

    @Override
    public Boolean getValue(final Object object) {
        if (object == null) {
            return null;
        }

        return (Boolean) object;
    }
}
