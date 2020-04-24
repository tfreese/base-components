package de.freese.base.swing.eventlist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * Implementierung einer eigenen Liste, welche bei Änderungen Events feuert.<br>
 * Sämtliche Events werden im EDT gefeuert.<br>
 *
 * @author Thomas Freese
 * @param <E> Konkreter Typ der Listobjekte.
 */
public final class EventList<E> extends ArrayList<E> implements IEventList<E>
{
    /**
     *
     */
    private static final long serialVersionUID = 4109095121391156624L;

    /**
     *
     */
    private Comparator<? super E> comparator = null;

    /**
     * True, wenn der Comparator in Gange ist.
     */
    private boolean isSorting = false;

    /**
     *
     */
    private boolean listenerEnabled = true;

    /**
     *
     */
    private final transient EventListenerList listenerList = new EventListenerList();

    /**
     *
     */
    private Object owner = null;

    /**
     * Erstellt ein neues {@link EventList} Object.
     */
    public EventList()
    {
        super();
    }

    /**
     * @see java.util.ArrayList#add(java.lang.Object)
     */
    @Override
    public boolean add(final E o)
    {
        super.add(o);

        // Sortieren VOR dem Ermitteln der Indices.
        sort();

        // bind(o);

        int index = indexOf(o);

        if (index != -1)
        {
            fireIntervalAdded(index, index);
        }

        return true;
    }

    /**
     * @see java.util.ArrayList#add(int, java.lang.Object)
     */
    @Override
    public void add(final int index, final E element)
    {
        super.add(index, element);

        // Sortieren VOR dem Ermitteln der Indices.
        sort();

        // bind(element);

        int realIndex = indexOf(element);

        if (realIndex != -1)
        {
            fireIntervalAdded(realIndex, realIndex);
        }
    }

    /**
     * @see java.util.ArrayList#addAll(java.util.Collection)
     */
    @Override
    public boolean addAll(final Collection<? extends E> c)
    {
        if ((c == null) || (c.size() == 0))
        {
            return false;
        }

        super.addAll(c);

        // Sortieren VOR dem Ermitteln der Indices.
        sort();

        // bind(c);

        if (size() == 0)
        {
            fireIntervalAdded(0, 0);
        }
        else
        {
            fireIntervalAdded(0, size() - 1);
        }

        return c.size() != 0;
    }

    /**
     * @see java.util.ArrayList#addAll(int, java.util.Collection)
     */
    @Override
    public boolean addAll(final int index, final Collection<? extends E> c)
    {
        if ((c == null) || (c.size() == 0))
        {
            return false;
        }

        super.addAll(index, c);

        // Sortieren VOR dem Ermitteln der Indices.
        sort();

        // bind(c);

        if (size() == 0)
        {
            fireIntervalAdded(0, 0);
        }
        else
        {
            fireIntervalAdded(0, size() - 1);
        }

        return c.size() != 0;
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
     * @see java.util.ArrayList#clear()
     */
    @Override
    public void clear()
    {
        setOwner(null);

        int oldSize = size();

        // unbind(this);

        super.clear();

        if (oldSize > 0)
        {
            fireIntervalRemoved(0, oldSize - 1);
        }
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
     * Benachrichtigt die Listener, dass neue Daten hinzugekommen sind.<br>
     * Alle Listener werden im EDT benachrichtigt.
     *
     * @param startIndex int
     * @param endIndex int
     */
    private void fireIntervalAdded(final int startIndex, final int endIndex)
    {
        if (!isListenerEnabled())
        {
            return;
        }

        int start = Math.min(startIndex, endIndex);
        int end = Math.max(startIndex, endIndex);

        final ListDataListener[] listeners = this.listenerList.getListeners(ListDataListener.class);
        final ListDataEvent event = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, start, end);

        Runnable runnable = () -> {
            for (int i = listeners.length - 1; i >= 0; i--)
            {
                listeners[i].intervalAdded(event);
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
     * Benachrichtigt die Listener, dass Daten weggefallen sind.<br>
     * Alle Listener werden im EDT benachrichtigt.
     *
     * @param startIndex int
     * @param endIndex int
     */
    private void fireIntervalRemoved(final int startIndex, final int endIndex)
    {
        if (!isListenerEnabled())
        {
            return;
        }

        int start = Math.min(startIndex, endIndex);
        int end = Math.max(startIndex, endIndex);

        final ListDataListener[] listeners = this.listenerList.getListeners(ListDataListener.class);
        final ListDataEvent event = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, start, end);

        Runnable runnable = () -> {
            for (int i = listeners.length - 1; i >= 0; i--)
            {
                listeners[i].intervalRemoved(event);
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
     * Liefert den Comparator.
     *
     * @return {@link Comparator}<E>
     */
    private Comparator<? super E> getComparator()
    {
        return this.comparator;
    }

    /**
     * @see de.freese.base.swing.eventlist.IEventList#getOwner()
     */
    @Override
    public Object getOwner()
    {
        return this.owner;
    }

    /**
     * @see de.freese.base.swing.eventlist.IEventList#isListenerEnabled()
     */
    @Override
    public boolean isListenerEnabled()
    {
        return this.listenerEnabled;
    }

    /**
     * @see java.util.ArrayList#remove(int)
     */
    @Override
    public E remove(final int index)
    {
        if ((index == -1) || (index >= size()))
        {
            return null;
        }

        // // Hier muss das Event zuerst gefeuert werden, da es zu einer
        // // ConcurrentModificationException
        // // kommen koennte, wenn die Listener auf das geloeschte Objekt der Liste
        // // zugreifen wuerden.
        // firePreRemove(index, index);

        E object = super.remove(index);

        fireIntervalRemoved(index, index);

        // unbind(object);

        return object;
    }

    /**
     * @see java.util.ArrayList#remove(java.lang.Object)
     */
    @Override
    public boolean remove(final Object o)
    {
        if (o == null)
        {
            return false;
        }

        int index = indexOf(o);

        if (index == -1)
        {
            return false;
        }

        // unbind(o);

        // // Hier muss das Event zuerst gefeuert werden, da es zu einer
        // // ConcurrentModificationException
        // // kommen koennte, wenn die Listener auf das geloeschte Objekt der Liste
        // // zugreifen wuerden.
        // firePreRemove(index, index);

        boolean value = super.remove(o);

        fireIntervalRemoved(index, index);

        return value;
    }

    /**
     * @see java.util.AbstractCollection#removeAll(java.util.Collection)
     */
    @Override
    public boolean removeAll(final Collection<?> c)
    {
        // Ruft intern remove(int) auf
        return super.removeAll(c);
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
        // Nix zu tun
    }

    /**
     * @see java.util.ArrayList#set(int, java.lang.Object)
     */
    @Override
    public E set(final int index, final E element)
    {
        E oldObject = super.set(index, element);

        // unbind(oldObject);
        // bind(element);

        if (!this.isSorting)
        {
            // Events nur feuern, wenn nicht sortiert wird.
            // Dies geschied nach dem sortieren.
            fireContentsChanged(index, index);
        }

        return oldObject;
    }

    /**
     * @see de.freese.base.swing.eventlist.IEventList#setComparator(java.util.Comparator)
     */
    @Override
    public void setComparator(final Comparator<? super E> comparator)
    {
        this.comparator = comparator;

        if (this.comparator != null)
        {
            update();
        }
    }

    /**
     * @see de.freese.base.swing.eventlist.IEventList#setListenerEnabled(boolean)
     */
    @Override
    public void setListenerEnabled(final boolean listenerEnabled)
    {
        this.listenerEnabled = listenerEnabled;
    }

    /**
     * @see de.freese.base.swing.eventlist.IEventList#setOwner(java.lang.Object)
     */
    @Override
    public void setOwner(final Object owner)
    {
        this.owner = owner;
    }

    /**
     * Sortiert die Liste, wenn ein {@link Comparator} vorhanden ist.
     */
    private void sort()
    {
        if (getComparator() == null)
        {
            return;
        }

        this.isSorting = true;
        Collections.sort(this, getComparator());
        this.isSorting = false;
    }

    /**
     * @see de.freese.base.swing.eventlist.IEventList#update()
     */
    @Override
    public void update()
    {
        sort();
        fireContentsChanged(0, size() - 1);
    }
}
