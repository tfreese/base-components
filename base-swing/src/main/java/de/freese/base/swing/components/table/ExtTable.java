package de.freese.base.swing.components.table;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.io.Serial;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;

import de.freese.base.swing.components.table.column.DefaultExtTableColumnModel;
import de.freese.base.swing.components.table.column.ExtTableColumn;
import de.freese.base.swing.components.table.column.ExtTableColumnModel;
import de.freese.base.swing.components.table.column.ExtTableColumnModelListener;
import de.freese.base.swing.components.table.columncontrol.ColumnControlButton;
import de.freese.base.swing.components.table.sort.Sort;
import de.freese.base.swing.components.table.sort.TableColumnSorter;
import de.freese.base.utils.TableUtils;

/**
 * @author Thomas Freese
 */
public class ExtTable extends JTable implements ExtTableColumnModelListener {
    @Serial
    private static final long serialVersionUID = -4454292369350861849L;
    /**
     * Show an alternative Header.
     */
    private final boolean showHeader;

    private ColumnControlButton columnControlButton;
    private JComponent columnHeaderReplacement;
    private JComponent rowHeaderReplacement;
    private boolean sortable = true;

    public ExtTable() {
        this(false);
    }

    public ExtTable(final boolean showHeader) {
        super();

        this.showHeader = showHeader;
    }

    @Override
    public void addNotify() {
        super.addNotify();

        if (rowHeaderReplacement != null) {
            installRowHeader(rowHeaderReplacement);
        }
        else if (columnHeaderReplacement != null) {
            installColumnHeader(columnHeaderReplacement);
        }
    }

    @Override
    public void columnPropertyChange(final PropertyChangeEvent event) {
        //        if ("sort".equals(event.getPropertyName()))
        //        {
        //            // System.out.println(event.getSource());
        //        }
        //        else
        if ("visible".equals(event.getPropertyName())) {
            // If RowSorter exist set the Column to UNSORTED.
            if (getClientProperty("ROWSORTER") != null) {
                final TableColumnSorter rowSorter = (TableColumnSorter) getClientProperty("ROWSORTER");
                final ExtTableColumn tableColumnExt = (ExtTableColumn) event.getSource();

                if (!Sort.UNSORTED.equals(tableColumnExt.getSort())) {
                    rowSorter.setSortStatus(tableColumnExt, Sort.UNSORTED);
                }
            }

            repaint();
        }
    }

    public void configureColumnControl() {
        final Container p = getParent();

        if (p instanceof JViewport) {
            final Container gp = p.getParent();

            if (gp instanceof JScrollPane scrollPane) {
                final JViewport viewport = scrollPane.getViewport();

                if (viewport == null || viewport.getView() != this) {
                    return;
                }

                if (isColumnControlVisible()) {
                    scrollPane.setCorner(ScrollPaneConstants.UPPER_TRAILING_CORNER, getColumnControl());

                    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                }
            }
        }
    }

    @Override
    public void createDefaultColumnsFromModel() {
        final TableModel model = getModel();

        if (model != null) {
            // Remove any current columns
            final TableColumnModel cm = getColumnModel();

            while (cm.getColumnCount() > 0) {
                cm.removeColumn(cm.getColumn(0));
            }

            // Create new columns from the data model info
            for (int i = 0; i < model.getColumnCount(); i++) {
                final TableColumn newColumn = new ExtTableColumn(i);
                addColumn(newColumn);
            }
        }
    }

    public ColumnControlButton getColumnControl() {
        if (columnControlButton == null) {
            columnControlButton = (ColumnControlButton) createDefaultColumnControl();
        }

        return columnControlButton;
    }

    public ExtTableColumnModel getColumnModelExt() {
        return (ExtTableColumnModel) getColumnModel();
    }

    public void installColumnHeader(final JComponent tableHeaderReplacement) {
        if (!showHeader()) {
            return;
        }

        columnHeaderReplacement = tableHeaderReplacement;

        final Container parent = getParent();

        if (parent instanceof JViewport) {
            final JScrollPane enclosingScrollPane = (JScrollPane) parent.getParent();

            if (tableHeaderReplacement == null) {
                enclosingScrollPane.setColumnHeaderView(getTableHeader());
            }
            else {
                enclosingScrollPane.setColumnHeaderView(tableHeaderReplacement);
            }
        }
    }

    public void installRowHeader(final JComponent tableHeaderReplacement) {
        if (!showHeader()) {
            return;
        }

        rowHeaderReplacement = tableHeaderReplacement;

        final Container parent = getParent();

        if (parent instanceof JViewport) {
            final JScrollPane enclosingScrollPane = (JScrollPane) parent.getParent();

            if (tableHeaderReplacement == null) {
                enclosingScrollPane.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, getTableHeader());
            }
            else {
                enclosingScrollPane.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, tableHeaderReplacement);
            }
        }
    }

    public boolean isColumnControlVisible() {
        return getColumnControl().isVisible();
    }

    public boolean isSortable() {
        return sortable;
    }

    /**
     * @param margin int, -1 als default
     */
    public void packAll(final int margin) {
        TableUtils.packTable(this, margin);
    }

    /**
     * @param margin int, -1 als default
     */
    public void packColumn(final int columnIndex, final int margin) {
        TableUtils.packColumn(this, columnIndex, margin);
    }

    /**
     * @param margin int, -1 als default
     * @param max int, -1 als default
     */
    public void packColumn(final int columnIndex, final int margin, final int max) {
        TableUtils.packColumn(this, columnIndex, margin, 20, max);
    }

    @Override
    public Component prepareEditor(final TableCellEditor editor, final int row, final int column) {
        // Select hole Text in a Text-Component.
        if (!isFocusOwner()) {
            requestFocus();
        }

        final Component component = super.prepareEditor(editor, row, column);

        if (component instanceof JTextComponent c) {
            c.selectAll();
        }

        return component;
    }

    public void setColumnControlVisible(final boolean visible) {
        getColumnControl().setVisible(visible);
    }

    public void setSortable(final boolean sortable) {
        this.sortable = sortable;
    }

    @Override
    protected void configureEnclosingScrollPane() {
        configureColumnControl();

        if (!showHeader()) {
            super.configureEnclosingScrollPane();

            return;
        }

        // well, this is a copy of JTable source code, where the TableHeader installation
        // has been removed as it created problems with installHeader method.
        final Container p = getParent();

        if (p instanceof JViewport) {
            final Container gp = p.getParent();

            if (gp instanceof JScrollPane scrollPane) {
                final Border border = scrollPane.getBorder();

                if (border == null || border instanceof UIResource) {
                    scrollPane.setBorder(UIManager.getBorder("Table.scrollPaneBorder"));
                }
            }
        }
    }

    protected JComponent createDefaultColumnControl() {
        return new ColumnControlButton(this);
    }

    @Override
    protected TableColumnModel createDefaultColumnModel() {
        return new DefaultExtTableColumnModel();
        // columnModel.addColumnModelListener(this);
        //
        // return columnModel;
    }

    @Override
    protected void initializeLocalVars() {
        super.initializeLocalVars();

        // Close CellEditor on FocusLost.
        putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        // Start editing on KeyStrokes.
        putClientProperty("JTable.autoStartsEdit", Boolean.TRUE);

        // Pack Actions
        final ActionMap map = getActionMap();
        map.put("pack_all", new AbstractAction("pack_all") {
            @Serial
            private static final long serialVersionUID = -5537831177642642702L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                packAll(-1);
            }
        });

        // Sortierung
        TableColumnSorter.add(this);
    }

    protected boolean showHeader() {
        return showHeader;
    }
}
