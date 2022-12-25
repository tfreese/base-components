package de.freese.base.swing.components.table;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.io.Serial;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;

import de.freese.base.swing.components.table.column.DefaultExtTableColumnModel;
import de.freese.base.swing.components.table.column.ExtTableColumn;
import de.freese.base.swing.components.table.column.ExtTableColumnModel;
import de.freese.base.swing.components.table.column.ExtTableColumnModelListener;
import de.freese.base.swing.components.table.columncontrol.ColumnControlButton;
import de.freese.base.swing.components.table.sort.Sort;
import de.freese.base.swing.components.table.sort.TableColumnSorter;
import de.freese.base.utils.TableUtils;

/**
 * Erweiterte JTable.
 *
 * @author Thomas Freese
 */
public class ExtTable extends JTable implements ExtTableColumnModelListener
{
    @Serial
    private static final long serialVersionUID = -4454292369350861849L;
    /**
     * Gibt an, ob ein alternativer Header verwendet werden soll.
     */
    private final boolean showHeader;

    private ColumnControlButton columnControlButton;

    private JComponent columnHeaderReplacement;

    private JComponent rowHeaderReplacement;

    private boolean sortable = true;

    public ExtTable()
    {
        this(false);
    }

    public ExtTable(final boolean showHeader)
    {
        super();

        this.showHeader = showHeader;
    }

    /**
     * @see javax.swing.JTable#addNotify()
     */
    @Override
    public void addNotify()
    {
        super.addNotify();

        if (this.rowHeaderReplacement != null)
        {
            installRowHeader(this.rowHeaderReplacement);
        }
        else if (this.columnHeaderReplacement != null)
        {
            installColumnHeader(this.columnHeaderReplacement);
        }
    }

    @Override
    public void columnPropertyChange(final PropertyChangeEvent event)
    {
        //        if ("sort".equals(event.getPropertyName()))
        //        {
        //            // System.out.println(event.getSource());
        //        }
        //        else
        if ("visible".equals(event.getPropertyName()))
        {
            // Falls RowSorter vorhanden die Spalte auf UNSORTED setzen
            if (getClientProperty("ROWSORTER") != null)
            {
                TableColumnSorter rowSorter = (TableColumnSorter) getClientProperty("ROWSORTER");
                ExtTableColumn tableColumnExt = (ExtTableColumn) event.getSource();

                if (!Sort.UNSORTED.equals(tableColumnExt.getSort()))
                {
                    rowSorter.setSortStatus(tableColumnExt, Sort.UNSORTED);
                }
            }

            repaint();
        }
    }

    public void configureColumnControl()
    {
        Container p = getParent();

        if (p instanceof JViewport)
        {
            Container gp = p.getParent();

            if (gp instanceof JScrollPane scrollPane)
            {
                JViewport viewport = scrollPane.getViewport();

                if ((viewport == null) || (viewport.getView() != this))
                {
                    return;
                }

                if (isColumnControlVisible())
                {
                    scrollPane.setCorner(ScrollPaneConstants.UPPER_TRAILING_CORNER, getColumnControl());

                    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                }
            }
        }
    }

    /**
     * @see javax.swing.JTable#createDefaultColumnsFromModel()
     */
    @Override
    public void createDefaultColumnsFromModel()
    {
        TableModel model = getModel();

        if (model != null)
        {
            // Remove any current columns
            TableColumnModel cm = getColumnModel();

            while (cm.getColumnCount() > 0)
            {
                cm.removeColumn(cm.getColumn(0));
            }

            // Create new columns from the data model info
            for (int i = 0; i < model.getColumnCount(); i++)
            {
                TableColumn newColumn = new ExtTableColumn(i);
                addColumn(newColumn);
            }
        }
    }

    public ColumnControlButton getColumnControl()
    {
        if (this.columnControlButton == null)
        {
            this.columnControlButton = (ColumnControlButton) createDefaultColumnControl();
        }

        return this.columnControlButton;
    }

    public ExtTableColumnModel getColumnModelExt()
    {
        return (ExtTableColumnModel) getColumnModel();
    }

    public void installColumnHeader(final JComponent tableHeaderReplacement)
    {
        if (!showHeader())
        {
            return;
        }

        this.columnHeaderReplacement = tableHeaderReplacement;

        Container parent = getParent();

        if (parent instanceof JViewport)
        {
            JScrollPane enclosingScrollPane = (JScrollPane) parent.getParent();

            if (tableHeaderReplacement == null)
            {
                enclosingScrollPane.setColumnHeaderView(getTableHeader());
            }
            else
            {
                enclosingScrollPane.setColumnHeaderView(tableHeaderReplacement);
            }
        }
    }

    public void installRowHeader(final JComponent tableHeaderReplacement)
    {
        if (!showHeader())
        {
            return;
        }

        this.rowHeaderReplacement = tableHeaderReplacement;

        Container parent = getParent();

        if (parent instanceof JViewport)
        {
            JScrollPane enclosingScrollPane = (JScrollPane) parent.getParent();

            if (tableHeaderReplacement == null)
            {
                enclosingScrollPane.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, getTableHeader());
            }
            else
            {
                enclosingScrollPane.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, tableHeaderReplacement);
            }
        }
    }

    public boolean isColumnControlVisible()
    {
        return getColumnControl().isVisible();
    }

    public boolean isSortable()
    {
        return this.sortable;
    }

    /**
     * @param margin int, -1 als default
     */
    public void packAll(final int margin)
    {
        TableUtils.packTable(this, margin);
    }

    /**
     * @param margin int, -1 als default
     */
    public void packColumn(final int columnIndex, final int margin)
    {
        TableUtils.packColumn(this, columnIndex, margin);
    }

    /**
     * @param margin int, -1 als default
     * @param max int, -1 als default
     */
    public void packColumn(final int columnIndex, final int margin, final int max)

    {
        TableUtils.packColumn(this, columnIndex, margin, 20, max);
    }

    @Override
    public Component prepareEditor(final TableCellEditor editor, final int row, final int column)
    {
        // Bei einer Textkomponente den ganzen Inhalt selektieren, damit bei Eingabe der Inhalt
        // komplett überschrieben wird.
        if (!isFocusOwner())
        {
            requestFocus();
        }

        Component component = super.prepareEditor(editor, row, column);

        if (component instanceof JTextComponent c)
        {
            c.selectAll();
        }

        return component;
    }

    public void setColumnControlVisible(final boolean visible)
    {
        getColumnControl().setVisible(visible);
    }

    public void setSortable(final boolean sortable)
    {
        this.sortable = sortable;
    }

    /**
     * Überschrieben, um den TableHeader nicht zu zeigen.
     *
     * @see javax.swing.JTable#configureEnclosingScrollPane()
     */
    @Override
    protected void configureEnclosingScrollPane()
    {
        configureColumnControl();

        if (!showHeader())
        {
            super.configureEnclosingScrollPane();

            return;
        }

        // well, this is a copy of JTable source code, where the TableHeader installation
        // has been removed as it created problems with installHeader method.
        Container p = getParent();

        if (p instanceof JViewport)
        {
            Container gp = p.getParent();

            if (gp instanceof JScrollPane scrollPane)
            {
                Border border = scrollPane.getBorder();

                if ((border == null) || (border instanceof UIResource))
                {
                    scrollPane.setBorder(UIManager.getBorder("Table.scrollPaneBorder"));
                }
            }
        }
    }

    protected JComponent createDefaultColumnControl()
    {
        return new ColumnControlButton(this);
    }

    /**
     * @see javax.swing.JTable#createDefaultColumnModel()
     */
    @Override
    protected TableColumnModel createDefaultColumnModel()
    {
        DefaultExtTableColumnModel columnModel = new DefaultExtTableColumnModel();
        // columnModel.addColumnModelListener(this);

        return columnModel;
    }

    /**
     * @see javax.swing.JTable#initializeLocalVars()
     */
    @Override
    protected void initializeLocalVars()
    {
        super.initializeLocalVars();

        // CellEditor schliessen bei FocusLost
        putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        // Automatisch CellEditor bei KeyStrokes
        putClientProperty("JTable.autoStartsEdit", Boolean.TRUE);

        // Pack Actions
        ActionMap map = getActionMap();
        map.put("pack_all", new AbstractAction("pack_all")
        {
            @Serial
            private static final long serialVersionUID = -5537831177642642702L;

            /**
             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
             */
            @Override
            public void actionPerformed(final ActionEvent e)
            {
                packAll(-1);
            }
        });

        // Sortierung
        TableColumnSorter.add(this);
    }

    /**
     * Gibt an, ob ein FilterHeader verwendet wird.
     *
     * @return <code>true</code> wenn vorhanden, sonst <code>false</code>
     */
    protected boolean showHeader()
    {
        return this.showHeader;
    }
}
