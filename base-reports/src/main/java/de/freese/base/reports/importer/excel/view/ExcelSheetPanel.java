package de.freese.base.reports.importer.excel.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Objects;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import de.freese.base.reports.importer.excel.ExcelSheet;

/**
 * Panel eines Sheets einer Exceldatei.
 *
 * @author Thomas Freese
 */
public class ExcelSheetPanel extends JPanel
{
    /**
     *
     */
    private static final long serialVersionUID = -1946632829758128806L;
    /**
     *
     */
    private final ExcelSheet excelSheet;
    /**
     *
     */
    private JScrollPane scrollPane;
    /**
     *
     */
    private JTable table;

    /**
     * Creates a new {@link ExcelSheetPanel} object.
     *
     * @param excelSheet {@link ExcelSheet}
     */
    public ExcelSheetPanel(final ExcelSheet excelSheet)
    {
        super();

        this.excelSheet = Objects.requireNonNull(excelSheet, "excelSheet required");
        initialize();
    }

    /**
     * Liefert die Tabelle eines Excelsheets.
     *
     * @return {@link JTable}
     */
    public JTable getTable()
    {
        if (this.table == null)
        {
            this.table = new JTable();
        }

        return this.table;
    }

    /**
     * Liefert die ScrollPane der Tabelle.
     *
     * @return {@link JScrollPane}
     */
    private JScrollPane getScrollPane()
    {
        if (this.scrollPane == null)
        {
            this.scrollPane = new JScrollPane();
            this.scrollPane.setViewportView(getTable());
        }

        return this.scrollPane;
    }

    /**
     * This method initializes this
     */
    private void initialize()
    {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        gbc.weightx = 1.0;
        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(800, 600));
        add(getScrollPane(), gbc);

        getTable().setModel(new ExcelSheetTableModel(this.excelSheet));
        getTable().setDefaultRenderer(Object.class, new ExcelSheetRenderer());
        getTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        getTable().setColumnSelectionAllowed(true);
        getTable().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        getTable().getTableHeader().setReorderingAllowed(false);
    }
}
