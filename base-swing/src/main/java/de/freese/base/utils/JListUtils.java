package de.freese.base.utils;

import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.JList;

/**
 * @author Thomas Freese
 */
public final class JListUtils
{
    public static <T> void fillList(final JList<T> list, final Iterable<T> iterable)
    {
        fillList(list, iterable.iterator());
    }

    public static <T> void fillList(final JList<T> list, final Iterator<T> iterator)
    {
        DefaultListModel<T> listModel = (DefaultListModel<T>) list.getModel();
        listModel.removeAllElements();

        while (iterator.hasNext())
        {
            listModel.addElement(iterator.next());
        }
    }

    private JListUtils()
    {
        super();
    }
}
