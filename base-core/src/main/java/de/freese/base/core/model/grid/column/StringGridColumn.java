// Created: 25.01.2018
package de.freese.base.core.model.grid.column;

/**
 * @author Thomas Freese
 */
public class StringGridColumn extends AbstractGridColumn<String> {

    public StringGridColumn() {
        super(String.class, "string", -1, -1, null);
    }

    public StringGridColumn(final String name) {
        super(String.class, name, -1, -1, null);
    }

    public StringGridColumn(final String name, final String comment) {
        super(String.class, name, -1, -1, comment);
    }

    /**
     * @see de.freese.base.core.model.grid.column.GridColumn#getValue(java.lang.Object)
     */
    @Override
    public String getValue(final Object object) {
        if (object == null) {
            return null;
        }

        return (String) object;
    }
}
