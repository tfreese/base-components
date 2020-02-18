package de.freese.base.core.xml.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * JAXB Adapter um aus einem String für einen Double ein Integer zu machen.
 * 
 * @author Thomas Freese
 */
public class DoubleToIntAdapter extends XmlAdapter<String, Integer>
{
	/**
	 * Erstellt ein neues {@link DoubleToIntAdapter} Object.
	 */
	public DoubleToIntAdapter()
	{
		super();
	}

	/**
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public String marshal(final Integer arg0) throws Exception
	{
		return arg0.toString();
	}

	/**
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	public Integer unmarshal(final String arg0) throws Exception
	{
		if ((arg0 == null) || (arg0.length() == 0))
		{
			return null;
		}

		double value = Double.parseDouble(arg0);

		return Integer.valueOf((int) value);
	}
}
