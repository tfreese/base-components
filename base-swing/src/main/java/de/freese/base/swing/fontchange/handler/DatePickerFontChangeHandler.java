package de.freese.base.swing.fontchange.handler;

import java.awt.Font;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXMonthView;

/**
 * Defaultimplementierung fuer die Fontaenderung eines {@link JXDatePicker}.
 * 
 * @author Thomas Freese
 */
public class DatePickerFontChangeHandler extends ComponentFontChangeHandler
{
	/**
	 * Erstellt ein neues {@link DatePickerFontChangeHandler} Object.
	 */
	public DatePickerFontChangeHandler()
	{
		super();
	}

	/**
	 * @see de.freese.base.swing.fontchange.handler.ComponentFontChangeHandler#fontChanged(java.awt.Font,
	 *      java.lang.Object)
	 */
	@Override
	public void fontChanged(final Font newFont, final Object object)
	{
		super.fontChanged(newFont, object);

		JXDatePicker datePicker = (JXDatePicker) object;

		JXMonthView monthView = datePicker.getMonthView();
		super.fontChanged(newFont, monthView);

		// Nicht moeglich an den Link zu kommen
		// JPanel linkPanel = datePicker.getLinkPanel();
		// super.fontChanged(newFont, linkPanel);
	}
}
