package de.freese.base.swing.components.table;

import java.io.Serial;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import de.freese.base.swing.eventlist.EventList;

/**
 * @author Thomas Freese
 */
public abstract class AbstractEventListTableModel<T> extends AbstractListTableModel<T>
{
    @Serial
    private static final long serialVersionUID = -4124337499231623139L;

    /**
     * @author Thomas Freese
     */
    private class EventListListener implements ListDataListener
    {
        /**
         * @see javax.swing.event.ListDataListener#contentsChanged(javax.swing.event.ListDataEvent)
         */
        @Override
        public void contentsChanged(final ListDataEvent event)
        {
            // int firstRow = event.getIndex0();
            // int lastRow = event.getIndex1();
            //
            // fireTableRowsUpdated(firstRow, lastRow);
            // fireTableRowsUpdated(0, Integer.MAX_VALUE);

            fireTableDataChanged();
        }

        /**
         * @see javax.swing.event.ListDataListener#intervalAdded(javax.swing.event.ListDataEvent)
         */
        @Override
        public void intervalAdded(final ListDataEvent event)
        {
            int firstRow = event.getIndex0();
            int lastRow = event.getIndex1();

            fireTableRowsInserted(firstRow, lastRow);
        }

        /**
         * @see javax.swing.event.ListDataListener#intervalRemoved(javax.swing.event.ListDataEvent)
         */
        @Override
        public void intervalRemoved(final ListDataEvent event)
        {
            int firstRow = event.getIndex0();
            int lastRow = event.getIndex1();

            fireTableRowsDeleted(firstRow, lastRow);
        }
    }

    protected AbstractEventListTableModel(final int columnCount, final EventList<T> list)
    {
        super(columnCount, list);

        list.addListDataListener(new EventListListener());
    }

    /**
     * @see de.freese.base.swing.components.table.AbstractListTableModel#refresh()
     */
    @Override
    public void refresh()
    {
        getList().update();

        // Events will fired by the EventList.
    }

    /**
     * @see de.freese.base.swing.components.table.AbstractListTableModel#getList()
     */
    @Override
    protected EventList<T> getList()
    {
        return (EventList<T>) super.getList();
    }
}
