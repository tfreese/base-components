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
public class TableGuiState extends AbstractGuiState {
    @Serial
    private static final long serialVersionUID = -8164953430592111778L;

    @XmlRootElement(name = "ColumnState")
    public static class ColumnState implements Serializable {
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

        public ColumnState() {
            super();
        }

        public ColumnState(final TableColumn tableColumn) {
            super();

            minWidth = tableColumn.getMinWidth();
            maxWidth = tableColumn.getMaxWidth();
            preferredWidth = tableColumn.getPreferredWidth();
            width = tableColumn.getWidth();
            resizeable = tableColumn.getResizable();
            modelIndex = tableColumn.getModelIndex();

            if (tableColumn instanceof TableColumnExt tableColumnExt) {
                visible = tableColumnExt.isVisible();
                editable = tableColumnExt.isEditable();
                sortable = tableColumnExt.isSortable();
            }
        }

        public void update(final TableColumn tableColumn) {
            if (width != -1) {
                tableColumn.setWidth(width);
            }

            if (minWidth != -1) {
                tableColumn.setMinWidth(minWidth);
            }

            if (maxWidth != -1) {
                tableColumn.setMaxWidth(maxWidth);
            }

            if (preferredWidth != -1) {
                tableColumn.setPreferredWidth(preferredWidth);
            }

            tableColumn.setResizable(resizeable);

            if (tableColumn instanceof TableColumnExt tableColumnExt) {
                tableColumnExt.setVisible(visible);
                tableColumnExt.setEditable(editable);
                tableColumnExt.setSortable(sortable);
            }
        }
    }

    private ColumnState[] columnStates;

    private int[] selectedRows;

    public TableGuiState() {
        super(JTable.class);
    }

    @Override
    public void restore(final Component component) {
        super.restore(component);

        final JTable table = (JTable) component;

        if (selectedRows != null && selectedRows.length > 0) {
            // With SINGLE_SELECTION must setColumnSelectionAllowed(false) be called to do this working.
            try {
                for (int row : selectedRows) {
                    table.addRowSelectionInterval(row, row);
                }

                final Rectangle rectangle = table.getCellRect(selectedRows[0], 0, true);
                table.scrollRectToVisible(rectangle);
            }
            catch (Exception _) {
                // Ignore
            }
        }

        if (columnStates != null && columnStates.length > 0) {
            final List<TableColumn> columns = getColumns(table);

            for (int i = 0; i < columns.size(); i++) {
                final int index = findModelIndex(i);

                if (index < 0) {
                    continue;
                }

                TableColumn tableColumn = null;

                if (index >= 0) {
                    tableColumn = columns.get(index);
                }

                final ColumnState columnState = columnStates[index];

                columnState.update(tableColumn);
            }
        }
    }

    @Override
    public void store(final Component component) {
        super.store(component);

        final JTable table = (JTable) component;

        selectedRows = table.getSelectedRows();

        final List<TableColumn> columns = getColumns(table);
        columnStates = new ColumnState[columns.size()];

        for (int i = 0; i < columns.size(); i++) {
            final TableColumn column = columns.get(i);
            columnStates[i] = new ColumnState(column);
        }
    }

    private int findModelIndex(final int index) {
        for (int j = 0; j < columnStates.length; j++) {
            if (columnStates[j].modelIndex == index) {
                return j;
            }
        }

        return -1;
    }

    private List<TableColumn> getColumns(final JTable table) {
        final List<TableColumn> columns = new ArrayList<>();

        if (table instanceof JXTable jxTable) {
            columns.addAll(jxTable.getColumns(true));
        }
        else {
            for (int i = 0; i < table.getColumnCount(); i++) {
                columns.add(table.getColumnModel().getColumn(i));
            }
        }

        return columns;
    }
}
