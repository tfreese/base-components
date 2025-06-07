// Created: 25.01.2018
package de.freese.base.core.model.grid.column;

import java.util.Objects;

import de.freese.base.core.model.grid.GridColumn;

/**
 * @author Thomas Freese
 */
public abstract class AbstractGridColumn<T> implements GridColumn<T> {
    private final String comment;
    private final int length;
    private final String name;
    private final int precision;
    private final Class<T> type;

    // protected AbstractGridColumn() {
    //     super();
    //
    //     // This works only, if the Super-Class is not generic too !
    //     // public class IntegerGridColumn extends AbstractGridColumn<Integer>
    //     final ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
    //
    //     this.type = (Class<T>) parameterizedType.getActualTypeArguments()[0];
    // }

    protected AbstractGridColumn(final Class<T> type, final String name, final int length, final int precision, final String comment) {
        super();

        this.type = Objects.requireNonNull(type, "type required");
        this.name = Objects.requireNonNull(name, "name required");
        this.length = length;
        this.precision = precision;
        this.comment = comment;
    }

    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getPrecision() {
        return precision;
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("GridColumn [");
        builder.append("name=").append(name);
        builder.append(", type=").append(type.getSimpleName());
        builder.append(", comment=").append(comment);
        builder.append(", length=").append(length);
        builder.append(", precision=").append(precision);
        builder.append("]");

        return builder.toString();
    }
}
