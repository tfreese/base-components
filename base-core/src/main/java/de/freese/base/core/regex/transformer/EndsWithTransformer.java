package de.freese.base.core.regex.transformer;

/**
 * Pruefung auf gueltige Endausdruecke.
 * 
 * @author Thomas Freese
 */
public class EndsWithTransformer implements RegExTransformer
{
	/**
	 * Erstellt ein neues {@link EscapeBracketTransformer} Object.
	 */
	public EndsWithTransformer()
	{
		super();
	}

	/**
	 * @see de.freese.base.core.regex.transformer.RegExTransformer#wildcardToRegEx(java.lang.String)
	 */
	@Override
	public String wildcardToRegEx(final String wildcard)
	{
		if (!wildcard.endsWith("$") && !wildcard.endsWith(".*") && !wildcard.endsWith(".{1}"))
		{
			return wildcard + ".*";
		}

		return wildcard;
	}

	/**
	 * @see de.freese.base.core.regex.transformer.RegExTransformer#regExToWildcard(java.lang.String)
	 */
	@Override
	public String regExToWildcard(final String regex)
	{
		if (regex.endsWith(".*"))
		{
			int index = regex.lastIndexOf(".*");

			return regex.substring(0, index);
		}

		if (regex.endsWith(".{1}"))
		{
			int index = regex.lastIndexOf(".{1}");

			return regex.substring(0, index);
		}

		return regex;
	}
}
