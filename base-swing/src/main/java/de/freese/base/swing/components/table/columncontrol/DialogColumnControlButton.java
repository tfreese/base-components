/**
 *
 */
package de.freese.base.swing.components.table.columncontrol;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.table.TableColumn;
import org.apache.commons.lang3.BooleanUtils;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.icon.ColumnControlIcon;
import org.jdesktop.swingx.table.TableColumnExt;
import de.freese.base.swing.components.dialog.DialogFactory;
import de.freese.base.swing.components.dialog.ExtDialog;
import de.freese.base.swing.components.table.ExtTable;
import de.freese.base.utils.GuiUtils;

/**
 * Button für einen Dialog/Popup der Tabelleneigenschaften der {@link ExtTable}.
 *
 * @author Thomas Freese
 */
public class DialogColumnControlButton extends JButton implements PropertyChangeListener
{
    /**
     *
     */
    public static final String COLUMN_CONTROL_DISABLED = "COLUMN_CONTROL_DISABLED";

    /**
     *
     */
    private static final long serialVersionUID = -3076920096726720396L;

    /**
     * Property für {@link PropertyChangeListener}, wenn Sichtbarkeit geändert.
     */
    public static final String TOGGLE_VISIBILITY = "toggleVisibility";

    /**
     *
     */
    private int groupedColumnCount = 15;

    /**
     *
     */
    private final JXTable table;

    /**
     * Erstellt ein neues {@link DialogColumnControlButton} Objekt.
     * 
     * @param table {@link JXTable}
     */
    public DialogColumnControlButton(final JXTable table)
    {
        super();

        if (table == null)
        {
            throw new NullPointerException("table");
        }

        this.table = table;

        init();
    }

    /**
     * Diese Methode bietet die Möglichkeit die Komponenten der Spalten spezifisch anzupassen.
     * 
     * @param panel {@link JPanel}
     * @param columnComponentMap {@link Map}
     */
    protected void configureColumnComponents(final JPanel panel, final Map<TableColumnExt, JComponent> columnComponentMap)
    {
        // EMPTY
    }

    /**
     * Erzeugt die Komponente für die Spalte.
     * 
     * @param column {@link TableColumnExt}
     * @return {@link JComponent}
     */
    protected JComponent createColumnComponent(final TableColumnExt column)
    {
        if (!isColumnControled(column))
        {
            return null;
        }

        JCheckBox checkBox = new JCheckBox();
        checkBox.setFocusPainted(false);
        checkBox.setFocusable(false);
        checkBox.setText(column.getTitle());
        checkBox.setSelected(column.isVisible());

        return checkBox;
    }

    /**
     * @return {@link Action}
     */
    protected Action createControlAction()
    {
        Action control = new AbstractAction()
        {
            /**
             *
             */
            private static final long serialVersionUID = 1L;

            /**
             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
             */
            @Override
            public void actionPerformed(final ActionEvent e)
            {
                showPopup();
            }
        };

        Icon icon = UIManager.getIcon("ColumnControlButton.actionIcon");

        if (icon == null)
        {
            icon = new ColumnControlIcon();
        }

        control.putValue(Action.SMALL_ICON, icon);

        String tooltip = "Properties";

        if (Locale.getDefault().equals(Locale.GERMAN) || Locale.getDefault().equals(Locale.GERMANY))
        {
            tooltip = "Eigenschaften";
        }

        control.putValue(Action.SHORT_DESCRIPTION, tooltip);

        return control;
    }

    /**
     * Erzeugt die Action für den ExcelExport.
     * 
     * @return {@link Action}
     */
    protected Action createExcelExportAction()
    {
        Action action = new AbstractAction("Excel Export (TODO)")
        {
            /**
             *
             */
            private static final long serialVersionUID = -6186677548811556005L;

            /**
             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
             */
            @Override
            public void actionPerformed(final ActionEvent e)
            {
                // JTableExcelExporter excelExporter = new JTableExcelExporter("");
                // ByteArrayOutputStream baos = new ByteArrayOutputStream();
                //
                // try
                // {
                // excelExporter.export(baos, null, getTable());
                // baos.close();
                //
                // ByteArrayDataSource dataSource =
                // new ByteArrayDataSource(baos.toByteArray(),
                // ByteArrayDataSource.MIMETYPE_APPLICATION_EXCEL);
                // dataSource.setName(String.format("%d.xls",
                // Long.valueOf(System.currentTimeMillis())));
                // String fileName = FileContext.getInstance().saveTemp(dataSource);
                // FileContext.getInstance().openFile(fileName);
                // }
                // catch (Exception ex)
                // {
                // LoggerFactory.getLogger(getClass()).error(null, ex);
                // }
            }
        };

        action.setEnabled(false);

        return action;
    }

    /**
     * Liefert die Anzahl der Spalten, welche untereinander dargestellt werden sollen.<br>
     * Dieser Wert steuert dadurch die Höhe des Dialoges/Popup.
     * 
     * @return int, Default 15
     */
    public int getGroupedColumnCount()
    {
        return this.groupedColumnCount;
    }

    /**
     * @return {@link JXTable}
     */
    protected JXTable getTable()
    {
        return this.table;
    }

    /**
     * Initialisiert den Button.
     */
    protected void init()
    {
        // setModel(new DefaultButtonModel());
        setFocusPainted(false);
        setFocusable(false);
        installAction();
        installTable();
    }

    /**
     * Konfiguriert die {@link Action}.
     */
    protected void installAction()
    {
        setAction(createControlAction());
    }

    /**
     * Konfiguriert die {@link JXTable}.
     */
    protected void installTable()
    {
        getTable().removePropertyChangeListener(this);
        getTable().addPropertyChangeListener(this);
        updateFromTableEnabledChanged();
    }

    /**
     * Liefert true, wenn die Sichtbarkeit der Spalte verändert werden darf.
     * 
     * @param column {@link TableColumnExt}
     * @return boolean
     */
    protected boolean isColumnControled(final TableColumnExt column)
    {
        Boolean controlDisabled = (Boolean) column.getClientProperty(COLUMN_CONTROL_DISABLED);

        if (controlDisabled == null)
        {
            return true;
        }

        return !BooleanUtils.toBoolean(controlDisabled);
    }

    /**
     * Setzt zusätzliche Komponenten unterhalb der Spalten in das Panel.
     * 
     * @param panel {@link JPanel}
     */
    protected void populateAdditionalComponents(final JPanel panel)
    {
        populatePackAll(panel);
        populateExcelExort(panel);
    }

    /**
     * Setzt die Komponenten der Spalten in das Panel.
     * 
     * @param panel {@link JPanel}
     * @param columnComponentMap {@link Map}
     */
    protected void populateColumns(final JPanel panel, final Map<TableColumnExt, JComponent> columnComponentMap)
    {
        List<TableColumn> columns = getTable().getColumns(true);

        int gbcCol = 0;
        int col = 0;

        for (TableColumn tableColumn : columns)
        {
            TableColumnExt columnExt = (TableColumnExt) tableColumn;
            JComponent component = createColumnComponent(columnExt);

            if (component == null)
            {
                continue;
            }

            columnComponentMap.put(columnExt, component);

            GridBagConstraints gbc = GuiUtils.getGBC(gbcCol, GridBagConstraints.RELATIVE);
            gbc.weightx = 0;
            gbc.weighty = 0;
            panel.add(component, gbc);

            col++;

            if ((col % getGroupedColumnCount()) == 0)
            {
                gbcCol++;
            }
        }
    }

    /**
     * Setzen der "Excel Export" Action.
     * 
     * @param panel {@link JPanel}
     */
    protected void populateExcelExort(final JPanel panel)
    {
        Action action = createExcelExportAction();

        GridBagConstraints gbc = GuiUtils.getGBC(0, GridBagConstraints.RELATIVE);
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(new JButton(action), gbc);
    }

    /**
     * Setzen der "Pack all" Action.
     * 
     * @param panel {@link JPanel}
     */
    protected void populatePackAll(final JPanel panel)
    {
        Action action = getTable().getActionMap().get(JXTable.PACKALL_ACTION_COMMAND);
        GridBagConstraints gbc = GuiUtils.getGBC(0, GridBagConstraints.RELATIVE);
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(5, 5, 0, 5);
        panel.add(new JButton(action), gbc);
    }

    /**
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    @Override
    public void propertyChange(final PropertyChangeEvent evt)
    {
        if ("enabled".equals(evt.getPropertyName()))
        {
            updateFromTableEnabledChanged();
        }
    }

    /**
     * Setzt die Anzahl der Spalten, welche untereinander dargestellt werden sollen.<br>
     * Dieser Wert steuert dadurch die Höhe des Dialoges/Popup.
     * 
     * @param groupedColumnCount int
     */
    public void setGroupedColumnCount(final int groupedColumnCount)
    {
        this.groupedColumnCount = groupedColumnCount;
    }

    /**
     * Zeigt den Dialog/Popup.<br>
     * Feuert das {@link PropertyChangeEvent} "toggleVisibility", wenn die Spaltensichtbarkeit verändert wird.
     */
    public void showPopup()
    {
        Map<TableColumnExt, JComponent> columnComponentMap = new HashMap<>();

        JPanel panel = new JPanel();
        // panel.initGUI();
        panel.setLayout(new GridBagLayout());

        // Panel aufbauen.
        populateColumns(panel, columnComponentMap);
        populateAdditionalComponents(panel);
        configureColumnComponents(panel, columnComponentMap);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(panel);

        // int choice =
        // JOptionPane.showOptionDialog(this, scrollPane,
        // getAction().getValue(Action.SHORT_DESCRIPTION).toString(),
        // JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
        //
        // if ((choice == JOptionPane.OK_OPTION) || (choice == JOptionPane.YES_OPTION))
        // {
        // toggleColumnVisibility(columnComponentMap);
        // }
        ExtDialog dialog = DialogFactory.createOkAbbrechen(this, getAction().getValue(Action.SHORT_DESCRIPTION).toString(), scrollPane, false);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        if (dialog.isYesOrOK())
        {
            toggleColumnVisibility(columnComponentMap);
        }

        columnComponentMap.clear();
        columnComponentMap = null;
    }

    /**
     * Ändert die Sichtbarkeit der einzelnen Spalten.
     * 
     * @param columnComponentMap {@link Map}
     */
    protected void toggleColumnVisibility(final Map<TableColumnExt, JComponent> columnComponentMap)
    {
        boolean doToggle = false;

        for (Entry<TableColumnExt, JComponent> entry : columnComponentMap.entrySet())
        {
            TableColumnExt column = entry.getKey();
            JComponent component = entry.getValue();

            boolean toggled = toggleColumnVisibility(column, component);

            if (toggled)
            {
                doToggle = true;
            }
        }

        if (doToggle)
        {
            firePropertyChange(TOGGLE_VISIBILITY, Boolean.FALSE, Boolean.TRUE);
        }
    }

    /**
     * Ändert die Sichtbarkeit der Spalte und liefert ein Flag, ob der alte und neue Zustand identisch sind.
     * 
     * @param column {@link TableColumnExt}
     * @param component {@link JComponent}
     * @return boolean; true=Sichtbarkeit geändert, false=nicht geändert
     */
    protected boolean toggleColumnVisibility(final TableColumnExt column, final JComponent component)
    {
        boolean toggled = false;

        if (component instanceof JCheckBox)
        {
            JCheckBox checkBox = (JCheckBox) component;

            if (column.isVisible() != checkBox.isSelected())
            {
                toggled = true;
                column.setVisible(checkBox.isSelected());
            }
        }

        return toggled;
    }

    /**
     * Synchronisiert die enabled-Flags der beiden Komponenten.
     */
    protected void updateFromTableEnabledChanged()
    {
        setEnabled(getTable().isEnabled());
    }
}
