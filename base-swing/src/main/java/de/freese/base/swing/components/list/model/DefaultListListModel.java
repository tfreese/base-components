package de.freese.base.swing.components.list.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import javax.swing.ListModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * {@link ListModel} that works with a {@link List}.
 *
 * @author Thomas Freese
 */
public class DefaultListListModel<T> implements ListModel<T>, Serializable {
    @Serial
    private static final long serialVersionUID = 8362504657702002619L;

    private final EventListenerList eventListenerList = new EventListenerList();

    private final transient List<T> list;

    public DefaultListListModel() {
        this(new ArrayList<>());
    }

    public DefaultListListModel(final List<T> list) {
        super();

        this.list = Objects.requireNonNull(list, "list required");
    }

    public void add(final T object) {
        if (object == null) {
            return;
        }

        getList().add(object);

        fireIntervalAdded(this, getList().size() - 1, getList().size() - 1);
    }

    public void addAll(final Collection<T> objects) {
        if (objects.isEmpty()) {
            return;
        }

        int sizeOld = getList().size();

        getList().addAll(objects);

        fireIntervalAdded(this, sizeOld, getList().size() - 1);
    }

    @Override
    public synchronized void addListDataListener(final ListDataListener listener) {
        this.eventListenerList.add(ListDataListener.class, listener);
    }

    public void clear() {
        getList().clear();

        refresh();
    }

    public boolean contains(final T object) {
        return getList().contains(object);
    }

    @Override
    public T getElementAt(final int index) {
        return getList().get(index);
    }

    public T getObjectAt(final int rowIndex) {
        return getList().get(rowIndex);
    }

    public int getRowOf(final T object) {
        return getList().indexOf(object);
    }

    @Override
    public int getSize() {
        return getList().size();
    }

    public Stream<T> getStream() {
        return getList().stream();
    }

    /**
     * Fires the ContentsChanged Event.
     */
    public void refresh() {
        fireContentsChanged(this, 0, getSize() - 1);
    }

    public void remove(final T object) {
        int index = getList().indexOf(object);

        if (index < 0) {
            return;
        }

        getList().remove(object);

        fireIntervalRemoved(this, index, index);
    }

    @Override
    public synchronized void removeListDataListener(final ListDataListener listener) {
        this.eventListenerList.add(ListDataListener.class, listener);
    }

    /**
     * <code>AbstractListModel</code> subclasses must call this method <b>after</b> one or more elements of the list change. The changed elements are specified
     * by the closed interval index0, index1 -- the endpoints are included. Note that index0 need not be less than or equal to index1.
     *
     * @param source the <code>ListModel</code> that changed, typically "this"
     * @param index0 one end of the new interval
     * @param index1 the other end of the new interval
     */
    protected void fireContentsChanged(final Object source, final int index0, final int index1) {
        Object[] listeners = this.eventListenerList.getListenerList();
        ListDataEvent event = null;

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ListDataListener.class) {
                if (event == null) {
                    event = new ListDataEvent(source, ListDataEvent.CONTENTS_CHANGED, index0, index1);
                }

                ((ListDataListener) listeners[i + 1]).contentsChanged(event);
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
     */
    protected void fireIntervalAdded(final Object source, final int index0, final int index1) {
        Object[] listeners = this.eventListenerList.getListenerList();
        ListDataEvent event = null;

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ListDataListener.class) {
                if (event == null) {
                    event = new ListDataEvent(source, ListDataEvent.INTERVAL_ADDED, index0, index1);
                }

                ((ListDataListener) listeners[i + 1]).intervalAdded(event);
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
     */
    protected void fireIntervalRemoved(final Object source, final int index0, final int index1) {
        Object[] listeners = this.eventListenerList.getListenerList();
        ListDataEvent event = null;

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ListDataListener.class) {
                if (event == null) {
                    event = new ListDataEvent(source, ListDataEvent.INTERVAL_REMOVED, index0, index1);
                }

                ((ListDataListener) listeners[i + 1]).intervalRemoved(event);
            }
        }
    }

    protected List<T> getList() {
        return this.list;
    }
}
