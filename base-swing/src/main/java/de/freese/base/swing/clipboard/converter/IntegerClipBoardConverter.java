/**
 * 
 */
package de.freese.base.swing.clipboard.converter;

/**
 * ClipboardConverter fuer Integer.
 * 
 * @author Thomas Freese
 */
public class IntegerClipBoardConverter extends AbstractNumberClipBoardConverter
{
	/**
	 * Erstellt ein neues {@link IntegerClipBoardConverter} Object.
	 */
	public IntegerClipBoardConverter()
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

		return Integer.valueOf(temp);
	}
}
