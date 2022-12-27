package de.freese.base.swing.eventlist;

import java.util.Comparator;
import java.util.List;

import javax.swing.event.ListDataListener;

/**
 * @author Thomas Freese
 */
public interface EventList<E> extends List<E>
{
    void addListDataListener(ListDataListener listener);

    Object getOwner();

    boolean isListenerEnabled();

    void removeListDataListener(ListDataListener listener);

    void reset();

    void setComparator(Comparator<? super E> comparator);

    void setListenerEnabled(boolean listenerEnabled);

    void setOwner(Object owner);

    /**
     * Fires a ContentsChanged Event if {@link #setListenerEnabled(boolean)} was called.
     */
    void update();
}
