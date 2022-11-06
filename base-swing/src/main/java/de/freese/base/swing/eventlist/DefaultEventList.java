package de.freese.base.swing.eventlist;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * Implementierung einer eigenen Liste, welche bei Änderungen Events feuert.<br>
 * Sämtliche Events werden im EDT gefeuert.<br>
 *
 * @param <E> Type
 *
 * @author Thomas Freese
 */
public final class DefaultEventList<E> extends ArrayList<E> implements EventList<E>
{
    @Serial
    private static final long serialVersionUID = 4109095121391156624L;

    private final transient EventListenerList listenerList = new EventListenerList();

    private transient Comparator<? super E> comparator;

    private boolean isSorting;

    private boolean listenerEnabled = true;

    private transient Object owner;

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
     * @see EventList#addListDataListener(javax.swing.event.ListDataListener)
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
     * @see EventList#getOwner()
     */
    @Override
    public Object getOwner()
    {
        return this.owner;
    }

    /**
     * @see EventList#isListenerEnabled()
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
        // // kommen könnte, wenn die Listener auf das gelöschte Objekt der Liste
        // // zugreifen würden.
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
        // // kommen könnte, wenn die Listener auf das gelöschte Objekt der Liste
        // // zugreifen würden.
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
     * @see EventList#removeListDataListener(javax.swing.event.ListDataListener)
     */
    @Override
    public void removeListDataListener(final ListDataListener listener)
    {
        this.listenerList.remove(ListDataListener.class, listener);
    }

    /**
     * @see EventList#reset()
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
            // Dies geschieht nach dem Sortieren.
            fireContentsChanged(index, index);
        }

        return oldObject;
    }

    /**
     * @see EventList#setComparator(java.util.Comparator)
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
     * @see EventList#setListenerEnabled(boolean)
     */
    @Override
    public void setListenerEnabled(final boolean listenerEnabled)
    {
        this.listenerEnabled = listenerEnabled;
    }

    /**
     * @see EventList#setOwner(java.lang.Object)
     */
    @Override
    public void setOwner(final Object owner)
    {
        this.owner = owner;
    }

    /**
     * @see EventList#update()
     */
    @Override
    public void update()
    {
        sort();
        fireContentsChanged(0, size() - 1);
    }

    /**
     * Benachrichtigt die Listener, dass sich die Struktur geändert hat.<br>
     * Alle Listener werden im EDT benachrichtigt.
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

        Runnable runnable = () ->
        {
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

        Runnable runnable = () ->
        {
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

        Runnable runnable = () ->
        {
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

    private Comparator<? super E> getComparator()
    {
        return this.comparator;
    }

    private void sort()
    {
        if (getComparator() == null)
        {
            return;
        }

        this.isSorting = true;
        super.sort(getComparator());
        this.isSorting = false;
    }
}
