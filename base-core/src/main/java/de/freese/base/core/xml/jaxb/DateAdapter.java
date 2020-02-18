package de.freese.base.core.xml.jaxb;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * JAXB Adapter für ein {@link Date} Objekt.
 * 
 * @author Thomas Freese
 */
public class DateAdapter extends XmlAdapter<String, Date>
{
	/**
	 *
	 */
	private final DateFormat formatter;

	/**
	 * Erstellt ein neues {@link DateAdapter} Object.
	 * 
	 * @param pattern String, z.B. "yyyy-MM-dd"
	 */
	public DateAdapter(final String pattern)
	{
		super();

		this.formatter = new SimpleDateFormat(pattern);
	}

	/**
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public String marshal(final Date date) throws Exception
	{
		return this.formatter.format(date);
	}

	/**
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	public Date unmarshal(final String date) throws Exception
	{
		return this.formatter.parse(date);
	}
}
