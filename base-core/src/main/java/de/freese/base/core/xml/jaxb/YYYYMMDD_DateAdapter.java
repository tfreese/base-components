package de.freese.base.core.xml.jaxb;

import java.util.Date;

/**
 * JAXB Adapter für ein {@link Date} im yyyy-MM-dd Format.
 * 
 * @author Thomas Freese
 */
public class YYYYMMDD_DateAdapter extends DateAdapter
{
	/**
	 * Erstellt ein neues {@link YYYYMMDD_DateAdapter} Object.
	 */
	public YYYYMMDD_DateAdapter()
	{
		super("yyyy-MM-dd");
	}
}
