package de.freese.base.swing.components.datepicker;

import java.io.Serial;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
public class SwingXDatePicker extends JXDatePicker {
    @Serial
    private static final long serialVersionUID = -4014651391029802229L;

    public SwingXDatePicker() {
        super();

        initialize();
    }

    public SwingXDatePicker(final Date selected) {
        super(selected);

        initialize();
    }

    public SwingXDatePicker(final Date selection, final Locale locale) {
        super(selection, locale);

        initialize();
    }

    public SwingXDatePicker(final Locale locale) {
        super(locale);

        initialize();
    }

    public Calendar getCalendar() {
        final Calendar calendar = new GregorianCalendar();
        calendar.setTime(getDate());

        return calendar;
    }

    public LocalDate getLocalDate() {
        final Instant instant = getDate().toInstant();

        return LocalDate.ofInstant(instant, ZoneId.systemDefault());
    }

    public void setCalendar(final Calendar calendar) {
        setDate(calendar.getTime());
    }

    public void setLocalDate(final LocalDate localDate) {
        final ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());

        setDate(Date.from(zonedDateTime.toInstant()));
    }

    protected void initialize() {
        getMonthView().setShowingWeekNumber(true);
        getMonthView().setPreferredColumnCount(2);
        getMonthView().setPreferredRowCount(2);

        setDate(new Date());
    }
}
