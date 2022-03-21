package de.freese.base.swing.components.table.sort;

import java.awt.Color;
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import de.freese.base.swing.components.table.column.ExtTableColumn;
import de.freese.base.swing.icon.ArrowIcon;

/**
 * Renderer für sortierbare Spalten.
 *
 * @author Thomas Freese
 */
public class RowSorterHeaderRenderer implements TableCellRenderer// , PropertyChangeListener
{
    /**
     *
     */
    public static final Color COLOR_MEDIUM_LIGHT_BROWN = new Color(150, 140, 130);
    /**
     * Icon
     */
    public static final Icon ICON_ASCENDING = new ArrowIcon(6, 6, SwingConstants.NORTH, COLOR_MEDIUM_LIGHT_BROWN);
    /**
     * Icon.
     */
    public static final Icon ICON_DESCENDING = new ArrowIcon(6, 6, SwingConstants.SOUTH, COLOR_MEDIUM_LIGHT_BROWN);
    /**
     *
     */
    private final TableColumnSorter rowSorter;
    /**
     * {@link TableCellRenderer}
     */
    private final TableCellRenderer tableCellRenderer;

    /**
     * Erstellt einen neuen {@link RowSorterHeaderRenderer}.
     *
     * @param rowSorter {@link TableColumnSorter}
     * @param tableCellRenderer {@link TableCellRenderer}
     */
    public RowSorterHeaderRenderer(final TableColumnSorter rowSorter, final TableCellRenderer tableCellRenderer)
    {
        super();

        this.rowSorter = rowSorter;
        this.tableCellRenderer = tableCellRenderer;

        // this.rowSorter.addPropertyChangeListener(this);
    }

    /**
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
     */
    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row,
                                                   final int column)
    {
        JComponent component = (JComponent) getTableCellRenderer().getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (column == -1)
        {
            return component;
        }

        TableColumn tc = table.getColumnModel().getColumn(column);

        if (!(tc instanceof ExtTableColumn tableColumnExt))
        {
            return component;
        }

        if (!tableColumnExt.isSortable() || Sort.UNSORTED.equals(tableColumnExt.getSort()))
        {
            return component;
        }

        Icon sortIcon = getIcon(tableColumnExt);
        String priority = getSortPriority(tableColumnExt);

        JComponent src = new SortRendererComponent(component, sortIcon, priority, COLOR_MEDIUM_LIGHT_BROWN);
        // src.setMinimumSize(c.getMinimumSize());
        // Breite der Spalte in die Komponente setzen
        // c.setPreferredSize(new Dimension(tc.getWidth(), 1));
        // c.setMinimumSize(new Dimension(tc.getWidth(), 1));

        return src;
    }

    /**
     * Liefert das Icon für den Renderer für die Sortierung des TableHeaders.
     *
     * @param tableColumnExt {@link ExtTableColumn}
     *
     * @return {@link Icon}
     */
    protected Icon getIcon(final ExtTableColumn tableColumnExt)
    {
        Sort sort = tableColumnExt.getSort();

        return (Sort.DESCENDING.equals(sort)) ? ICON_DESCENDING : ICON_ASCENDING;
    }

    /**
     * Liefert die Reihenfolge in der Sortierung der Spalte.
     *
     * @param tableColumnExt {@link ExtTableColumn}
     *
     * @return String
     */
    protected String getSortPriority(final ExtTableColumn tableColumnExt)
    {
        int index = this.rowSorter.getSortPriority(tableColumnExt);

        return (index == -1) ? null : ("" + (index + 1));
    }

    /**
     * Liefert den TableCellRenderer für die Tabellenüberschrift.
     *
     * @return {@link TableCellRenderer}
     */
    protected final TableCellRenderer getTableCellRenderer()
    {
        return this.tableCellRenderer;
    }

    // /**
    // * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
    // */
    // public void propertyChange(final PropertyChangeEvent evt)
    // {
    // if (evt.getPropertyName().equals("ROWSORTER_ORDER_CHANGED"))
    // {
    // if (this.tableCellRenderer instanceof Component)
    // {
    // ((Component) this.tableCellRenderer).repaint();
    // }
    // }
    // }
}
