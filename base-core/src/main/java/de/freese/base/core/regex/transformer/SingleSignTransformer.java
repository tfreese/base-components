package de.freese.base.core.regex.transformer;

/**
 * ? und _ durch .{1} ersetzen, nur ein beliebiges Zeichen soll auftreten.
 *
 * @author Thomas Freese
 */
public class SingleSignTransformer implements RegExTransformer
{
    /**
     * Erstellt ein neues {@link EscapeBracketTransformer} Object.
     */
    public SingleSignTransformer()
    {
        super();
    }

    /**
     * @see de.freese.base.core.regex.transformer.RegExTransformer#regExToWildcard(java.lang.String)
     */
    @Override
    public String regExToWildcard(final String regex)
    {
        return regex.replaceAll("\\.\\{1}", "?");
    }

    /**
     * @see de.freese.base.core.regex.transformer.RegExTransformer#wildcardToRegEx(java.lang.String)
     */
    @Override
    public String wildcardToRegEx(final String wildcard)
    {
        String expression = wildcard.replaceAll("\\?", ".{1}");
        expression = expression.replace("_", ".{1}");

        return expression;
    }
}
