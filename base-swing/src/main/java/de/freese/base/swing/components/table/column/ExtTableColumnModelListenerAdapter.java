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
public class ExtTableColumnModelListenerAdapter implements ExtTableColumnModelListener {
    @Override
    public void columnAdded(final TableColumnModelEvent event) {
        // Empty
    }

    @Override
    public void columnMarginChanged(final ChangeEvent event) {
        // Empty
    }

    @Override
    public void columnMoved(final TableColumnModelEvent event) {
        // Empty
    }

    @Override
    public void columnPropertyChange(final PropertyChangeEvent event) {
        // Empty
    }

    @Override
    public void columnRemoved(final TableColumnModelEvent event) {
        // Empty
    }

    @Override
    public void columnSelectionChanged(final ListSelectionEvent event) {
        // Empty
    }
}
