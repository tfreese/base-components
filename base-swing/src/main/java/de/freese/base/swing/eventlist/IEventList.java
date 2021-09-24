package de.freese.base.swing.eventlist;

import java.util.Comparator;
import java.util.List;

import javax.swing.event.ListDataListener;

/**
 * Interface einer eigenen Liste, welche bei Aenderungen Events feuert.
 *
 * @author Thomas Freese
 *
 * @param <E> Konkreter Typ der Listobjekte.
 */
public interface IEventList<E> extends List<E>
{
    /**
     * Hinzufuegen eines {@link ListDataListener}s.
     *
     * @param listener {@link ListDataListener}
     */
    void addListDataListener(final ListDataListener listener);

    /**
     * Liefert den Eigentuemer der Liste.
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
     * Stellt einen urspruenglichen Zustand wieder her.
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
     * Setzt den Eigentuemer der Liste.
     *
     * @param owner Object
     */
    void setOwner(final Object owner);

    /**
     * Feuert ein ContentsChanged Event fuer die ganze Liste.<br>
     * Nur relevant, wenn {@link #setListenerEnabled(boolean)} verwendet wurde.
     */
    void update();
}
