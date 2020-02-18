package de.freese.base.swing.components.datepicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.jdesktop.swingx.JXDatePicker;

/**
 * DatePicker Komponente.
 * 
 * @author Thomas Freese
 */
public class DatePicker extends JXDatePicker
{
	/**
	 *
	 */
	private static final long serialVersionUID = -4014651391029802229L;

	/**
	 * Erstellt ein neues {@link DatePicker} Object.
	 */
	public DatePicker()
	{
		super();

		initialize();
	}

	/**
	 * Erstellt ein neues {@link DatePicker} Object.
	 * 
	 * @param selected {@link Date}
	 */
	public DatePicker(final Date selected)
	{
		super(selected);

		initialize();
	}

	/**
	 * Erstellt ein neues {@link DatePicker} Object.
	 * 
	 * @param selection {@link Date}
	 * @param locale {@link Locale}
	 */
	public DatePicker(final Date selection, final Locale locale)
	{
		super(selection, locale);

		initialize();
	}

	/**
	 * Erstellt ein neues {@link DatePicker} Object.
	 * 
	 * @param locale {@link Locale}
	 */
	public DatePicker(final Locale locale)
	{
		super(locale);

		initialize();
	}

	/**
	 * @return {@link Calendar}
	 */
	public Calendar getCalendar()
	{
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(getDate());

		return calendar;
	}

	/**
	 * @param calendar {@link Calendar}
	 */
	public void setCalendar(final Calendar calendar)
	{
		setDate(calendar.getTime());
	}

	/**
	 * Defaultkonfiguration.
	 */
	protected void initialize()
	{
		getMonthView().setShowingWeekNumber(true);
		getMonthView().setPreferredColumnCount(2);
		getMonthView().setPreferredRowCount(2);

		setDate(new Date());
	}
}
