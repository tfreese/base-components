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
 * Panel einer Exceldatei.
 *
 * @author Thomas Freese
 */
public class ExcelPanel extends JPanel
{
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 2622130940186653172L;
    /**
     *
     */
    private JTabbedPane tabbedPane;

    /**
     * Creates a new {@link ExcelPanel} object.
     */
    public ExcelPanel()
    {
        super();

        initialize();
    }

    /**
     * Hinzufügen eines Excelsheets als Tab.
     *
     * @param excelSheet {@link ExcelSheet}
     */
    public void addExcelSheet(final ExcelSheet excelSheet)
    {
        getJTabbedPane().addTab(excelSheet.getSheetName(), null, new ExcelSheetPanel(excelSheet), null);
    }

    /**
     * Liefert den selektierten Bereich in Excelnotation.
     *
     * @return String
     */
    public String getSelectedRange()
    {
        ExcelSheetPanel selectedPanel = (ExcelSheetPanel) getJTabbedPane().getSelectedComponent();
        JTable table = selectedPanel.getTable();

        return ExcelToolkit.getRange(table);
    }

    /**
     * Selektiert den Tab des Namens.
     *
     * @param sheetName String
     */
    public void selectSheet(final String sheetName)
    {
        if ((sheetName == null) || (sheetName.length() == 0))
        {
            return;
        }

        for (int i = 0; i < getJTabbedPane().getTabCount(); i++)
        {
            if (sheetName.equals(getJTabbedPane().getTitleAt(i)))
            {
                getJTabbedPane().setSelectedIndex(i);

                break;
            }
        }
    }

    /**
     * Liefert die TabbedPane der Excelsheets.
     *
     * @return {@link JTabbedPane}
     */
    private JTabbedPane getJTabbedPane()
    {
        if (this.tabbedPane == null)
        {
            this.tabbedPane = new JTabbedPane();
            this.tabbedPane.setTabPlacement(SwingConstants.BOTTOM);
        }

        return this.tabbedPane;
    }

    /**
     * This method initializes this
     */
    private void initialize()
    {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        setLayout(new GridBagLayout());
        this.setSize(800, 600);
        setPreferredSize(new Dimension(800, 600));
        this.add(getJTabbedPane(), gridBagConstraints);
    }
}
