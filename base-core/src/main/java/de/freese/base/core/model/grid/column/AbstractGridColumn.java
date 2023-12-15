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

    //    protected AbstractGridColumn()
    //    {
    //        super();
    //
    //        // This works only, if the Super-Class is not generic too !
    //        // public class IntegerGridColumn extends AbstractGridColumn<Integer>
    //        final ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
    //
    //        this.type = (Class<T>) parameterizedType.getActualTypeArguments()[0];
    //    }

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
        return this.comment;
    }

    @Override
    public int getLength() {
        return this.length;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getPrecision() {
        return this.precision;
    }

    @Override
    public Class<T> getType() {
        return this.type;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("GridColumn [");
        builder.append("name=").append(this.name);
        builder.append(", type=").append(this.type.getSimpleName());
        builder.append(", comment=").append(this.comment);
        builder.append(", length=").append(this.length);
        builder.append(", precision=").append(this.precision);
        builder.append("]");

        return builder.toString();
    }
}
