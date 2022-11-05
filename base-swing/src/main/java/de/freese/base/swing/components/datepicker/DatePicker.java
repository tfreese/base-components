package de.freese.base.swing.components.datepicker;

import java.awt.BorderLayout;
import java.io.Serial;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.jdesktop.swingx.JXDatePicker;

/**
 * DatePicker Komponente.
 *
 * @author Thomas Freese
 */
public class DatePicker extends JXDatePicker
{
    @Serial
    private static final long serialVersionUID = -4014651391029802229L;

    public static void main(final String[] args)
    {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(BorderLayout.CENTER, new DatePicker());
        //        frame.setSize(300, 300);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public DatePicker()
    {
        super();

        initialize();
    }

    public DatePicker(final Date selected)
    {
        super(selected);

        initialize();
    }

    public DatePicker(final Date selection, final Locale locale)
    {
        super(selection, locale);

        initialize();
    }

    public DatePicker(final Locale locale)
    {
        super(locale);

        initialize();
    }

    public Calendar getCalendar()
    {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(getDate());

        return calendar;
    }

    public void setCalendar(final Calendar calendar)
    {
        setDate(calendar.getTime());
    }

    protected void initialize()
    {
        getMonthView().setShowingWeekNumber(true);
        getMonthView().setPreferredColumnCount(2);
        getMonthView().setPreferredRowCount(2);

        setDate(new Date());
    }
}
