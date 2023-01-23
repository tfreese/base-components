package de.freese.base.utils;

import java.util.Iterator;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

/**
 * @author Thomas Freese
 */
public final class ComboBoxUtils
{
    public static void fillComboBox(final JComboBox<?> comboBox, final Iterable<?> iterable)
    {
        fillComboBox(comboBox, iterable.iterator());
    }

    public static void fillComboBox(final JComboBox<?> comboBox, final Iterator<?> iterator)
    {
        DefaultComboBoxModel<Object> comboBoxModel = (DefaultComboBoxModel<Object>) comboBox.getModel();
        comboBoxModel.removeAllElements();

        while (iterator.hasNext())
        {
            comboBoxModel.addElement(iterator.next());
        }
    }

    private ComboBoxUtils()
    {
        super();
    }
}
