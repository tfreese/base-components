// Created: 12.01.2018
package de.freese.base.swing.components.list.model;

import java.util.Objects;
import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * Basis {@link ListModel}, welches die Verwendung einer {@link ObservableList} ermoeglicht.
 *
 * @author Thomas Freese
 * @param <T> Typ der Entity
 */
public abstract class AbstractObservableListListModel<T> implements ListModel<T>, ListChangeListener<T>
{
    /**
     *
     */
    private final EventListenerList eventListenerList = new EventListenerList();

    /**
    *
    */
    private final ObservableList<T> list;

    /**
     * Erzeugt eine neue Instanz von {@link AbstractObservableListListModel}.
     *
     * @param list {@link ObservableList}
     */
    protected AbstractObservableListListModel(final ObservableList<T> list)
    {
        super();

        this.list = Objects.requireNonNull(list, "list required");
        this.list.addListener(this);
    }

    /**
     * @see javax.swing.ListModel#addListDataListener(javax.swing.event.ListDataListener)
     */
    @Override
    public synchronized void addListDataListener(final ListDataListener listener)
    {
        this.eventListenerList.add(ListDataListener.class, listener);
    }

    /**
     * <code>AbstractListModel</code> subclasses must call this method <b>after</b> one or more elements of the list change. The changed elements are specified
     * by the closed interval index0, index1 -- the endpoints are included. Note that index0 need not be less than or equal to index1.
     *
     * @param source the <code>ListModel</code> that changed, typically "this"
     * @param index0 one end of the new interval
     * @param index1 the other end of the new interval
     * @see EventListenerList
     * @see DefaultListModel
     */
    protected void fireContentsChanged(final Object source, final int index0, final int index1)
    {
        Object[] listeners = this.eventListenerList.getListenerList();
        ListDataEvent e = null;

        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == ListDataListener.class)
            {
                if (e == null)
                {
                    e = new ListDataEvent(source, ListDataEvent.CONTENTS_CHANGED, index0, index1);
                }

                ((ListDataListener) listeners[i + 1]).contentsChanged(e);
            }
        }
    }

    /**
     * <code>AbstractListModel</code> subclasses must call this method <b>after</b> one or more elements are added to the model. The new elements are specified
     * by a closed interval index0, index1 -- the enpoints are included. Note that index0 need not be less than or equal to index1.
     *
     * @param source the <code>ListModel</code> that changed, typically "this"
     * @param index0 one end of the new interval
     * @param index1 the other end of the new interval
     * @see EventListenerList
     * @see DefaultListModel
     */
    protected void fireIntervalAdded(final Object source, final int index0, final int index1)
    {
        Object[] listeners = this.eventListenerList.getListenerList();
        ListDataEvent e = null;

        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == ListDataListener.class)
            {
                if (e == null)
                {
                    e = new ListDataEvent(source, ListDataEvent.INTERVAL_ADDED, index0, index1);
                }

                ((ListDataListener) listeners[i + 1]).intervalAdded(e);
            }
        }
    }

    /**
     * <code>AbstractListModel</code> subclasses must call this method <b>after</b> one or more elements are removed from the model. The new elements are
     * specified by a closed interval index0, index1, i.e. the range that includes both index0 and index1. Note that index0 need not be less than or equal to
     * index1.
     *
     * @param source the ListModel that changed, typically "this"
     * @param index0 one end of the new interval
     * @param index1 the other end of the new interval
     * @see EventListenerList
     * @see DefaultListModel
     */
    protected void fireIntervalRemoved(final Object source, final int index0, final int index1)
    {
        Object[] listeners = this.eventListenerList.getListenerList();
        ListDataEvent e = null;

        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == ListDataListener.class)
            {
                if (e == null)
                {
                    e = new ListDataEvent(source, ListDataEvent.INTERVAL_REMOVED, index0, index1);
                }

                ((ListDataListener) listeners[i + 1]).intervalRemoved(e);
            }
        }
    }

    /**
     * @see javax.swing.ListModel#getElementAt(int)
     */
    @Override
    public T getElementAt(final int index)
    {
        return getList().get(index);
    }

    /**
     * Liefert die Liste des ListModels.
     *
     * @return {@link ObservableList}
     */
    protected ObservableList<T> getList()
    {
        return this.list;
    }

    /**
     * @see javax.swing.ListModel#getSize()
     */
    @Override
    public int getSize()
    {
        return getList().size();
    }

    /**
     * @see javafx.collections.ListChangeListener#onChanged(javafx.collections.ListChangeListener.Change)
     */
    @Override
    public void onChanged(final Change<? extends T> change)
    {
        while (change.next())
        {
            if (change.wasAdded())
            {
                int firstRow = change.getFrom();
                int lastRow = change.getTo();

                fireIntervalAdded(change.getList(), firstRow, lastRow);
            }
            else if (change.wasRemoved())
            {
                int firstRow = change.getFrom();
                int lastRow = change.getTo();

                fireIntervalRemoved(change.getList(), firstRow, lastRow);
            }
            else
            {
                fireContentsChanged(change.getList(), 0, getSize());

                // 1x reicht
                return;
            }
        }
    }

    /**
     * @see javax.swing.ListModel#removeListDataListener(javax.swing.event.ListDataListener)
     */
    @Override
    public synchronized void removeListDataListener(final ListDataListener listener)
    {
        this.eventListenerList.add(ListDataListener.class, listener);
    }
}
