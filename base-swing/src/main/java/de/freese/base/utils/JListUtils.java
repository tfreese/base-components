package de.freese.base.utils;

import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.JList;

/**
 * @author Thomas Freese
 */
public final class JListUtils
{
    public static void fillList(final JList<?> list, final Iterable<?> iterable)
    {
        fillList(list, iterable.iterator());
    }

    public static void fillList(final JList<?> list, final Iterator<?> iterator)
    {
        DefaultListModel<Object> listModel = (DefaultListModel<Object>) list.getModel();
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
