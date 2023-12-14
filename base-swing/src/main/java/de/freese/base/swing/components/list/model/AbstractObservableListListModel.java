// Created: 12.01.2018
package de.freese.base.swing.components.list.model;

import java.util.Objects;

import javax.swing.ListModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * @author Thomas Freese
 */
public abstract class AbstractObservableListListModel<T> implements ListModel<T>, ListChangeListener<T> {
    private final EventListenerList eventListenerList = new EventListenerList();
    private final ObservableList<T> list;

    protected AbstractObservableListListModel(final ObservableList<T> list) {
        super();

        this.list = Objects.requireNonNull(list, "list required");
        this.list.addListener(this);
    }

    @Override
    public synchronized void addListDataListener(final ListDataListener listener) {
        this.eventListenerList.add(ListDataListener.class, listener);
    }

    @Override
    public T getElementAt(final int index) {
        return getList().get(index);
    }

    @Override
    public int getSize() {
        return getList().size();
    }

    @Override
    public void onChanged(final Change<? extends T> change) {
        while (change.next()) {
            if (change.wasAdded()) {
                final int firstRow = change.getFrom();
                final int lastRow = change.getTo();

                fireIntervalAdded(change.getList(), firstRow, lastRow);
            }
            else if (change.wasRemoved()) {
                final int firstRow = change.getFrom();
                final int lastRow = change.getTo();

                fireIntervalRemoved(change.getList(), firstRow, lastRow);
            }
            else {
                fireContentsChanged(change.getList(), 0, getSize());

                return;
            }
        }
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
        final Object[] listeners = this.eventListenerList.getListenerList();
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
        final Object[] listeners = this.eventListenerList.getListenerList();
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
        final Object[] listeners = this.eventListenerList.getListenerList();
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

    protected ObservableList<T> getList() {
        return this.list;
    }
}
