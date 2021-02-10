package de.freese.base.core.regex.transformer;

/**
 * Auftretende Klammern werden escaped, da sie ein Sonderzeichen in den regulaeren Ausdruecken sind.
 *
 * @author Thomas Freese
 */
public class EscapeBracketTransformer implements RegExTransformer
{
    /**
     * @see de.freese.base.core.regex.transformer.RegExTransformer#regExToWildcard(java.lang.String)
     */
    @Override
    public String regExToWildcard(final String regex)
    {
        return regex.replaceAll("\\\\\\(", "\\(");
    }

    /**
     * @see de.freese.base.core.regex.transformer.RegExTransformer#wildcardToRegEx(java.lang.String)
     */
    @Override
    public String wildcardToRegEx(final String wildcard)
    {
        return wildcard.replaceAll("\\(", "\\\\(");
    }
}
