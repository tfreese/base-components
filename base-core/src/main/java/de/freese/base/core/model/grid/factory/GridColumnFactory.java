/**
 * Created: 18.03.2020
 */

package de.freese.base.core.model.grid.factory;

import java.sql.Types;
import java.util.Date;
import de.freese.base.core.model.grid.column.GridColumn;

/**
 * @author Thomas Freese
 */
public interface GridColumnFactory
{
    /**
     * Liefert für den SQL-Type die entsprechende Column.
     *
     * @param sqlType int
     * @return {@link GridColumn}
     */
    public default GridColumn<?> getColumnForSQL(final int sqlType)
    {
        Class<?> objectClazz = null;

        switch (sqlType)
        {
            case Types.BINARY:
            case Types.VARBINARY:
                objectClazz = byte[].class;
                break;

            case Types.BOOLEAN:
                objectClazz = Boolean.class;
                break;

            case Types.DATE:
                objectClazz = Date.class;
                break;

            case Types.FLOAT:
            case Types.DOUBLE:
                objectClazz = Double.class;
                break;

            case Types.TINYINT:
            case Types.SMALLINT:
            case Types.INTEGER:
                objectClazz = Integer.class;
                break;

            case Types.BIGINT:
            case Types.DECIMAL:
            case Types.NUMERIC:
                objectClazz = Long.class;
                break;

            case Types.VARCHAR:
                objectClazz = String.class;
                break;

            default:
                throw new UnsupportedOperationException("sqlType is not supported: " + sqlType);
        }

        GridColumn<?> gridColumn = getColumnForType(objectClazz);

        return gridColumn;
    }

    /**
     * Liefert für den Object-Typ die entsprechende Column.
     *
     * @param objectClazz Class
     * @return {@link GridColumn}
     */
    public GridColumn<?> getColumnForType(final Class<?> objectClazz);
}
