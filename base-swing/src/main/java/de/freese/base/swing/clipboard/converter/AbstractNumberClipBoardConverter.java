/**
 * 
 */
package de.freese.base.swing.clipboard.converter;

/**
 * BasisClipboardConverter fuer Zahlen.
 * 
 * @author Thomas Freese
 */
public abstract class AbstractNumberClipBoardConverter extends AbstractClipboardConverter
{
	/**
	 * Erstellt ein neues {@link AbstractNumberClipBoardConverter} Object.
	 */
	public AbstractNumberClipBoardConverter()
	{
		super();
	}

	/**
	 * Normalisiert den String für die Umwandlung, entfernt ungültige Zeichen, Buchstaben etc.
	 * 
	 * @param value String
	 * @return String
	 */
	protected String normalize(final String value)
	{
		String temp = value;
		temp = temp.replaceAll("[a-zA-Z]", "");
		temp = temp.replaceAll("%", "");

		return temp.trim();
	}

	/**
	 * Normalisiert den String für die Umwandlung, entfernt ungültige Zeichen, Buchstaben etc.<br>
	 * für Komma-Zahlen.
	 * 
	 * @param value String
	 * @return String
	 */
	protected String normalizeFraction(final String value)
	{
		String temp = normalize(value);
		temp = temp.replaceAll("\\,", ".");

		return temp;
	}

	/**
	 * Normalisiert den String für die Umwandlung, entfernt ungültige Zeichen, Buchstaben etc.<br>
	 * für ganze Zahlen.
	 * 
	 * @param value String
	 * @return String
	 */
	protected String normalizeNonFraction(final String value)
	{
		String temp = normalize(value);
		temp = temp.replaceAll("\\.", ",");

		int index = temp.indexOf(",");

		// Nur den Wert VOR dem Komma verwenden !
		if (index != -1)
		{
			temp = temp.substring(0, index);
		}

		return temp;
	}
}
