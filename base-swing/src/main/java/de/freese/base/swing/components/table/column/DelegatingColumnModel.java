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
 * Achtung!!!: Die Methoden removeColumn(), getSelectedColumns(), getSelectedColumnCount()<br>
 * und moveColumn() müssen angepasst werden, wenn das SelectionModel richtig funktionieren soll.
 *
 * @author Thomas Freese
 */
public class DelegatingColumnModel implements TableColumnModel, ListSelectionListener
{
    /**
     *
     */
    private final JTable delegateTable;
    /**
     *
     */
    private final ListSelectionModel listSelectionModel;

    /**
     * Creates a new {@link DelegatingColumnModel} object.
     *
     * @param delegateTable {@link TableColumnModel}
     */
    public DelegatingColumnModel(final JTable delegateTable)
    {
        super();

        this.delegateTable = delegateTable;
        this.listSelectionModel = new DefaultListSelectionModel();
        this.listSelectionModel.addListSelectionListener(this);
    }

    /**
     * @see javax.swing.table.TableColumnModel#addColumn(javax.swing.table.TableColumn)
     */
    @Override
    public void addColumn(final TableColumn aColumn)
    {
        getDelegateColumnModel().addColumn(aColumn);
    }

    /**
     * @see javax.swing.table.TableColumnModel#addColumnModelListener(javax.swing.event.TableColumnModelListener)
     */
    @Override
    public void addColumnModelListener(final TableColumnModelListener x)
    {
        getDelegateColumnModel().addColumnModelListener(x);
    }

    /**
     * @see javax.swing.table.TableColumnModel#getColumn(int)
     */
    @Override
    public TableColumn getColumn(final int columnIndex)
    {
        return getDelegateColumnModel().getColumn(columnIndex);
    }

    /**
     * @see javax.swing.table.TableColumnModel#getColumnCount()
     */
    @Override
    public int getColumnCount()
    {
        return getDelegateColumnModel().getColumnCount();
    }

    /**
     * @see javax.swing.table.TableColumnModel#getColumnIndex(java.lang.Object)
     */
    @Override
    public int getColumnIndex(final Object columnIdentifier)
    {
        return getDelegateColumnModel().getColumnIndex(columnIdentifier);
    }

    /**
     * @see javax.swing.table.TableColumnModel#getColumnIndexAtX(int)
     */
    @Override
    public int getColumnIndexAtX(final int xPosition)
    {
        return getDelegateColumnModel().getColumnIndexAtX(xPosition);
    }

    /**
     * @see javax.swing.table.TableColumnModel#getColumnMargin()
     */
    @Override
    public int getColumnMargin()
    {
        return getDelegateColumnModel().getColumnMargin();
    }

    /**
     * @see javax.swing.table.TableColumnModel#getColumnSelectionAllowed()
     */
    @Override
    public boolean getColumnSelectionAllowed()
    {
        return getDelegateColumnModel().getColumnSelectionAllowed();
    }

    /**
     * @see javax.swing.table.TableColumnModel#getColumns()
     */
    @Override
    public Enumeration<TableColumn> getColumns()
    {
        return getDelegateColumnModel().getColumns();
    }

    /**
     * Liefert das original {@link TableColumnModel}.
     *
     * @return {@link TableColumnModel}
     */
    public TableColumnModel getDelegateColumnModel()
    {
        return this.delegateTable.getColumnModel();
    }

    /**
     * Liefert die original {@link JTable}.
     *
     * @return {@link JTable}
     */
    public JTable getDelegateTable()
    {
        return this.delegateTable;
    }

    /**
     * @see javax.swing.table.TableColumnModel#getSelectedColumnCount()
     */
    @Override
    public int getSelectedColumnCount()
    {
        return getDelegateColumnModel().getSelectedColumnCount();
    }

    /**
     * @see javax.swing.table.TableColumnModel#getSelectedColumns()
     */
    @Override
    public int[] getSelectedColumns()
    {
        return getDelegateColumnModel().getSelectedColumns();
    }

    /**
     * @see javax.swing.table.TableColumnModel#getSelectionModel()
     */
    @Override
    public ListSelectionModel getSelectionModel()
    {
        return this.listSelectionModel;
    }

    /**
     * @see javax.swing.table.TableColumnModel#getTotalColumnWidth()
     */
    @Override
    public int getTotalColumnWidth()
    {
        return getDelegateColumnModel().getTotalColumnWidth();
    }

    /**
     * @see javax.swing.table.TableColumnModel#moveColumn(int, int)
     */
    @Override
    public void moveColumn(final int columnIndex, final int newIndex)
    {
        getDelegateColumnModel().moveColumn(columnIndex, newIndex);
    }

    /**
     * @see javax.swing.table.TableColumnModel#removeColumn(javax.swing.table.TableColumn)
     */
    @Override
    public void removeColumn(final TableColumn column)
    {
        getDelegateColumnModel().removeColumn(column);
    }

    /**
     * @see javax.swing.table.TableColumnModel#removeColumnModelListener(javax.swing.event.TableColumnModelListener)
     */
    @Override
    public void removeColumnModelListener(final TableColumnModelListener x)
    {
        getDelegateColumnModel().removeColumnModelListener(x);
    }

    /**
     * @see javax.swing.table.TableColumnModel#setColumnMargin(int)
     */
    @Override
    public void setColumnMargin(final int newMargin)
    {
        getDelegateColumnModel().setColumnMargin(newMargin);
    }

    /**
     * @see javax.swing.table.TableColumnModel#setColumnSelectionAllowed(boolean)
     */
    @Override
    public void setColumnSelectionAllowed(final boolean flag)
    {
        getDelegateColumnModel().setColumnSelectionAllowed(flag);
    }

    /**
     * @see javax.swing.table.TableColumnModel#setSelectionModel(javax.swing.ListSelectionModel)
     */
    @Override
    public void setSelectionModel(final ListSelectionModel newModel)
    {
        // delegate.setSelectionModel(newModel);
        throw new UnsupportedOperationException("Nicht erlaubt !");
    }

    /**
     * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
     */
    @Override
    public void valueChanged(final ListSelectionEvent e)
    {
        if (getDelegateColumnModel() instanceof ListSelectionListener l)
        {
            l.valueChanged(e);
        }
    }
}
