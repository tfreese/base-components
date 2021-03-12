package de.freese.base.core.regex.transformer;

/**
 * Transformiert Wildcard-Ausdruecke in RegEx-Ausdruecke und zurueck.
 *
 * @author Thomas Freese
 */
public interface RegExTransformer
{
    /**
     * Transformiert einen RegEx-Ausdruck in einen Wildcard-Ausdruck.
     * 
     * @param regex String
     * @return String
     */
    public String regExToWildcard(String regex);

    /**
     * Transformiert einen Wildcard-Ausdruck in einen RegEx-Ausdruck.
     * 
     * @param wildcard String
     * @return String
     */
    public String wildcardToRegEx(String wildcard);
}
