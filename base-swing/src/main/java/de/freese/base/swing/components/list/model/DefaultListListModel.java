package de.freese.base.swing.components.list.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * {@link ListModel}, welches die Verwendung einer {@link List} ermöglicht.
 *
 * @param <T> Type
 *
 * @author Thomas Freese
 */
public class DefaultListListModel<T> implements ListModel<T>, Serializable
{
    /**
     *
     */
    private static final long serialVersionUID = 8362504657702002619L;
    /**
     *
     */
    private final EventListenerList eventListenerList = new EventListenerList();
    /**
     *
     */
    private final List<T> list;

    /**
     * Creates a new {@link DefaultListListModel} object.
     */
    public DefaultListListModel()
    {
        this(new ArrayList<>());
    }

    /**
     * Creates a new {@link DefaultListListModel} object.
     *
     * @param list {@link List}
     */
    public DefaultListListModel(final List<T> list)
    {
        super();

        this.list = Objects.requireNonNull(list, "list required");
    }

    /**
     * @param object Object
     */
    public void add(final T object)
    {
        getList().add(object);

        fireIntervalAdded(this, getList().size() - 1, getList().size() - 1);
    }

    /**
     * @param objects {@link Collection}
     */
    public void addAll(final Collection<T> objects)
    {
        int sizeOld = getList().size();

        getList().addAll(objects);

        fireIntervalAdded(this, sizeOld, getList().size() - 1);
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
     *
     */
    public void clear()
    {
        getList().clear();

        refresh();
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
     * Liefert ein Objekt für einen Index einer Zeile.
     *
     * @param rowIndex int
     *
     * @return Object
     */
    public T getObjectAt(final int rowIndex)
    {
        return getList().get(rowIndex);
    }

    /**
     * Liefert den ZeilenIndex für ein Objekt zurück.
     *
     * @param object Object
     *
     * @return int
     */
    public int getRowOf(final T object)
    {
        return getList().indexOf(object);
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
     * Liefert den {@link Stream} des TableModels.
     *
     * @return {@link Stream}
     */
    public Stream<T> getStream()
    {
        return getList().stream();
    }

    /**
     * Feuert das ContentsChanged Event.
     */
    public void refresh()
    {
        fireContentsChanged(this, 0, getSize() - 1);
    }

    /**
     * @see javax.swing.ListModel#removeListDataListener(javax.swing.event.ListDataListener)
     */
    @Override
    public synchronized void removeListDataListener(final ListDataListener listener)
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
     *
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
     * by a closed interval index0, index1 -- the endpoints are included. Note that index0 need not be less than or equal to index1.
     *
     * @param source the <code>ListModel</code> that changed, typically "this"
     * @param index0 one end of the new interval
     * @param index1 the other end of the new interval
     *
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
     *
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
     * @return {@link List}<T>
     */
    protected List<T> getList()
    {
        return this.list;
    }
}
