package de.freese.base.resourcemap.converter;

/**
 * Exception fuer {@link IResourceConverter}.
 * 
 * @author Thomas Freese
 */
public class ResourceConverterException extends Exception
{
	/**
	 *
	 */
	private static final long serialVersionUID = 8562723118889869531L;

	/**
	 * 
	 */
	private final String badString;

	/**
	 * Erstellt ein neues {@link ResourceConverterException} Object.
	 * 
	 * @param message String
	 * @param badString String
	 */
	ResourceConverterException(final String message, final String badString)
	{
		super(message);

		this.badString = maybeShorten(badString);
	}

	/**
	 * Erstellt ein neues {@link ResourceConverterException} Object.
	 * 
	 * @param message String
	 * @param badString String
	 * @param cause {@link Throwable}
	 */
	ResourceConverterException(final String message, final String badString, final Throwable cause)
	{
		super(message, cause);

		this.badString = maybeShorten(badString);
	}

	/**
	 * Kuerzung der Fehlermeldung.
	 * 
	 * @param s String
	 * @return String
	 */
	private String maybeShorten(final String s)
	{
		int n = s.length();

		return (n < 128) ? s : s.substring(0, 128) + "...[" + (n - 128) + " more characters]";
	}

	/**
	 * @see java.lang.Throwable#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder(super.toString());
		sb.append(" string: \"");
		sb.append(this.badString);
		sb.append("\"");

		return sb.toString();
	}
}
