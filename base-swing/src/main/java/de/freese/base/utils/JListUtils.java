package de.freese.base.utils;

import java.util.Arrays;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.JList;

/**
 * Die fill-Methoden setzen voraus, dass die {@link JList} ein {@link DefaultListModel} besitzt.
 *
 * @author Thomas Freese
 */
public final class JListUtils
{
    /**
     * Setzt die Daten des Iterables in das {@link DefaultListModel}.
     *
     * @param list {@link JList}
     * @param iterable {@link Iterable}
     */
    public static void fillList(final JList<?> list, final Iterable<?> iterable)
    {
        fillList(list, iterable.iterator());
    }

    /**
     * Setzt die Daten des Iterators in das {@link DefaultListModel}.
     *
     * @param list {@link JList}
     * @param iterator {@link Iterator}
     */
    @SuppressWarnings("unchecked")
    public static void fillList(final JList<?> list, final Iterator<?> iterator)
    {
        DefaultListModel<Object> listModel = (DefaultListModel<Object>) list.getModel();
        listModel.removeAllElements();

        while (iterator.hasNext())
        {
            listModel.addElement(iterator.next());
        }
    }

    /**
     * Setzt die Daten des Enums in das {@link DefaultListModel}.
     *
     * @param <T> Konkreter Typ der Objekte.
     * @param list {@link JList}
     * @param objects {@link Object}
     */
    @SuppressWarnings("unchecked")
    public static <T> void fillList(final JList<T> list, final T... objects)
    {
        fillList(list, Arrays.asList(objects));
    }

    /**
     * Erstellt ein neues {@link JListUtils} Object.
     */
    private JListUtils()
    {
        super();
    }
}
