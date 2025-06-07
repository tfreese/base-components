package de.freese.base.swing.components.table.column;

import java.util.Enumeration;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * Delegator für ein {@link TableColumnModel}. Damit wird sichergestellt, dass die Spaltenbreiten dieselben sind, wie die Tabelle mit dem übergebenden
 * {@link TableColumnModel}. Zusätzlich wird verhindert, das beide ColumnModels das gleiche SelectionModel haben.<br>
 * Achtung!: Die Methoden removeColumn(), getSelectedColumns(), getSelectedColumnCount()<br>
 * und moveColumn() müssen angepasst werden, wenn das SelectionModel richtig funktionieren soll.
 *
 * @author Thomas Freese
 */
public class DelegatingColumnModel implements TableColumnModel, ListSelectionListener {
    private final JTable delegateTable;
    private final ListSelectionModel listSelectionModel;

    public DelegatingColumnModel(final JTable delegateTable) {
        super();

        this.delegateTable = delegateTable;
        this.listSelectionModel = new DefaultListSelectionModel();
        this.listSelectionModel.addListSelectionListener(this);
    }

    @Override
    public void addColumn(final TableColumn aColumn) {
        getDelegateColumnModel().addColumn(aColumn);
    }

    @Override
    public void addColumnModelListener(final TableColumnModelListener x) {
        getDelegateColumnModel().addColumnModelListener(x);
    }

    @Override
    public TableColumn getColumn(final int columnIndex) {
        return getDelegateColumnModel().getColumn(columnIndex);
    }

    @Override
    public int getColumnCount() {
        return getDelegateColumnModel().getColumnCount();
    }

    @Override
    public int getColumnIndex(final Object columnIdentifier) {
        return getDelegateColumnModel().getColumnIndex(columnIdentifier);
    }

    @Override
    public int getColumnIndexAtX(final int xPosition) {
        return getDelegateColumnModel().getColumnIndexAtX(xPosition);
    }

    @Override
    public int getColumnMargin() {
        return getDelegateColumnModel().getColumnMargin();
    }

    @Override
    public boolean getColumnSelectionAllowed() {
        return getDelegateColumnModel().getColumnSelectionAllowed();
    }

    @Override
    public Enumeration<TableColumn> getColumns() {
        return getDelegateColumnModel().getColumns();
    }

    public TableColumnModel getDelegateColumnModel() {
        return delegateTable.getColumnModel();
    }

    public JTable getDelegateTable() {
        return delegateTable;
    }

    @Override
    public int getSelectedColumnCount() {
        return getDelegateColumnModel().getSelectedColumnCount();
    }

    @Override
    public int[] getSelectedColumns() {
        return getDelegateColumnModel().getSelectedColumns();
    }

    @Override
    public ListSelectionModel getSelectionModel() {
        return listSelectionModel;
    }

    @Override
    public int getTotalColumnWidth() {
        return getDelegateColumnModel().getTotalColumnWidth();
    }

    @Override
    public void moveColumn(final int columnIndex, final int newIndex) {
        getDelegateColumnModel().moveColumn(columnIndex, newIndex);
    }

    @Override
    public void removeColumn(final TableColumn column) {
        getDelegateColumnModel().removeColumn(column);
    }

    @Override
    public void removeColumnModelListener(final TableColumnModelListener x) {
        getDelegateColumnModel().removeColumnModelListener(x);
    }

    @Override
    public void setColumnMargin(final int newMargin) {
        getDelegateColumnModel().setColumnMargin(newMargin);
    }

    @Override
    public void setColumnSelectionAllowed(final boolean flag) {
        getDelegateColumnModel().setColumnSelectionAllowed(flag);
    }

    @Override
    public void setSelectionModel(final ListSelectionModel newModel) {
        // delegate.setSelectionModel(newModel);
        throw new UnsupportedOperationException("Nicht erlaubt !");
    }

    @Override
    public void valueChanged(final ListSelectionEvent event) {
        if (getDelegateColumnModel() instanceof ListSelectionListener l) {
            l.valueChanged(event);
        }
    }
}
