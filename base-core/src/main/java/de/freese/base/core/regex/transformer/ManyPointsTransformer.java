package de.freese.base.core.regex.transformer;

/**
 * @author Thomas Freese
 */
public class ManyPointsTransformer implements RegExTransformer {
    /**
     * @see de.freese.base.core.regex.transformer.RegExTransformer#regExToWildcard(java.lang.String)
     */
    @Override
    public String regExToWildcard(final String regex) {
        return regex.replaceAll("\\*+", "*");
    }

    /**
     * @see de.freese.base.core.regex.transformer.RegExTransformer#wildcardToRegEx(java.lang.String)
     */
    @Override
    public String wildcardToRegEx(final String wildcard) {
        return wildcard.replaceAll("\\.+", ".");
    }
}
