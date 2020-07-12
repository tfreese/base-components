// Created: 12.01.2018
package de.freese.base.swing.components.table;

import java.util.List;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * TableModel das Intern eine {@link ObservableList} verwendet.
 *
 * @author Thomas Freese
 * @param <T> Typ der Entity
 */
public abstract class AbstractObservableListTableModel<T> extends AbstractListTableModel<T> implements ListChangeListener<T>
{
    /**
     *
     */
    private static final long serialVersionUID = -5542628813153019029L;

    /**
     * Erzeugt eine neue Instanz von {@link AbstractObservableListTableModel}.
     *
     * @param columnCount int
     * @param list {@link ObservableList}
     */
    public AbstractObservableListTableModel(final int columnCount, final ObservableList<T> list)
    {
        super(columnCount, list);

        list.addListener(this);
    }

    /**
     * Erstellt ein neues {@link AbstractObservableListTableModel} Objekt.
     *
     * @param columnNames {@link List}
     * @param list {@link ObservableList}
     */
    public AbstractObservableListTableModel(final List<String> columnNames, final ObservableList<T> list)
    {
        super(columnNames, list);

        list.addListener(this);
    }

    /**
     * @see de.freese.base.swing.components.table.AbstractListTableModel#getList()
     */
    @Override
    protected ObservableList<T> getList()
    {
        return (ObservableList<T>) super.getList();
    }

    /**
     * @see javafx.collections.ListChangeListener#onChanged(javafx.collections.ListChangeListener.Change)
     */
    @Override
    public void onChanged(final Change<? extends T> change)
    {
        while (change.next())
        {
            if (change.wasAdded())
            {
                int firstRow = change.getFrom();
                int lastRow = change.getTo();

                fireTableRowsInserted(firstRow, lastRow);
            }
            else if (change.wasRemoved())
            {
                int firstRow = change.getFrom();
                int lastRow = change.getTo();

                fireTableRowsDeleted(firstRow, lastRow);
            }
            else if (change.wasUpdated())
            {
                int firstRow = change.getFrom();
                int lastRow = change.getTo();

                fireTableRowsUpdated(firstRow, lastRow);
            }
            else
            {
                fireTableDataChanged();

                // 1x reicht
                return;
            }
        }
    }
}
