package de.freese.base.core.regex.transformer;

/**
 * @author Thomas Freese
 */
public class ManyPointsTransformer implements RegExTransformer {
    @Override
    public String regExToWildcard(final String regex) {
        return regex.replaceAll("\\*+", "*");
    }

    @Override
    public String wildcardToRegEx(final String wildcard) {
        return wildcard.replaceAll("\\.+", ".");
    }
}
