package de.freese.base.core.regex.transformer;

/**
 * @author Thomas Freese
 */
public class StartsWithTransformer implements RegExTransformer {
    @Override
    public String regExToWildcard(final String regex) {
        if (regex.startsWith(".*")) {
            return regex.replaceFirst("\\.*", "*");
        }

        if (regex.startsWith(".{1}")) {
            return regex.replaceFirst("\\.{1}", "?");
        }

        return regex;
    }

    @Override
    public String wildcardToRegEx(final String wildcard) {
        if (!wildcard.startsWith("^") && !wildcard.startsWith(".*") && !wildcard.startsWith(".{1}")) {
            return ".*" + wildcard;
        }

        return wildcard;
    }
}
