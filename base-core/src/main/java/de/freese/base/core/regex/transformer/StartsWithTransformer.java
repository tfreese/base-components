package de.freese.base.core.regex.transformer;

/**
 * Pruefung auf gueltige Anfangsausdruecke.
 *
 * @author Thomas Freese
 */
public class StartsWithTransformer implements RegExTransformer
{
    /**
     * @see de.freese.base.core.regex.transformer.RegExTransformer#regExToWildcard(java.lang.String)
     */
    @Override
    public String regExToWildcard(final String regex)
    {
        if (regex.startsWith(".*"))
        {
            return regex.replaceFirst("\\.*", "*");
        }

        if (regex.startsWith(".{1}"))
        {
            return regex.replaceFirst("\\.{1}", "?");
        }

        return regex;
    }

    /**
     * @see de.freese.base.core.regex.transformer.RegExTransformer#wildcardToRegEx(java.lang.String)
     */
    @Override
    public String wildcardToRegEx(final String wildcard)
    {
        if (!wildcard.startsWith("^") && !wildcard.startsWith(".*") && !wildcard.startsWith(".{1}"))
        {
            return ".*" + wildcard;
        }

        return wildcard;
    }
}
