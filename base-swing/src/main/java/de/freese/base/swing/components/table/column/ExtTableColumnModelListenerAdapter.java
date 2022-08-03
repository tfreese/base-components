package de.freese.base.swing.components.table.column;

import java.beans.PropertyChangeEvent;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;

/**
 * Listener Adapter.
 *
 * @author Thomas Freese
 */
public class ExtTableColumnModelListenerAdapter implements ExtTableColumnModelListener
{
    /**
     * @see javax.swing.event.TableColumnModelListener#columnAdded(javax.swing.event.TableColumnModelEvent)
     */
    @Override
    public void columnAdded(final TableColumnModelEvent e)
    {
        // Empty
    }

    /**
     * @see javax.swing.event.TableColumnModelListener#columnMarginChanged(javax.swing.event.ChangeEvent)
     */
    @Override
    public void columnMarginChanged(final ChangeEvent e)
    {
        // Empty
    }

    /**
     * @see javax.swing.event.TableColumnModelListener#columnMoved(javax.swing.event.TableColumnModelEvent)
     */
    @Override
    public void columnMoved(final TableColumnModelEvent e)
    {
        // Empty
    }

    /**
     * @see ExtTableColumnModelListener#columnPropertyChange(java.beans.PropertyChangeEvent)
     */
    @Override
    public void columnPropertyChange(final PropertyChangeEvent event)
    {
        // Empty
    }

    /**
     * @see javax.swing.event.TableColumnModelListener#columnRemoved(javax.swing.event.TableColumnModelEvent)
     */
    @Override
    public void columnRemoved(final TableColumnModelEvent e)
    {
        // Empty
    }

    /**
     * @see javax.swing.event.TableColumnModelListener#columnSelectionChanged(javax.swing.event.ListSelectionEvent)
     */
    @Override
    public void columnSelectionChanged(final ListSelectionEvent e)
    {
        // Empty
    }
}
