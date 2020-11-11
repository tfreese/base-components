package de.freese.base.swing.clipboard;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

/**
 * Adapter einer JTable fuer die Zwischenablage.
 *
 * @author Thomas Freese
 */
public class TableClipboardAdapter extends AbstractClipboardAdapter
{
    /**
     * MouseAdapter der Tabelle fuer das Popup.
     * 
     * @author Thomas Freese
     */
    protected class PopupListener extends MouseAdapter
    {
        /**
         * Creates a new {@link PopupListener} object.
         */
        public PopupListener()
        {
            super();
        }

        /**
         * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
         */
        @Override
        public void mouseReleased(final MouseEvent e)
        {
            if (isEnabled() && e.isPopupTrigger())
            {
                JPopupMenu popup = getPopupMenu();

                if (popup != null)
                {
                    // JTable table = (JTable) e.getSource();
                    // int row = table.rowAtPoint(new Point(e.getX(),e.getY()));
                    int[] rowsSelected = getTable().getSelectedRows();
                    int[] colsSelected = getTable().getSelectedColumns();

                    if ((rowsSelected.length > 0) && (colsSelected.length > 0))
                    {
                        if (getTable().isEnabled())
                        {
                            popup.show(e.getComponent(), e.getX(), e.getY());
                        }
                    }
                }
            }
        }
    }

    /**
     * 
     */
    private boolean externalPopup;

    /**
     *
     */
    private JPopupMenu popup;

    /**
     * Creates a new {@link TableClipboardAdapter} object.
     * 
     * @param table {@link JTable}
     */
    public TableClipboardAdapter(final JTable table)
    {
        this(table, null);
    }

    /**
     * Creates a new {@link TableClipboardAdapter} object.
     * 
     * @param table {@link JTable}
     * @param popupMenu JPopupMenu, falls bereits ein JPopupMenu existiert.
     */
    public TableClipboardAdapter(final JTable table, final JPopupMenu popupMenu)
    {
        super(table);

        this.popup = popupMenu;

        if (popupMenu != null)
        {
            this.externalPopup = true;
        }

        initialize();
    }

    /**
     * @see de.freese.base.swing.clipboard.AbstractClipboardAdapter#doCopy()
     */
    @Override
    public void doCopy()
    {
        int[] rowsSelected = getTable().getSelectedRows();
        int[] colsSelected = getTable().getSelectedColumns();

        if ((rowsSelected.length == 0) && (colsSelected.length == 0))
        {
            Toolkit.getDefaultToolkit().beep();

            JOptionPane.showMessageDialog(null, "Invalid Copy Selection", "Invalid Copy Selection", JOptionPane.ERROR_MESSAGE);

            return;
        }

        StringBuilder sb = new StringBuilder();

        for (int element : rowsSelected)
        {
            for (int col = 0; col < colsSelected.length; col++)
            {
                Class<?> clazz = getTable().getColumnClass(colsSelected[col]);
                ClipboardConverter converter = getConverter(clazz);

                Object value = getTable().getValueAt(element, colsSelected[col]);
                String stringValue = "";

                if (value != null)
                {
                    stringValue = value.toString();
                }

                if (converter != null)
                {
                    stringValue = converter.toClipboard(value);
                }

                sb.append(stringValue);

                if (col < (colsSelected.length - 1))
                {
                    sb.append("\t");
                }
            }

            sb.append("\n");
        }

        StringSelection selection = new StringSelection(sb.toString());

        getClipboard().setContents(selection, selection);
    }

    /**
     * @see de.freese.base.swing.clipboard.AbstractClipboardAdapter#doPaste(boolean)
     */
    @Override
    public void doPaste(final boolean flipAxes)
    {
        int[] rowsSelected = getTable().getSelectedRows();
        int[] colsSelected = getTable().getSelectedColumns();

        if ((rowsSelected.length == 0) && (colsSelected.length == 0))
        {
            Toolkit.getDefaultToolkit().beep();

            JOptionPane.showMessageDialog(null, "Invalid Paste Selection", "Invalid Paste Selection", JOptionPane.ERROR_MESSAGE);

            return;
        }

        int startRow = rowsSelected[0];
        int startCol = colsSelected[0];

        String clipboardString = "";

        try
        {
            clipboardString = (String) (getClipboard().getContents(this).getTransferData(DataFlavor.stringFlavor));
        }
        catch (Exception ex)
        {
            getLogger().error(null, ex);
        }
        // 2 dim String Array der Werte aufbauen
        String[][] matrix = getPasteMatrix(clipboardString);

        if (flipAxes)
        {
            matrix = flipMatrix(matrix);
        }

        List<Point> points = new ArrayList<>();

        for (int row = 0; row < matrix.length; row++)
        {
            for (int col = 0; col < matrix[row].length; col++)
            {
                String stringValue = matrix[row][col];
                int currentColumn = startCol + col;
                int currentRow = startRow + row;

                if ((currentRow < getTable().getRowCount()) && (currentColumn < getTable().getColumnCount()))
                {
                    Class<?> clazz = getTable().getColumnClass(currentColumn);
                    ClipboardConverter converter = getConverter(clazz);
                    Object value = null;

                    if (converter == null)
                    {
                        continue;
                    }

                    if (!"".equals(stringValue))
                    {
                        value = converter.fromClipboard(stringValue);
                    }

                    if ((value != null) && getTable().isCellEditable(currentRow, currentColumn))
                    {
                        getTable().setValueAt(value, currentRow, currentColumn);

                        points.add(new Point(row, col));
                    }
                }
            }
        }

        // Selektiere alle eingefuegten Werte
        for (Point point : points)
        {
            int rowIndex = startRow + (int) point.getX();
            int columnIndex = startCol + (int) point.getY();

            if ((getTable().getRowCount() > rowIndex) && (getTable().getColumnCount() > columnIndex))
            {
                getTable().addRowSelectionInterval(rowIndex, rowIndex);
                getTable().addColumnSelectionInterval(columnIndex, columnIndex);
            }
        }
    }

    /**
     * Dreht die Achsen der eingehenden Matrix.
     * 
     * @param matrix String[][]
     * @return String[][]
     */
    private String[][] flipMatrix(final String[][] matrix)
    {
        if (matrix == null)
        {
            return null;
        }

        String[][] newMatrix = new String[matrix[0].length][matrix.length];

        for (int row = 0; row < matrix.length; row++)
        {
            for (int col = 0; col < matrix[row].length; col++)
            {
                newMatrix[col][row] = matrix[row][col];
            }
        }

        return newMatrix;
    }

    /**
     * Liefert ein 2 dim String Array fuer die Paste Action.
     * 
     * @param clipboardString String
     * @return String[][]
     */
    private String[][] getPasteMatrix(final String clipboardString)
    {
        String[] rows = clipboardString.split("[\n]");

        // 1. max. Anzahl an Spalten ermitteln
        int maxCols = Integer.MIN_VALUE;

        for (String row2 : rows)
        {
            String[] cols = row2.split("[\t]");

            maxCols = Math.max(maxCols, cols.length);
        }

        if (maxCols <= 0)
        {
            return new String[rows.length][0];
        }

        // 2. StringMatrix fuellen
        String[][] matrix = new String[rows.length][maxCols];

        for (int row = 0; row < rows.length; row++)
        {
            String[] cols = rows[row].split("[\t]");

            for (int col = 0; col < cols.length; col++)
            {
                String stringValue = cols[col];

                matrix[row][col] = stringValue;
            }
        }

        return matrix;
    }

    /**
     * PopupMenu der Tabelle.
     * 
     * @return {@link JPopupMenu}
     */
    public JPopupMenu getPopupMenu()
    {
        if (this.popup == null)
        {
            this.popup = new JPopupMenu();
        }

        return this.popup;
    }

    /**
     * Liefert die Tabelle.
     * 
     * @return {@link JTable}
     */
    private JTable getTable()
    {
        return (JTable) getComponent();
    }

    /**
     * @see de.freese.base.swing.clipboard.AbstractClipboardAdapter#initialize()
     */
    @Override
    protected void initialize()
    {
        super.initialize();

        getPopupMenu().add(getActionCopy());
        getPopupMenu().add(getActionPaste());
        getPopupMenu().add(getActionPasteFlipAxes());

        getTable().setColumnSelectionAllowed(true);
        getTable().setRowSelectionAllowed(true);

        if (!this.externalPopup)
        {
            getTable().addMouseListener(new PopupListener());
        }
    }
}
