package de.freese.base.swing.components.table;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;

/**
 * Wenn das ColumnModel eine Aenderung der Columns meldet, wird die Groesse des Viewports automatisch angepasst.
 *
 * @author Thomas Freese
 */
public class UpdateViewportSizeColumnListener implements TableColumnModelListener
{
    /**
     *
     */
    private JTable table;

    /**
     * Creates a new {@link UpdateViewportSizeColumnListener} object.
     *
     * @param table {@link JTable}
     */
    public UpdateViewportSizeColumnListener(final JTable table)
    {
        super();

        if (table == null)
        {
            throw new IllegalArgumentException("Tabelle ist NULL !!!");
        }

        this.table = table;
    }

    /**
     * @see javax.swing.event.TableColumnModelListener#columnAdded(javax.swing.event.TableColumnModelEvent)
     */
    @Override
    public void columnAdded(final TableColumnModelEvent e)
    {
        updateViewportSize();
    }

    /**
     * @see javax.swing.event.TableColumnModelListener#columnMarginChanged(javax.swing.event.ChangeEvent)
     */
    @Override
    public void columnMarginChanged(final ChangeEvent e)
    {
        updateViewportSize();
    }

    /**
     * @see javax.swing.event.TableColumnModelListener#columnMoved(javax.swing.event.TableColumnModelEvent)
     */
    @Override
    public void columnMoved(final TableColumnModelEvent e)
    {
        updateViewportSize();
    }

    /**
     * @see javax.swing.event.TableColumnModelListener#columnRemoved(javax.swing.event.TableColumnModelEvent)
     */
    @Override
    public void columnRemoved(final TableColumnModelEvent e)
    {
        updateViewportSize();
    }

    /**
     * @see javax.swing.event.TableColumnModelListener#columnSelectionChanged(javax.swing.event.ListSelectionEvent)
     */
    @Override
    public void columnSelectionChanged(final ListSelectionEvent e)
    {
        // NOOP
    }

    /**
     * Anpassung der Groesse des Viewports and die Groesse der Tabelle.
     */
    protected void updateViewportSize()
    {
        JViewport view = (JViewport) this.table.getParent();
        JScrollPane pane = (JScrollPane) view.getParent();

        pane.revalidate();
        view.setPreferredSize(this.table.getPreferredSize());
    }
}
