package de.freese.base.swing.eventlist;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import de.freese.base.swing.filter.Filter;
import de.freese.base.swing.filter.FilterCondition;

/**
 * Implementierung einer Liste, welche eine {@link IEventList} aufnimmt, auf deren Aenderungen reagiert und die Inhalte filtert.<br>
 * Saemtliche Events werden im EDT gefeuert.
 *
 * @author Thomas Freese
 *
 * @param <E> Type
 */
public class FilterableEventList<E> implements IEventList<E>, PropertyChangeListener
{
    /**
     * Listener auf der {@link IEventList}.
     *
     * @author Thomas Freese
     */
    private class DelegateListener implements ListDataListener
    {
        /**
         * @see javax.swing.event.ListDataListener#contentsChanged(javax.swing.event.ListDataEvent)
         */
        @Override
        public void contentsChanged(final ListDataEvent e)
        {
            filter();
        }

        /**
         * @see javax.swing.event.ListDataListener#intervalAdded(javax.swing.event.ListDataEvent)
         */
        @Override
        public void intervalAdded(final ListDataEvent e)
        {
            filter();
        }

        /**
         * @see javax.swing.event.ListDataListener#intervalRemoved(javax.swing.event.ListDataEvent)
         */
        @Override
        public void intervalRemoved(final ListDataEvent e)
        {
            filter();
        }
    }

    /**
     *
     */
    private final IEventList<E> delegate;
    /**
     *
     */
    private FilterCondition filter;
    /**
     *
     */
    private final List<E> filteredList;
    /**
     *
     */
    private final EventListenerList listenerList = new EventListenerList();

    /**
     * Erstellt ein neues {@link FilterableEventList} Object.
     *
     * @param delegate {@link IEventList}
     */
    public FilterableEventList(final IEventList<E> delegate)
    {
        super();

        this.delegate = delegate;
        this.filteredList = new ArrayList<>();

        this.delegate.addListDataListener(new DelegateListener());
        // this.filteredList.addAll(delegate);
    }

    /**
     * @see java.util.List#add(java.lang.Object)
     */
    @Override
    public boolean add(final E e)
    {
        return this.delegate.add(e);
    }

    /**
     * @see java.util.List#add(int, java.lang.Object)
     */
    @Override
    public void add(final int index, final E element)
    {
        this.delegate.add(index, element);
    }

    /**
     * @see java.util.List#addAll(java.util.Collection)
     */
    @Override
    public boolean addAll(final Collection<? extends E> c)
    {
        return this.delegate.addAll(c);
    }

    /**
     * @see java.util.List#addAll(int, java.util.Collection)
     */
    @Override
    public boolean addAll(final int index, final Collection<? extends E> c)
    {
        return this.delegate.addAll(index, c);
    }

    /**
     * @see de.freese.base.swing.eventlist.IEventList#addListDataListener(javax.swing.event.ListDataListener)
     */
    @Override
    public void addListDataListener(final ListDataListener listener)
    {
        this.listenerList.add(ListDataListener.class, listener);
    }

    /**
     * @see java.util.List#clear()
     */
    @Override
    public void clear()
    {
        this.delegate.clear();
    }

    /**
     * @see java.util.List#contains(java.lang.Object)
     */
    @Override
    public boolean contains(final Object o)
    {
        return this.filteredList.contains(o);
    }

    /**
     * @see java.util.List#containsAll(java.util.Collection)
     */
    @Override
    public boolean containsAll(final Collection<?> c)
    {
        return this.filteredList.containsAll(c);
    }

    /**
     * Filter die gewrappte {@link IEventList}.
     */
    private void filter()
    {
        reset();

        if (getFilter() != null)
        {
            for (Iterator<E> iterator = this.filteredList.iterator(); iterator.hasNext();)
            {
                E object = iterator.next();

                if (!getFilter().test(object))
                {
                    iterator.remove();
                }
            }
        }

        fireContentsChanged(0, size() - 1);
    }

    /**
     * Benachrichtigt die Listener, dass sich die Struktur geaendert hat.<br>
     * Alle Listener werden im EDT benachrichtigt.
     *
     * @param startIndex int
     * @param endIndex int
     */
    private void fireContentsChanged(final int startIndex, final int endIndex)
    {
        if (!isListenerEnabled())
        {
            return;
        }

        int start = Math.min(startIndex, endIndex);
        int end = Math.max(startIndex, endIndex);

        final ListDataListener[] listeners = this.listenerList.getListeners(ListDataListener.class);
        final ListDataEvent event = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, start, end);

        Runnable runnable = () -> {
            for (int i = listeners.length - 1; i >= 0; i--)
            {
                listeners[i].contentsChanged(event);
            }
        };

        if (!SwingUtilities.isEventDispatchThread())
        {
            SwingUtilities.invokeLater(runnable);
        }
        else
        {
            runnable.run();
        }
    }

    /**
     * @see java.util.List#get(int)
     */
    @Override
    public E get(final int index)
    {
        return this.filteredList.get(index);
    }

    /**
     * @return {@link FilterCondition}
     */
    public FilterCondition getFilter()
    {
        return this.filter;
    }

    /**
     * @see de.freese.base.swing.eventlist.IEventList#getOwner()
     */
    @Override
    public Object getOwner()
    {
        return this.delegate.getOwner();
    }

    /**
     * @see java.util.List#indexOf(java.lang.Object)
     */
    @Override
    public int indexOf(final Object o)
    {
        return this.filteredList.indexOf(o);
    }

    /**
     * @see java.util.List#isEmpty()
     */
    @Override
    public boolean isEmpty()
    {
        return size() == 0;
    }

    /**
     * @see de.freese.base.swing.eventlist.IEventList#isListenerEnabled()
     */
    @Override
    public boolean isListenerEnabled()
    {
        return this.delegate.isListenerEnabled();
    }

    /**
     * @see java.util.List#iterator()
     */
    @Override
    public Iterator<E> iterator()
    {
        return this.filteredList.iterator();
    }

    /**
     * @see java.util.List#lastIndexOf(java.lang.Object)
     */
    @Override
    public int lastIndexOf(final Object o)
    {
        return this.filteredList.lastIndexOf(o);
    }

    /**
     * @see java.util.List#listIterator()
     */
    @Override
    public ListIterator<E> listIterator()
    {
        return this.filteredList.listIterator();
    }

    /**
     * @see java.util.List#listIterator(int)
     */
    @Override
    public ListIterator<E> listIterator(final int index)
    {
        return this.filteredList.listIterator(index);
    }

    /**
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    @Override
    public void propertyChange(final PropertyChangeEvent evt)
    {
        // Falls sich die Bedingungen innerhalb des Filters geaendert haben
        if (Filter.FILTER_CHANGED.equals(evt.getPropertyName()))
        {
            filter();
        }
    }

    /**
     * @see java.util.List#remove(int)
     */
    @Override
    public E remove(final int index)
    {
        return this.delegate.remove(index);
    }

    /**
     * @see java.util.List#remove(java.lang.Object)
     */
    @Override
    public boolean remove(final Object o)
    {
        return this.delegate.remove(o);
    }

    /**
     * @see java.util.List#removeAll(java.util.Collection)
     */
    @Override
    public boolean removeAll(final Collection<?> c)
    {
        return this.delegate.removeAll(c);
    }

    /**
     * @see de.freese.base.swing.eventlist.IEventList#removeListDataListener(javax.swing.event.ListDataListener)
     */
    @Override
    public void removeListDataListener(final ListDataListener listener)
    {
        this.listenerList.remove(ListDataListener.class, listener);
    }

    /**
     * @see de.freese.base.swing.eventlist.IEventList#reset()
     */
    @Override
    public void reset()
    {
        this.filteredList.clear();
        this.filteredList.addAll(this.delegate);
    }

    /**
     * @see java.util.List#retainAll(java.util.Collection)
     */
    @Override
    public boolean retainAll(final Collection<?> c)
    {
        return this.filteredList.retainAll(c);
    }

    /**
     * @see java.util.List#set(int, java.lang.Object)
     */
    @Override
    public E set(final int index, final E element)
    {
        return this.delegate.set(index, element);
    }

    /**
     * @see de.freese.base.swing.eventlist.IEventList#setComparator(java.util.Comparator)
     */
    @Override
    public void setComparator(final Comparator<? super E> comparator)
    {
        this.delegate.setComparator(comparator);
    }

    /**
     * Setzt den Filter fuer die FilterableEventList.
     *
     * @param filter {@link FilterCondition}
     */
    public void setFilter(final FilterCondition filter)
    {
        if (this.filter != null)
        {
            // Zuerst sich als Listener aus dem alten Filter austragen
            this.filter.getPropertyChangeSupport().removePropertyChangeListener(this);
        }

        this.filter = filter;

        if (this.filter != null)
        {
            // Dann sich als Listener bei dem neuen registrieren
            this.filter.getPropertyChangeSupport().addPropertyChangeListener(this);
        }

        filter();
    }

    /**
     * @see de.freese.base.swing.eventlist.IEventList#setListenerEnabled(boolean)
     */
    @Override
    public void setListenerEnabled(final boolean listenerEnabled)
    {
        this.delegate.setListenerEnabled(listenerEnabled);
    }

    /**
     * @see de.freese.base.swing.eventlist.IEventList#setOwner(java.lang.Object)
     */
    @Override
    public void setOwner(final Object owner)
    {
        this.delegate.setOwner(owner);
    }

    /**
     * @see java.util.List#size()
     */
    @Override
    public int size()
    {
        return this.filteredList.size();
    }

    /**
     * @see java.util.List#subList(int, int)
     */
    @Override
    public List<E> subList(final int fromIndex, final int toIndex)
    {
        return this.filteredList.subList(fromIndex, toIndex);
    }

    /**
     * @see java.util.List#toArray()
     */
    @Override
    public Object[] toArray()
    {
        return this.filteredList.toArray();
    }

    /**
     * @see java.util.List#toArray(T[])
     */
    @Override
    public <T> T[] toArray(final T[] a)
    {
        return this.filteredList.toArray(a);
    }

    /**
     * @see de.freese.base.swing.eventlist.IEventList#update()
     */
    @Override
    public void update()
    {
        this.delegate.update();
    }
}
