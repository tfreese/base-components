package de.freese.base.core.regex.transformer;

/**
 * @author Thomas Freese
 */
public class EndsWithTransformer implements RegExTransformer {
    @Override
    public String regExToWildcard(final String regex) {
        if (regex.endsWith(".*")) {
            final int index = regex.lastIndexOf(".*");

            return regex.substring(0, index);
        }

        if (regex.endsWith(".{1}")) {
            final int index = regex.lastIndexOf(".{1}");

            return regex.substring(0, index);
        }

        return regex;
    }

    @Override
    public String wildcardToRegEx(final String wildcard) {
        if (!wildcard.endsWith("$") && !wildcard.endsWith(".*") && !wildcard.endsWith(".{1}")) {
            return wildcard + ".*";
        }

        return wildcard;
    }
}
