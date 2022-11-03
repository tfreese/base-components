// Created: 18.03.2020
package de.freese.base.core.model.grid.factory;

import java.sql.Types;
import java.util.Date;

import de.freese.base.core.model.grid.column.GridColumn;

/**
 * @author Thomas Freese
 */
public interface GridColumnFactory
{
    default GridColumn<?> getColumnForSQL(final int sqlType)
    {
        Class<?> objectClazz = switch (sqlType)
                {
                    case Types.BINARY, Types.VARBINARY -> byte[].class;
                    case Types.BOOLEAN -> Boolean.class;
                    case Types.DATE -> Date.class;
                    case Types.FLOAT, Types.DOUBLE -> Double.class;
                    case Types.TINYINT, Types.SMALLINT, Types.INTEGER -> Integer.class;
                    case Types.BIGINT, Types.DECIMAL, Types.NUMERIC -> Long.class;
                    case Types.VARCHAR -> String.class;

                    default -> throw new UnsupportedOperationException("sqlType is not supported: " + sqlType);
                };

        return getColumnForType(objectClazz);
    }

    GridColumn<?> getColumnForType(final Class<?> objectClazz);
}
