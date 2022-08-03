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
    /**
     * Hinzufügen eines {@link ListDataListener}s.
     *
     * @param listener {@link ListDataListener}
     */
    void addListDataListener(final ListDataListener listener);

    /**
     * Liefert den Eigentümer der Liste.
     *
     * @return Object
     */
    Object getOwner();

    /**
     * Liefert true, wenn die Events gefeuert werden.
     *
     * @return boolean
     */
    boolean isListenerEnabled();

    /**
     * Entfernen eines {@link ListDataListener}s.
     *
     * @param listener {@link ListDataListener}
     */
    void removeListDataListener(final ListDataListener listener);

    /**
     * Stellt einen ursprünglichen Zustand wieder her.
     */
    void reset();

    /**
     * Setzt den Comparator.
     *
     * @param comparator {@link Comparator}<E>
     */
    void setComparator(final Comparator<? super E> comparator);

    /**
     * True, wenn die Events gefeuert werden sollen.
     *
     * @param listenerEnabled boolean
     */
    void setListenerEnabled(final boolean listenerEnabled);

    /**
     * Setzt den Eigentümer der Liste.
     *
     * @param owner Object
     */
    void setOwner(final Object owner);

    /**
     * Feuert ein ContentsChanged Event für die ganze Liste.<br>
     * Nur relevant, wenn {@link #setListenerEnabled(boolean)} verwendet wurde.
     */
    void update();
}
