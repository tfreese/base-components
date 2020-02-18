/**
 * 
 */
package de.freese.base.swing.clipboard.converter;

/**
 * ClipboardConverter fuer Double.
 * 
 * @author Thomas Freese
 */
public class DoubleClipBoardConverter extends AbstractNumberClipBoardConverter
{
	/**
	 * Erstellt ein neues {@link DoubleClipBoardConverter} Object.
	 */
	public DoubleClipBoardConverter()
	{
		super();
	}

	/**
	 * @see de.freese.base.swing.clipboard.ClipboardConverter#fromClipboard(java.lang.String)
	 */
	@Override
	public Object fromClipboard(final String value)
	{
		if (value == null)
		{
			return null;
		}

		String temp = normalizeFraction(value);

		return Double.valueOf(temp);
	}
}
