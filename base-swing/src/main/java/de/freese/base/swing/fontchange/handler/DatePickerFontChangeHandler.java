package de.freese.base.swing.fontchange.handler;

import java.awt.Font;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXMonthView;

/**
 * @author Thomas Freese
 */
public class DatePickerFontChangeHandler extends ComponentFontChangeHandler
{
    /**
     * @see de.freese.base.swing.fontchange.handler.ComponentFontChangeHandler#fontChanged(java.awt.Font, java.lang.Object)
     */
    @Override
    public void fontChanged(final Font newFont, final Object object)
    {
        super.fontChanged(newFont, object);

        JXDatePicker datePicker = (JXDatePicker) object;

        JXMonthView monthView = datePicker.getMonthView();
        super.fontChanged(newFont, monthView);
    }
}
