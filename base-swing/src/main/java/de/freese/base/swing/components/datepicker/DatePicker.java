package de.freese.base.swing.components.datepicker;

import java.io.Serial;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.jdesktop.swingx.JXDatePicker;

/**
 * Alternative: <a href="https://github.com/LGoodDatePicker/LGoodDatePicker">LGoodDatePicker</a>
 *
 * @author Thomas Freese
 */
public class DatePicker extends JXDatePicker {
    @Serial
    private static final long serialVersionUID = -4014651391029802229L;

    public DatePicker() {
        super();

        initialize();
    }

    public DatePicker(final Date selected) {
        super(selected);

        initialize();
    }

    public DatePicker(final Date selection, final Locale locale) {
        super(selection, locale);

        initialize();
    }

    public DatePicker(final Locale locale) {
        super(locale);

        initialize();
    }

    public Calendar getCalendar() {
        final Calendar calendar = new GregorianCalendar();
        calendar.setTime(getDate());

        return calendar;
    }

    public void setCalendar(final Calendar calendar) {
        setDate(calendar.getTime());
    }

    protected void initialize() {
        getMonthView().setShowingWeekNumber(true);
        getMonthView().setPreferredColumnCount(2);
        getMonthView().setPreferredRowCount(2);

        setDate(new Date());
    }
}
