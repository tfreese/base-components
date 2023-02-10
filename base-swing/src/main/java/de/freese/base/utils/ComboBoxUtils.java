package de.freese.base.utils;

import java.util.Iterator;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

/**
 * @author Thomas Freese
 */
public final class ComboBoxUtils {
    public static <T> void fillComboBox(final JComboBox<T> comboBox, final Iterable<T> iterable) {
        fillComboBox(comboBox, iterable.iterator());
    }

    public static <T> void fillComboBox(final JComboBox<T> comboBox, final Iterator<T> iterator) {
        DefaultComboBoxModel<T> comboBoxModel = (DefaultComboBoxModel<T>) comboBox.getModel();
        comboBoxModel.removeAllElements();

        while (iterator.hasNext()) {
            comboBoxModel.addElement(iterator.next());
        }
    }

    private ComboBoxUtils() {
        super();
    }
}
