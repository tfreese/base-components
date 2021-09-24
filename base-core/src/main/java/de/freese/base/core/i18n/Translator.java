package de.freese.base.core.i18n;

/**
 * Allgemeines Interface für Übersetzer.<br>
 * Default: String.format(key, args)
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface Translator
{
    /**
     * Übersetzt einen Key mit optionalen Parametern.
     *
     * @param key String
     * @param args Object[], optional
     *
     * @return String
     */
    String translate(final String key, final Object...args);
    // public default String translate(final String key, final Object...args)
    // {
    // return String.format(key, args);
    // }
}
