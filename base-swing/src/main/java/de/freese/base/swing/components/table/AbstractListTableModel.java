package de.freese.base.swing.components.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import javax.swing.table.AbstractTableModel;

/**
 * TableModel das intern eine Liste verwendet.
 *
 * @author Thomas Freese
 *
 * @param <T> Konkreter Typ der List-Objekte.
 */
public abstract class AbstractListTableModel<T> extends AbstractTableModel
{
    /**
     *
     */
    private static final long serialVersionUID = 8219964863357772409L;
    /**
     *
     */
    private final int columnCount;
    /**
     *
     */
    private final List<String> columnNames;
    /**
     *
     */
    private final List<T> list;

    /**
     * Erstellt ein neues {@link AbstractListTableModel} Objekt.
     *
     * @param columnCount int
     */
    protected AbstractListTableModel(final int columnCount)
    {
        this(columnCount, new ArrayList<>());
    }

    /**
     * Erstellt ein neues {@link AbstractListTableModel} Objekt.
     *
     * @param columnCount int
     * @param list {@link List}
     */
    protected AbstractListTableModel(final int columnCount, final List<T> list)
    {
        super();

        if (columnCount < 0)
        {
            throw new IllegalArgumentException("columncount < 0: " + columnCount);
        }

        this.columnNames = null;
        this.columnCount = columnCount;
        this.list = Objects.requireNonNull(list, "list required");
    }

    /**
     * Erstellt ein neues {@link AbstractListTableModel} Objekt.
     *
     * @param columnNames List
     */
    protected AbstractListTableModel(final List<String> columnNames)
    {
        this(columnNames, new ArrayList<>());
    }

    /**
     * Erstellt ein neues {@link AbstractListTableModel} Objekt.
     *
     * @param columnNames {@link List}
     * @param list {@link List}
     */
    protected AbstractListTableModel(final List<String> columnNames, final List<T> list)
    {
        super();

        this.columnNames = Objects.requireNonNull(columnNames, "columnNames required");
        this.columnCount = this.columnNames.size();

        this.list = Objects.requireNonNull(list, "list required");
    }

    /**
     * @param object Object
     */
    public void add(final T object)
    {
        getList().add(object);

        fireTableRowsInserted(getList().size() - 1, getList().size() - 1);
    }

    /**
     * @param objects {@link Collection}
     */
    public void addAll(final Collection<T> objects)
    {
        int sizeOld = getList().size();

        getList().addAll(objects);

        fireTableRowsInserted(sizeOld, getList().size() - 1);
    }

    /**
     *
     */
    public void clear()
    {
        getList().clear();

        refresh();
    }

    /**
     * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
     */
    @Override
    public Class<? extends Object> getColumnClass(final int columnIndex)
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

    /**
     * @return {@link List}<String>
     */
    protected List<String> getColumnNames()
    {
        return this.columnNames;
    }

    /**
     * Liefert die Liste des TableModels.
     *
     * @return {@link List}
     */
    protected List<T> getList()
    {
        return this.list;
    }

    /**
     * Liefert ein Objekt fuer einen Index einer Zeile.
     *
     * @param rowIndex int
     *
     * @return Object
     */
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

    /**
     * Liefert den ZeilenIndex fuer ein Objekt zurueck.
     *
     * @param object Object
     *
     * @return int
     */
    public int getRowOf(final T object)
    {
        return getList().indexOf(object);
    }

    /**
     * Liefert den {@link Stream} des TableModels.
     *
     * @return {@link Stream}
     */
    public Stream<T> getStream()
    {
        return getList().stream();
    }

    /**
     * Feuert das TableDataChanged Event.
     */
    public void refresh()
    {
        fireTableDataChanged();
    }
}
