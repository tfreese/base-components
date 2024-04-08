// Created: 25.01.2018
package de.freese.base.core.model.grid.column;

import java.util.Objects;
import java.util.function.Function;

/**
 * @author Thomas Freese
 */
public class GenericGridColumn<T> extends AbstractGridColumn<T> {
    private final Function<Object, T> mapper;

    public GenericGridColumn(final Class<T> type, final Function<Object, T> mapper) {
        this(type, "generic", mapper);
    }

    public GenericGridColumn(final Class<T> type, final String name, final Function<Object, T> mapper) {
        this(type, name, -1, -1, null, mapper);
    }

    public GenericGridColumn(final Class<T> type, final String name, final int length, final int precision, final String comment, final Function<Object, T> mapper) {
        super(type, name, length, precision, comment);

        this.mapper = Objects.requireNonNull(mapper, "mapper required");
    }

    @Override
    public T getValue(final Object object) {
        if (object == null) {
            return null;
        }

        return this.mapper.apply(object);
    }
}
