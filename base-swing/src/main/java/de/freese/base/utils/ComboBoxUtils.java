package de.freese.base.utils;

import java.util.Arrays;
import java.util.Iterator;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

/**
 * Utilmethoden fuer eine {@link JComboBox}.<br>
 * Die fill-Methoden setzen vorraus das die {@link JComboBox} ein {@link DefaultComboBoxModel}
 * besitzt.
 * 
 * @author Thomas Freese
 */
public final class ComboBoxUtils
{
	/**
	 * Setzt die Daten des Iterables in das ComboBoxModel.
	 * 
	 * @param comboBox {@link JComboBox}
	 * @param iterable {@link Iterable}
	 */
	public static void fillComboBox(final JComboBox<?> comboBox, final Iterable<?> iterable)
	{
		fillComboBox(comboBox, iterable.iterator());
	}

	/**
	 * Setzt die Daten des Iterators in das ComboBoxModel.
	 * 
	 * @param comboBox {@link JComboBox}
	 * @param iterator {@link Iterator}
	 */
	@SuppressWarnings("unchecked")
	public static void fillComboBox(final JComboBox<?> comboBox, final Iterator<?> iterator)
	{
		DefaultComboBoxModel<Object> comboBoxModel =
				(DefaultComboBoxModel<Object>) comboBox.getModel();
		comboBoxModel.removeAllElements();

		while (iterator.hasNext())
		{
			comboBoxModel.addElement(iterator.next());
		}
	}

	/**
	 * Setzt die Daten des Iterables in das ComboBoxModel.
	 * 
	 * @param <T> Konkreter Typ der Objekte.
	 * @param comboBox {@link JComboBox}
	 * @param objects @link Object[]
	 */
	@SuppressWarnings("unchecked")
	public static <T> void fillComboBox(final JComboBox<T> comboBox, final T...objects)
	{
		fillComboBox(comboBox, Arrays.asList(objects));
	}
}
