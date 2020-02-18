/**
 * 
 */
package de.freese.base.swing.clipboard.converter;

/**
 * ClipboardConverter fuer Long.
 * 
 * @author Thomas Freese
 */
public class LongClipBoardConverter extends AbstractNumberClipBoardConverter
{
	/**
	 * Erstellt ein neues {@link LongClipBoardConverter} Object.
	 */
	public LongClipBoardConverter()
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

		String temp = normalizeNonFraction(value);

		return Long.valueOf(temp);
	}
}
