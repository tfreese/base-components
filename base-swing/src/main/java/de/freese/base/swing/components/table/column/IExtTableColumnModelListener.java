package de.freese.base.swing.components.table.column;

import java.beans.PropertyChangeEvent;
import javax.swing.event.TableColumnModelListener;

/**
 * Erweiterte {@link TableColumnModelListener}.
 *
 * @author Thomas Freese
 */
public interface IExtTableColumnModelListener extends TableColumnModelListener
{
    /**
     * Nimmt das Event auf.
     *
     * @param event {@link PropertyChangeEvent}
     */
    public void columnPropertyChange(PropertyChangeEvent event);
}
