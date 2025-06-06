package de.freese.base.reports.importer.excel.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.Serial;
import java.util.Objects;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import de.freese.base.reports.importer.excel.ExcelSheet;

/**
 * @author Thomas Freese
 */
public class ExcelSheetPanel extends JPanel {
    @Serial
    private static final long serialVersionUID = -1946632829758128806L;

    private final ExcelSheet excelSheet;

    private JScrollPane scrollPane;
    private JTable table;

    public ExcelSheetPanel(final ExcelSheet excelSheet) {
        super();

        this.excelSheet = Objects.requireNonNull(excelSheet, "excelSheet required");

        initialize();
    }

    public JTable getTable() {
        if (table == null) {
            table = new JTable();
        }

        return table;
    }

    private JScrollPane getScrollPane() {
        if (scrollPane == null) {
            scrollPane = new JScrollPane();
            scrollPane.setViewportView(getTable());
        }

        return scrollPane;
    }

    private void initialize() {
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0D;
        gbc.weightx = 1.0D;
        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(800, 600));
        add(getScrollPane(), gbc);

        getTable().setModel(new ExcelSheetTableModel(excelSheet));
        getTable().setDefaultRenderer(Object.class, new ExcelSheetRenderer());
        getTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        getTable().setColumnSelectionAllowed(true);
        getTable().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        getTable().getTableHeader().setReorderingAllowed(false);
    }
}
