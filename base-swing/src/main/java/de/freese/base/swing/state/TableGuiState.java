package de.freese.base.swing.state;

import java.awt.Component;
import java.awt.Rectangle;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.table.TableColumnExt;

/**
 * State einer Tabelle.
 *
 * @author Thomas Freese
 */
@XmlRootElement(name = "TableGuiState")
@XmlAccessorType(XmlAccessType.FIELD)
public class TableGuiState extends AbstractGuiState
{
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -8164953430592111778L;

    /**
     * Statusklasse einer TableColumn.
     *
     * @author Thomas Freese
     */
    @XmlRootElement(name = "ColumnState")
    public static class ColumnState implements Serializable
    {
        /**
         *
         */
        @Serial
        private static final long serialVersionUID = -4666054569112117571L;
        /**
         *
         */
        private boolean editable = true;
        /**
         *
         */
        private int maxWidth = 30;
        /**
         *
         */
        private int minWidth = 30;
        /**
         *
         */
        private int modelIndex;
        /**
         *
         */
        private int preferredWidth = 50;
        /**
         *
         */
        private boolean resizeable = true;
        /**
         *
         */
        private boolean sortable = true;
        /**
         *
         */
        private boolean visible = true;
        /**
         *
         */
        private int width = 50;

        /**
         * Erstellt ein neues {@link ColumnState} Object.
         */
        public ColumnState()
        {
            super();
        }

        /**
         * Creates a new {@link ColumnState} object.
         *
         * @param tableColumn {@link TableColumn}
         */
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

        /**
         * Konfiguriert die Spalte.
         *
         * @param tableColumn {@link TableColumn}
         */
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

                // System.out.println(this.modelIndex + " - " + this.visible);
            }
        }
    }
    /**
     *
     */
    private ColumnState[] columnStates;
    /**
     *
     */
    private int[] selectedRows;

    /**
     * Creates a new {@link TableGuiState} object.
     */
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
            // Bei SINGLE_SELECTION muss setColumnSelectionAllowed(false) gesetzt werden
            // damit das hier funktioniert.
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

                // if (index == -1)
                // {
                // if (tableColumn instanceof ExtTableColumn)
                // {
                // ((ExtTableColumn) tableColumn).setVisible(false);
                // }
                // }
                // else
                {
                    ColumnState columnState = this.columnStates[index];

                    columnState.update(tableColumn);
                }
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

    /**
     * Liefert den gespeicherten ModelIndex einer Spalte.
     *
     * @param index int
     *
     * @return int
     */
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

    /**
     * Liefert eine Liste mit den {@link TableColumn}s.
     *
     * @param table {@link JTable}
     *
     * @return {@link List}
     */
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
