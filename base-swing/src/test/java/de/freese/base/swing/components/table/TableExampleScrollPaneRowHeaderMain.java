// Created: 04.11.2021
package de.freese.base.swing.components.table;

import java.awt.Dimension;
import java.awt.Font;
import java.io.Serial;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * @author Thomas Freese
 */
public final class TableExampleScrollPaneRowHeaderMain
{
    /**
     * @author Thomas Freese
     */
    private static final class MyTableModel extends AbstractListTableModel<Map<String, String>>
    {
        @Serial
        private static final long serialVersionUID = 767661536272989643L;

        private MyTableModel(final List<String> columnNames)
        {
            super(columnNames);
        }

        /**
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         */
        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex)
        {
            Map<String, String> map = getObjectAt(rowIndex);

            return map.get(getColumnName(columnIndex));
        }
    }

    public static void main(final String[] args)
    {
        List<Map<String, String>> list = new ArrayList<>();

        for (int row = 0; row < 100; row++)
        {
            Map<String, String> map = new LinkedHashMap<>();
            list.add(map);

            for (int col = 0; col < 10; col++)
            {
                map.put("Spalte-" + col, "Value-" + row + "-" + col);
            }
        }

        // ViewPort
        MyTableModel tableModelData = new MyTableModel(new ArrayList<>(list.get(0).keySet()));
        tableModelData.addAll(list);

        JTable tableData = new JTable(tableModelData);
        tableData.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tableData.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableData.setAutoCreateRowSorter(true);
        tableData.getTableHeader().setFont(tableData.getTableHeader().getFont().deriveFont(Font.BOLD, 13F));
        // tableData.getTableHeader().setBackground(Color.GRAY);
        // tableData.getTableHeader().setForeground(Color.WHITE);

        TableColumnModel columnModel = tableData.getColumnModel();

        for (int c = 0; c < columnModel.getColumnCount(); c++)
        {
            columnModel.getColumn(c).setPreferredWidth(100);
        }

        // RowHeader
        JTable tableRowHeader = new JTable();
        tableRowHeader.setAutoCreateColumnsFromModel(false);
        tableRowHeader.setModel(tableModelData);
        tableRowHeader.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tableRowHeader.setSelectionModel(tableData.getSelectionModel());
        tableRowHeader.setDragEnabled(false);
        tableRowHeader.setFocusable(false);

        // Sonst würden Events 2x verarbeitet werden, jeweils 1x pro Tabelle.
        tableModelData.removeTableModelListener(tableRowHeader);

        tableRowHeader.getTableHeader().setFont(tableRowHeader.getTableHeader().getFont().deriveFont(Font.BOLD, 13F));
        tableRowHeader.getTableHeader().setReorderingAllowed(false);
        tableRowHeader.getTableHeader().setResizingAllowed(false);

        tableRowHeader.setRowSorter(tableData.getRowSorter());

        // Die ersten beiden Spalten der Daten-Tabelle in den RowHeader stecken.
        for (int i = 0; i < 2; i++)
        {
            TableColumn tableColumn = columnModel.getColumn(i);
            columnModel.removeColumn(tableColumn);
            tableRowHeader.getColumnModel().addColumn(tableColumn);
        }

        // ScrollPane-RowHeader an Breite der Tabelle anpassen.
        tableRowHeader.setPreferredScrollableViewportSize(tableRowHeader.getPreferredSize());

        // GUI
        JScrollPane scrollPane = new JScrollPane(tableData);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setRowHeaderView(tableRowHeader);
        scrollPane.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, tableRowHeader.getTableHeader());

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(scrollPane);
        frame.setSize(new Dimension(800, 600));

        SwingUtilities.invokeLater(() ->
        {
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private TableExampleScrollPaneRowHeaderMain()
    {
        super();
    }
}
