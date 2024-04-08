package de.freese.base.swing.fontchange.handler;

import java.awt.Font;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXMonthView;

/**
 * @author Thomas Freese
 */
public class DatePickerFontChangeHandler extends ComponentFontChangeHandler {
    @Override
    public void fontChanged(final Font newFont, final Object object) {
        super.fontChanged(newFont, object);

        final JXDatePicker datePicker = (JXDatePicker) object;

        final JXMonthView monthView = datePicker.getMonthView();
        super.fontChanged(newFont, monthView);
    }
}
