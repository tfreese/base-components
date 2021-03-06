/**
 * Created: 18.03.2020
 */

package de.freese.base.core.model.grid.factory;

import java.util.Date;
import de.freese.base.core.model.grid.column.BinaryGridColumn;
import de.freese.base.core.model.grid.column.BooleanGridColumn;
import de.freese.base.core.model.grid.column.DateGridColumn;
import de.freese.base.core.model.grid.column.DoubleGridColumn;
import de.freese.base.core.model.grid.column.GridColumn;
import de.freese.base.core.model.grid.column.IntegerGridColumn;
import de.freese.base.core.model.grid.column.LongGridColumn;
import de.freese.base.core.model.grid.column.StringGridColumn;

/**
 * @author Thomas Freese
 */
public class DefaultGridColumnFactory implements GridColumnFactory
{
    /**
     * @see de.freese.base.core.model.grid.factory.GridColumnFactory#getColumnForType(java.lang.Class)
     */
    @Override
    public GridColumn<?> getColumnForType(final Class<?> objectClazz)
    {
        GridColumn<?> column = null;

        if (byte[].class.equals(objectClazz))
        {
            column = new BinaryGridColumn();
        }
        else if (Boolean.class.equals(objectClazz))
        {
            column = new BooleanGridColumn();
        }
        else if (Date.class.equals(objectClazz))
        {
            column = new DateGridColumn();
        }
        else if (Double.class.equals(objectClazz))
        {
            column = new DoubleGridColumn();
        }
        else if (Integer.class.equals(objectClazz))
        {
            column = new IntegerGridColumn();
        }
        else if (Long.class.equals(objectClazz))
        {
            column = new LongGridColumn();
        }
        else if (String.class.equals(objectClazz))
        {
            column = new StringGridColumn();
        }
        else
        {
            throw new UnsupportedOperationException("objectClass is not supported: " + objectClazz.getName());
        }

        return column;
    }
}
