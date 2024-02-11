package de.freese.base.reports.importer.excel.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.Serial;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import de.freese.base.reports.importer.excel.ExcelSheet;
import de.freese.base.reports.importer.excel.ExcelToolkit;

/**
 * @author Thomas Freese
 */
public class ExcelPanel extends JPanel {
    @Serial
    private static final long serialVersionUID = 2622130940186653172L;

    private JTabbedPane tabbedPane;

    public ExcelPanel() {
        super();

        initialize();
    }

    public void addExcelSheet(final ExcelSheet excelSheet) {
        getJTabbedPane().addTab(excelSheet.getSheetName(), null, new ExcelSheetPanel(excelSheet), null);
    }

    public String getSelectedRange() {
        final ExcelSheetPanel selectedPanel = (ExcelSheetPanel) getJTabbedPane().getSelectedComponent();
        final JTable table = selectedPanel.getTable();

        return ExcelToolkit.getRange(table);
    }

    public void selectSheet(final String sheetName) {
        if ((sheetName == null) || sheetName.isEmpty()) {
            return;
        }

        for (int i = 0; i < getJTabbedPane().getTabCount(); i++) {
            if (sheetName.equals(getJTabbedPane().getTitleAt(i))) {
                getJTabbedPane().setSelectedIndex(i);

                break;
            }
        }
    }

    private JTabbedPane getJTabbedPane() {
        if (this.tabbedPane == null) {
            this.tabbedPane = new JTabbedPane();
            this.tabbedPane.setTabPlacement(SwingConstants.BOTTOM);
        }

        return this.tabbedPane;
    }

    private void initialize() {
        final GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0D;
        gridBagConstraints.weighty = 1.0D;
        setLayout(new GridBagLayout());
        this.setSize(800, 600);
        setPreferredSize(new Dimension(800, 600));
        this.add(getJTabbedPane(), gridBagConstraints);
    }
}
