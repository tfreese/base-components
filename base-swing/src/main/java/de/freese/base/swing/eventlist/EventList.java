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
    void addListDataListener(final ListDataListener listener);

    Object getOwner();

    boolean isListenerEnabled();

    void removeListDataListener(final ListDataListener listener);

    void reset();

    void setComparator(final Comparator<? super E> comparator);

    void setListenerEnabled(final boolean listenerEnabled);

    void setOwner(final Object owner);

    /**
     * Feuert ein ContentsChanged Event für die ganze Liste.<br>
     * Nur relevant, wenn {@link #setListenerEnabled(boolean)} verwendet wurde.
     */
    void update();
}
