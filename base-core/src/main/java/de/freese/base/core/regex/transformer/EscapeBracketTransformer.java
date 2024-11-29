package de.freese.base.core.regex.transformer;

/**
 * @author Thomas Freese
 */
public class EscapeBracketTransformer implements RegExTransformer {
    @Override
    public String regExToWildcard(final String regex) {
        return regex.replace("\\\\\\(", "\\(");
    }

    @Override
    public String wildcardToRegEx(final String wildcard) {
        return wildcard.replace("\\(", "\\\\(");
    }
}
