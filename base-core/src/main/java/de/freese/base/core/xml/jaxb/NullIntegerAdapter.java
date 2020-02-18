package de.freese.base.core.xml.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * JAXB Adapter für ein {@link Integer} die auch null sein können.
 * 
 * @author Thomas Freese
 */
public class NullIntegerAdapter extends XmlAdapter<String, Integer>
{
	/**
	 * Erstellt ein neues {@link NullIntegerAdapter} Object.
	 */
	public NullIntegerAdapter()
	{
		super();
	}

	/**
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public String marshal(final Integer arg0) throws Exception
	{
		return arg0 != null ? arg0.toString() : "null";
	}

	/**
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	public Integer unmarshal(final String arg0) throws Exception
	{
		return arg0 != null ? Integer.valueOf(arg0) : null;
	}
}
