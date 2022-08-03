package de.freese.base.swing.components.table;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import de.freese.base.swing.eventlist.EventList;

/**
 * TableModel das intern eine {@link EventList} verwendet.
 *
 * @param <T> Konkreter Typ der List-Objekte.
 *
 * @author Thomas Freese
 */
public abstract class AbstractEventListTableModel<T> extends AbstractListTableModel<T>
{
    /**
     *
     */
    private static final long serialVersionUID = -4124337499231623139L;

    /**
     * Listener auf der {@link EventList}.
     *
     * @author Thomas Freese
     */
    private class EventListListener implements ListDataListener
    {
        /**
         * @see javax.swing.event.ListDataListener#contentsChanged(javax.swing.event.ListDataEvent)
         */
        @Override
        public void contentsChanged(final ListDataEvent e)
        {
            // int firstRow = e.getIndex0();
            // int lastRow = e.getIndex1();
            //
            // fireTableRowsUpdated(firstRow, lastRow);
            // fireTableRowsUpdated(0, Integer.MAX_VALUE);

            fireTableDataChanged();
        }

        /**
         * @see javax.swing.event.ListDataListener#intervalAdded(javax.swing.event.ListDataEvent)
         */
        @Override
        public void intervalAdded(final ListDataEvent e)
        {
            int firstRow = e.getIndex0();
            int lastRow = e.getIndex1();

            fireTableRowsInserted(firstRow, lastRow);
        }

        /**
         * @see javax.swing.event.ListDataListener#intervalRemoved(javax.swing.event.ListDataEvent)
         */
        @Override
        public void intervalRemoved(final ListDataEvent e)
        {
            int firstRow = e.getIndex0();
            int lastRow = e.getIndex1();

            fireTableRowsDeleted(firstRow, lastRow);
        }
    }

    /**
     * Erstellt ein neues {@link AbstractEventListTableModel} Objekt.
     *
     * @param columnCount int
     * @param list {@link EventList}
     */
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

        // Die Events werden über die EventList gefeuert.
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
