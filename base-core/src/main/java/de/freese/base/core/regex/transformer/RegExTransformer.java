package de.freese.base.core.regex.transformer;

/**
 * @author Thomas Freese
 */
public interface RegExTransformer {
    String regExToWildcard(String regex);

    String wildcardToRegEx(String wildcard);
}
