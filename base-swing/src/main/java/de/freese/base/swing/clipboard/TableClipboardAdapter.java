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
 * Adapter einer JTable für die Zwischenablage.
 *
 * @author Thomas Freese
 */
public class TableClipboardAdapter extends AbstractClipboardAdapter {
    /**
     * Dreht die Achsen der eingehenden Matrix.
     */
    private static String[][] flipMatrix(final String[][] matrix) {
        if (matrix == null) {
            return new String[0][0];
        }

        final String[][] newMatrix = new String[matrix[0].length][matrix.length];

        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[row].length; col++) {
                newMatrix[col][row] = matrix[row][col];
            }
        }

        return newMatrix;
    }

    /**
     * Liefert ein 2 dim String Array für die Paste Action.
     */
    private static String[][] getPasteMatrix(final String clipboardString) {
        final String[] rows = clipboardString.split(System.lineSeparator());

        // 1. max. Anzahl an Spalten ermitteln
        int maxCols = Integer.MIN_VALUE;

        for (String row2 : rows) {
            final String[] cols = row2.split("\t");

            maxCols = Math.max(maxCols, cols.length);
        }

        if (maxCols <= 0) {
            return new String[rows.length][0];
        }

        // 2. StringMatrix füllen
        final String[][] matrix = new String[rows.length][maxCols];

        for (int row = 0; row < rows.length; row++) {
            final String[] cols = rows[row].split("\t");

            for (int col = 0; col < cols.length; col++) {
                final String stringValue = cols[col];

                matrix[row][col] = stringValue;
            }
        }

        return matrix;
    }

    /**
     * @author Thomas Freese
     */
    protected class PopupListener extends MouseAdapter {
        @Override
        public void mouseReleased(final MouseEvent event) {
            if (isEnabled() && event.isPopupTrigger()) {
                final JPopupMenu popupMenu = getPopupMenu();

                if (popupMenu != null) {
                    // final JTable table = (JTable) e.getSource();
                    // final int row = table.rowAtPoint(new Point(e.getX(),e.getY()));
                    final int[] rowsSelected = getTable().getSelectedRows();
                    final int[] colsSelected = getTable().getSelectedColumns();

                    if (rowsSelected.length > 0
                            && colsSelected.length > 0
                            && getTable().isEnabled()) {
                        popupMenu.show(event.getComponent(), event.getX(), event.getY());
                    }
                }
            }
        }
    }

    private boolean externalPopup;
    private JPopupMenu popup;

    public TableClipboardAdapter(final JTable table) {
        this(table, null);
    }

    public TableClipboardAdapter(final JTable table, final JPopupMenu popupMenu) {
        super(table);

        this.popup = popupMenu;

        if (popupMenu != null) {
            externalPopup = true;
        }

        initialize();
    }

    @Override
    public void doCopy() {
        final int[] rowsSelected = getTable().getSelectedRows();
        final int[] colsSelected = getTable().getSelectedColumns();

        if (rowsSelected.length == 0 && colsSelected.length == 0) {
            Toolkit.getDefaultToolkit().beep();

            JOptionPane.showMessageDialog(null, "Invalid Copy Selection", "Invalid Copy Selection", JOptionPane.ERROR_MESSAGE);

            return;
        }

        final StringBuilder sb = new StringBuilder();

        for (int element : rowsSelected) {
            for (int col = 0; col < colsSelected.length; col++) {
                final Class<?> clazz = getTable().getColumnClass(colsSelected[col]);
                final ClipboardConverter converter = getConverter(clazz);

                final Object value = getTable().getValueAt(element, colsSelected[col]);
                String stringValue = "";

                if (value != null) {
                    stringValue = value.toString();
                }

                if (converter != null) {
                    stringValue = converter.toClipboard(value);
                }

                sb.append(stringValue);

                if (col < (colsSelected.length - 1)) {
                    sb.append("\t");
                }
            }

            sb.append(System.lineSeparator());
        }

        final StringSelection selection = new StringSelection(sb.toString());

        getClipboard().setContents(selection, selection);
    }

    @Override
    public void doPaste(final boolean flipAxes) {
        final int[] rowsSelected = getTable().getSelectedRows();
        final int[] colsSelected = getTable().getSelectedColumns();

        if (rowsSelected.length == 0 && colsSelected.length == 0) {
            Toolkit.getDefaultToolkit().beep();

            JOptionPane.showMessageDialog(null, "Invalid Paste Selection", "Invalid Paste Selection", JOptionPane.ERROR_MESSAGE);

            return;
        }

        final int startRow = rowsSelected[0];
        final int startCol = colsSelected[0];

        String clipboardString = "";

        try {
            clipboardString = (String) (getClipboard().getContents(this).getTransferData(DataFlavor.stringFlavor));
        }
        catch (Exception ex) {
            getLogger().error(ex.getMessage(), ex);
        }

        // 2 dim String Array der Werte aufbauen.
        String[][] matrix = getPasteMatrix(clipboardString);

        if (flipAxes) {
            matrix = flipMatrix(matrix);
        }

        final List<Point> points = new ArrayList<>();

        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[row].length; col++) {
                final String stringValue = matrix[row][col];
                final int currentColumn = startCol + col;
                final int currentRow = startRow + row;

                if (currentRow < getTable().getRowCount() && currentColumn < getTable().getColumnCount()) {
                    final Class<?> clazz = getTable().getColumnClass(currentColumn);
                    final ClipboardConverter converter = getConverter(clazz);
                    Object value = null;

                    if (converter == null) {
                        continue;
                    }

                    if (!"".equals(stringValue)) {
                        value = converter.fromClipboard(stringValue);
                    }

                    if (value != null && getTable().isCellEditable(currentRow, currentColumn)) {
                        getTable().setValueAt(value, currentRow, currentColumn);

                        points.add(new Point(row, col));
                    }
                }
            }
        }

        // Selektiere alle eingefügten Werte.
        for (Point point : points) {
            final int rowIndex = startRow + (int) point.getX();
            final int columnIndex = startCol + (int) point.getY();

            if (getTable().getRowCount() > rowIndex && getTable().getColumnCount() > columnIndex) {
                getTable().addRowSelectionInterval(rowIndex, rowIndex);
                getTable().addColumnSelectionInterval(columnIndex, columnIndex);
            }
        }
    }

    public JPopupMenu getPopupMenu() {
        if (popup == null) {
            popup = new JPopupMenu();
        }

        return popup;
    }

    @Override
    protected void initialize() {
        super.initialize();

        getPopupMenu().add(getActionCopy());
        getPopupMenu().add(getActionPaste());
        getPopupMenu().add(getActionPasteFlipAxes());

        getTable().setColumnSelectionAllowed(true);
        getTable().setRowSelectionAllowed(true);

        if (!externalPopup) {
            getTable().addMouseListener(new PopupListener());
        }
    }

    private JTable getTable() {
        return (JTable) getComponent();
    }
}
