package de.freese.base.core.regex.transformer;

/**
 * @author Thomas Freese
 */
public class SingleSignTransformer implements RegExTransformer {
    /**
     * @see de.freese.base.core.regex.transformer.RegExTransformer#regExToWildcard(java.lang.String)
     */
    @Override
    public String regExToWildcard(final String regex) {
        return regex.replaceAll("\\.\\{1}", "?");
    }

    /**
     * @see de.freese.base.core.regex.transformer.RegExTransformer#wildcardToRegEx(java.lang.String)
     */
    @Override
    public String wildcardToRegEx(final String wildcard) {
        String expression = wildcard.replaceAll("\\?", ".{1}");
        expression = expression.replace("_", ".{1}");

        return expression;
    }
}
