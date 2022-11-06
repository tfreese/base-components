package de.freese.base.swing.components.table.column;

import java.beans.PropertyChangeEvent;

import javax.swing.event.TableColumnModelListener;

/**
 * Erweiterte {@link TableColumnModelListener}.
 *
 * @author Thomas Freese
 */
public interface ExtTableColumnModelListener extends TableColumnModelListener
{
    void columnPropertyChange(PropertyChangeEvent event);
}
