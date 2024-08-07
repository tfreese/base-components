// Created: 12.01.2018
package de.freese.base.swing.components.table;

import java.io.Serial;
import java.util.List;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * @author Thomas Freese
 */
public abstract class AbstractObservableListTableModel<T> extends AbstractListTableModel<T> implements ListChangeListener<T> {
    @Serial
    private static final long serialVersionUID = -5542628813153019029L;

    protected AbstractObservableListTableModel(final int columnCount, final ObservableList<T> list) {
        super(columnCount, list);

        list.addListener(this);
    }

    protected AbstractObservableListTableModel(final List<String> columnNames, final ObservableList<T> list) {
        super(columnNames, list);

        list.addListener(this);
    }

    @Override
    public void onChanged(final Change<? extends T> change) {
        while (change.next()) {
            if (change.wasAdded()) {
                final int firstRow = change.getFrom();
                final int lastRow = change.getTo();

                fireTableRowsInserted(firstRow, lastRow);
            }
            else if (change.wasRemoved()) {
                final int firstRow = change.getFrom();
                final int lastRow = change.getTo();

                fireTableRowsDeleted(firstRow, lastRow);
            }
            else if (change.wasUpdated()) {
                final int firstRow = change.getFrom();
                final int lastRow = change.getTo();

                fireTableRowsUpdated(firstRow, lastRow);
            }
            else {
                fireTableDataChanged();

                return;
            }
        }
    }

    @Override
    protected ObservableList<T> getList() {
        return (ObservableList<T>) super.getList();
    }
}
