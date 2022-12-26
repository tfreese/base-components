package de.freese.base.swing.components.table;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

/**
 * {@link TableModel} which is using a {@link List} to store the Objects.
 *
 * @author Thomas Freese
 */
public abstract class AbstractListTableModel<T> extends AbstractTableModel
{
    @Serial
    private static final long serialVersionUID = 8219964863357772409L;

    private final int columnCount;

    private final transient List<String> columnNames;

    private final transient List<T> list;

    protected AbstractListTableModel(final int columnCount)
    {
        this(columnCount, new ArrayList<>());
    }

    protected AbstractListTableModel(final int columnCount, final List<T> list)
    {
        super();

        if (columnCount < 0)
        {
            throw new IllegalArgumentException("column count < 0: " + columnCount);
        }

        this.columnNames = null;
        this.columnCount = columnCount;
        this.list = Objects.requireNonNull(list, "list required");
    }

    protected AbstractListTableModel(final List<String> columnNames)
    {
        this(columnNames, new ArrayList<>());
    }

    protected AbstractListTableModel(final List<String> columnNames, final List<T> list)
    {
        super();

        this.columnNames = Objects.requireNonNull(columnNames, "columnNames required");
        this.columnCount = this.columnNames.size();

        this.list = Objects.requireNonNull(list, "list required");
    }

    public void add(final T object)
    {
        getList().add(object);

        fireTableRowsInserted(getList().size() - 1, getList().size() - 1);
    }

    public void addAll(final Collection<T> objects)
    {
        int sizeOld = getList().size();

        getList().addAll(objects);

        fireTableRowsInserted(sizeOld, getList().size() - 1);
    }

    public void clear()
    {
        getList().clear();

        refresh();
    }

    /**
     * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
     */
    @Override
    public Class<?> getColumnClass(final int columnIndex)
    {
        if (getRowCount() != 0)
        {
            for (int row = 0; row < getRowCount(); row++)
            {
                Object object = getValueAt(row, columnIndex);

                if (object != null)
                {
                    return object.getClass();
                }
            }
        }

        return super.getColumnClass(columnIndex);
    }

    /**
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    @Override
    public int getColumnCount()
    {
        return this.columnCount;
    }

    /**
     * @see javax.swing.table.AbstractTableModel#getColumnName(int)
     */
    @Override
    public String getColumnName(final int column)
    {
        if ((getColumnNames() == null) || getColumnNames().isEmpty())
        {
            return super.getColumnName(column);
        }

        return getColumnNames().get(column);
    }

    public T getObjectAt(final int rowIndex)
    {
        return getList().get(rowIndex);
    }

    /**
     * @see javax.swing.table.TableModel#getRowCount()
     */
    @Override
    public int getRowCount()
    {
        return getList().size();
    }

    public int getRowOf(final T object)
    {
        return getList().indexOf(object);
    }

    public Stream<T> getStream()
    {
        return getList().stream();
    }

    /**
     * Fires the TableDataChanged Event.
     */
    public void refresh()
    {
        fireTableDataChanged();
    }

    public void remove(final T object)
    {
        int row = getRowOf(object);

        getList().remove(object);

        if (row >= 0)
        {
            fireTableRowsDeleted(row, row);
        }
    }

    public T removeAt(final int rowIndex)
    {
        if (rowIndex < 0)
        {
            return null;
        }

        T object = getList().remove(rowIndex);

        fireTableRowsDeleted(rowIndex, rowIndex);

        return object;
    }

    protected List<String> getColumnNames()
    {
        return this.columnNames;
    }

    protected List<T> getList()
    {
        return this.list;
    }
}
