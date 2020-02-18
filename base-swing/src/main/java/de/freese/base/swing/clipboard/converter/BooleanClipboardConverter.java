// Created: 06.10.2008
package de.freese.base.swing.clipboard.converter;

import org.apache.commons.lang3.StringUtils;

/**
 * Allgemeiner ClipboardConverter für Boolean.
 * 
 * @author Thomas Freese
 */
public class BooleanClipboardConverter extends AbstractClipboardConverter
{
	/**
	 * Creates a new {@link BooleanClipboardConverter} object.
	 */
	public BooleanClipboardConverter()
	{
		super();
	}

	/**
	 * @see de.freese.base.swing.clipboard.ClipboardConverter#fromClipboard(java.lang.String)
	 */
	@Override
	public Object fromClipboard(final String value)
	{
		if (StringUtils.isEmpty(value))
		{
			return Boolean.FALSE;
		}

		String temp = value.trim();

		if (temp.equalsIgnoreCase("true") || temp.equalsIgnoreCase("1")
				|| temp.equalsIgnoreCase("yes") || temp.equalsIgnoreCase("ja")
				|| temp.equalsIgnoreCase("on") || temp.equalsIgnoreCase("an")
				|| temp.equalsIgnoreCase("active") || temp.equalsIgnoreCase("aktiv"))
		{
			return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}
}
