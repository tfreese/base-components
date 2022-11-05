package de.freese.base.utils;

import java.awt.Component;
import java.awt.Rectangle;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import de.freese.base.swing.components.table.AbstractListTableModel;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public final class TableUtils
{
    public static void cancelCellEditing(final JTable table)
    {
        try
        {
            if (table.getCellEditor() != null)
            {
                table.getCellEditor().cancelCellEditing();
            }
        }
        catch (Exception ex)
        {
            LoggerFactory.getLogger(TableUtils.class).warn(null, ex);
        }
    }

    /**
     * <a href="http://www.exampledepot.com/egs/javax.swing.table/VisCenter.html">VisCenter</a><br>
     * Selektiert die Zelle an der gewünschten Position und zentriert sie innerhalb der ScrollPane.
     */
    public static void centerTableInScrollPane(final JTable table, final int row, final int column)
    {
        JViewport viewport = (JViewport) table.getParent();
        table.changeSelection(row, column, false, false);

        // This rectangle is relative to the table where the northwest corner of cell (0,0) is
        // always (0,0).
        Rectangle rect = table.getCellRect(row, column, true);

        // The location of the view relative to the table
        Rectangle viewRect = viewport.getViewRect();

        // Translate the cell location so that it is relative to the view, assuming the northwest
        // corner of the view is (0,0).
        rect.setLocation(rect.x - viewRect.x, rect.y - viewRect.y);

        // Calculate location of rect if it were at the center of view
        int centerX = (viewRect.width - rect.width) / 2;
        int centerY = (viewRect.height - rect.height) / 2;

        // Fake the location of the cell so that scrollRectToVisible will move the cell to the
        // center
        if (rect.x < centerX)
        {
            centerX = -centerX;
        }

        if (rect.y < centerY)
        {
            centerY = -centerY;
        }

        rect.translate(centerX, centerY);

        // Scroll the area into view.
        viewport.scrollRectToVisible(rect);
    }

    public static TableCellRenderer getCellRenderer(final JTable table, final int column)
    {
        int viewIndex = table.convertColumnIndexToView(column);

        TableCellRenderer renderer = null;

        if (viewIndex >= 0)
        {
            renderer = table.getCellRenderer(0, viewIndex);
        }

        if (renderer == null)
        {
            renderer = table.getDefaultRenderer(table.getModel().getColumnClass(column));
        }

        return renderer;
    }

    public static TableCellRenderer getCellRenderer(final JTable table, final TableColumn column)
    {
        return getCellRenderer(table, column.getModelIndex());
    }

    public static Object getFirstSelectedObject(final JTable table)
    {
        Object[] obj = getSelectedObjects(table);

        if ((obj != null) && (obj.length > 0))
        {
            return obj[0];
        }

        return null;
    }

    public static TableCellRenderer getHeaderRenderer(final JTable table, final TableColumn column)
    {
        TableCellRenderer renderer = column.getHeaderRenderer();

        if (renderer == null)
        {
            JTableHeader header = table.getTableHeader();

            if (header != null)
            {
                renderer = header.getDefaultRenderer();
            }
        }

        return renderer;
    }

    public static String getRenderedValueAt(final JTable table, final int row, final int column)
    {
        String value = null;

        TableCellRenderer tcr = getCellRenderer(table, column);

        if (tcr != null)
        {
            Component c = table.prepareRenderer(tcr, row, column);

            if (c instanceof JLabel label)
            {
                value = label.getText().strip();
            }
            else if (c instanceof JCheckBox checkBox)
            {
                if (checkBox.isSelected())
                {
                    value = Boolean.TRUE.toString();
                }
                else
                {
                    value = Boolean.FALSE.toString();
                }
            }
        }

        if (value == null)
        {
            TableModel tm = table.getModel();

            if (tm != null)
            {
                int colIdxModel = table.convertColumnIndexToModel(column);
                value = tm.getValueAt(row, colIdxModel).toString().strip();
            }
        }

        return value;
    }

    public static Object[] getSelectedObjects(final JTable table)
    {
        int[] rows = table.getSelectedRows();
        Object[] result = new Object[rows.length];

        if (table.getModel() instanceof AbstractListTableModel<?> model)
        {
            for (int i = 0; i < rows.length; i++)
            {
                int modelRowIndex = table.convertRowIndexToModel(rows[i]);

                result[i] = model.getObjectAt(modelRowIndex);
            }
        }

        return result;
    }

    /**
     * Setzt die optimale Breite der {@link TableColumn}s.
     *
     * @param margin int; -1 = default
     */
    public static void packColumn(final JTable table, final int column, final int margin)
    {
        packColumn(table, table.getColumnModel().getColumn(column), margin, -1, -1);
    }

    /**
     * Setzt die optimale Breite der {@link TableColumn}s.
     *
     * @param margin int; -1 = default
     * @param min int; -1 = default
     * @param max int; -1 = default
     */
    public static void packColumn(final JTable table, final int column, final int margin, final int min, final int max)
    {
        packColumn(table, table.getColumnModel().getColumn(column), margin, min, max);
    }

    /**
     * Setzt die optimale Breite der {@link TableColumn}, falls diese Resizeable ist.
     *
     * @param margin int; -1 = default
     * @param min int; -1 = default
     * @param max int; -1 = default
     */
    public static void packColumn(final JTable table, final TableColumn column, int margin, final int min, final int max)
    {
        if (!column.getResizable())
        {
            return;
        }

        int columnIndex = table.convertColumnIndexToView(column.getModelIndex());
        int width = 0;
        TableCellRenderer headerRenderer = getHeaderRenderer(table, column);

        if (headerRenderer != null)
        {
            Component comp = headerRenderer.getTableCellRendererComponent(table, column.getHeaderValue(), false, false, 0, columnIndex);
            width = comp.getPreferredSize().width;
        }

        TableCellRenderer renderer = getCellRenderer(table, column);

        for (int r = 0; r < table.getRowCount(); r++)
        {
            Component comp = renderer.getTableCellRendererComponent(table, table.getValueAt(r, columnIndex), false, false, r, columnIndex);
            width = Math.max(width, comp.getPreferredSize().width);
        }

        if (margin < 0)
        {
            margin = 3;
        }

        width += 2 * margin;

        if ((min > 0) && (width < min))
        {
            width = min;
        }

        if ((max > 0) && (width > max))
        {
            width = max;
        }

        column.setPreferredWidth(width);
    }

    /**
     * Setzt die optimale Breite der {@link TableColumn}s.
     *
     * @param margin int; -1 = default
     */
    public static void packTable(final JTable table, final int margin)
    {
        for (int c = 0; c < table.getColumnCount(); c++)
        {
            packColumn(table, table.getColumnModel().getColumn(c), margin, -1, -1);
        }
    }

    /**
     * Setzt die optimale Breite der {@link TableColumn}s.
     *
     * @param margin int; -1 = default
     * @param min int; -1 = default
     * @param max int; -1 = default
     */
    public static void packTable(final JTable table, final int margin, final int min, final int max)
    {
        for (int c = 0; c < table.getColumnCount(); c++)
        {
            packColumn(table, table.getColumnModel().getColumn(c), margin, min, max);
        }
    }

    public static void stopCellEditing(final JTable table)
    {
        try
        {
            if (table.getCellEditor() != null)
            {
                table.getCellEditor().stopCellEditing();
            }
        }
        catch (Exception ex)
        {
            LoggerFactory.getLogger(TableUtils.class).warn(null, ex);
        }
    }
}
