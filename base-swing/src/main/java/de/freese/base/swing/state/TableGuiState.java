package de.freese.base.swing.state;

import java.awt.Component;
import java.awt.Rectangle;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.TableColumn;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.table.TableColumnExt;

/**
 * @author Thomas Freese
 */
@XmlRootElement(name = "TableGuiState")
@XmlAccessorType(XmlAccessType.FIELD)
public class TableGuiState extends AbstractGuiState
{
    @Serial
    private static final long serialVersionUID = -8164953430592111778L;

    @XmlRootElement(name = "ColumnState")
    public static class ColumnState implements Serializable
    {
        @Serial
        private static final long serialVersionUID = -4666054569112117571L;

        private boolean editable = true;

        private int maxWidth = 30;

        private int minWidth = 30;

        private int modelIndex;

        private int preferredWidth = 50;

        private boolean resizeable = true;

        private boolean sortable = true;

        private boolean visible = true;

        private int width = 50;

        public ColumnState()
        {
            super();
        }

        public ColumnState(final TableColumn tableColumn)
        {
            super();

            this.minWidth = tableColumn.getMinWidth();
            this.maxWidth = tableColumn.getMaxWidth();
            this.preferredWidth = tableColumn.getPreferredWidth();
            this.width = tableColumn.getWidth();
            this.resizeable = tableColumn.getResizable();
            this.modelIndex = tableColumn.getModelIndex();

            if (tableColumn instanceof TableColumnExt tableColumnExt)
            {
                this.visible = tableColumnExt.isVisible();
                this.editable = tableColumnExt.isEditable();
                this.sortable = tableColumnExt.isSortable();
            }
        }

        public void update(final TableColumn tableColumn)
        {
            if (this.width != -1)
            {
                tableColumn.setWidth(this.width);
            }

            if (this.minWidth != -1)
            {
                tableColumn.setMinWidth(this.minWidth);
            }

            if (this.maxWidth != -1)
            {
                tableColumn.setMaxWidth(this.maxWidth);
            }

            if (this.preferredWidth != -1)
            {
                tableColumn.setPreferredWidth(this.preferredWidth);
            }

            tableColumn.setResizable(this.resizeable);

            if (tableColumn instanceof TableColumnExt tableColumnExt)
            {
                tableColumnExt.setVisible(this.visible);
                tableColumnExt.setEditable(this.editable);
                tableColumnExt.setSortable(this.sortable);
            }
        }
    }

    private ColumnState[] columnStates;

    private int[] selectedRows;

    public TableGuiState()
    {
        super(JTable.class);
    }

    /**
     * @see de.freese.base.swing.state.AbstractGuiState#restore(java.awt.Component)
     */
    @Override
    public void restore(final Component component)
    {
        super.restore(component);

        JTable table = (JTable) component;

        if ((this.selectedRows != null) && (this.selectedRows.length > 0))
        {
            // With SINGLE_SELECTION must setColumnSelectionAllowed(false) be called to do this working.
            try
            {
                for (int row : this.selectedRows)
                {
                    table.addRowSelectionInterval(row, row);
                }

                Rectangle rectangle = table.getCellRect(this.selectedRows[0], 0, true);
                table.scrollRectToVisible(rectangle);
            }
            catch (Exception ex)
            {
                // Ignore
            }
        }

        if ((this.columnStates != null) && (this.columnStates.length > 0))
        {
            List<TableColumn> columns = getColumns(table);

            for (int i = 0; i < columns.size(); i++)
            {
                int index = findModelIndex(i);

                if (index < 0)
                {
                    continue;
                }

                TableColumn tableColumn = null;

                if (index >= 0)
                {
                    tableColumn = columns.get(index);
                }

                ColumnState columnState = this.columnStates[index];

                columnState.update(tableColumn);
            }
        }
    }

    /**
     * @see de.freese.base.swing.state.AbstractGuiState#store(java.awt.Component)
     */
    @Override
    public void store(final Component component)
    {
        super.store(component);

        JTable table = (JTable) component;

        this.selectedRows = table.getSelectedRows();

        List<TableColumn> columns = getColumns(table);
        this.columnStates = new ColumnState[columns.size()];

        for (int i = 0; i < columns.size(); i++)
        {
            TableColumn column = columns.get(i);
            this.columnStates[i] = new ColumnState(column);
        }
    }

    private int findModelIndex(final int index)
    {
        for (int j = 0; j < this.columnStates.length; j++)
        {
            if (this.columnStates[j].modelIndex == index)
            {
                return j;
            }
        }

        return -1;
    }

    private List<TableColumn> getColumns(final JTable table)
    {
        List<TableColumn> columns = new ArrayList<>();

        if (table instanceof JXTable jxTable)
        {
            columns.addAll(jxTable.getColumns(true));
        }
        else
        {
            for (int i = 0; i < table.getColumnCount(); i++)
            {
                columns.add(table.getColumnModel().getColumn(i));
            }
        }

        return columns;
    }
}
