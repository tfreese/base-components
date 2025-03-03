package de.freese.base.core.regex.transformer;

/**
 * @author Thomas Freese
 */
public class SingleSignTransformer implements RegExTransformer {
    @Override
    public String regExToWildcard(final String regex) {
        return regex.replace("\\.\\{1}", "?");
    }

    @Override
    public String wildcardToRegEx(final String wildcard) {
        String expression = wildcard.replace("\\?", ".{1}");
        expression = expression.replace("_", ".{1}");

        return expression;
    }
}
