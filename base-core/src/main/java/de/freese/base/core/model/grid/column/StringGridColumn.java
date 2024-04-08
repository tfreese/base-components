// Created: 25.01.2018
package de.freese.base.core.model.grid.column;

/**
 * @author Thomas Freese
 */
public class StringGridColumn extends AbstractGridColumn<String> {

    public StringGridColumn() {
        this("string");
    }

    public StringGridColumn(final String name) {
        this(name, null);
    }

    public StringGridColumn(final String name, final String comment) {
        super(String.class, name, -1, -1, comment);
    }

    @Override
    public String getValue(final Object object) {
        if (object == null) {
            return null;
        }

        return (String) object;
    }
}
