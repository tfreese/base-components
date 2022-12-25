package de.freese.base.swing.eventlist;

import java.util.Comparator;
import java.util.List;

import javax.swing.event.ListDataListener;

/**
 * Interface einer eigenen Liste, welche bei Änderungen Events feuert.
 *
 * @param <E> Type
 *
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
     * Feuert ein ContentsChanged Event für die ganze Liste.<br>
     * Nur relevant, wenn {@link #setListenerEnabled(boolean)} verwendet wurde.
     */
    void update();
}
