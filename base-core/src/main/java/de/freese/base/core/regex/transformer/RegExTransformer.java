package de.freese.base.core.regex.transformer;

/**
 * Transformiert Wildcard-Ausdrücke in RegEx-Ausdrücke und zurück.
 *
 * @author Thomas Freese
 */
public interface RegExTransformer
{
    /**
     * Transformiert einen RegEx-Ausdruck in einen Wildcard-Ausdruck.
     *
     * @param regex String
     *
     * @return String
     */
    String regExToWildcard(String regex);

    /**
     * Transformiert einen Wildcard-Ausdruck in einen RegEx-Ausdruck.
     *
     * @param wildcard String
     *
     * @return String
     */
    String wildcardToRegEx(String wildcard);
}
