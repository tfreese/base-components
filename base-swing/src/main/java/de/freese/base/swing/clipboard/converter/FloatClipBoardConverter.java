/**
 * 
 */
package de.freese.base.swing.clipboard.converter;

/**
 * ClipboardConverter fuer Float.
 * 
 * @author Thomas Freese
 */
public class FloatClipBoardConverter extends AbstractNumberClipBoardConverter
{
	/**
	 * Erstellt ein neues {@link FloatClipBoardConverter} Object.
	 */
	public FloatClipBoardConverter()
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

		return Float.valueOf(temp);
	}
}
