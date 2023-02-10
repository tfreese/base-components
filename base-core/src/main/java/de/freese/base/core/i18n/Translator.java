package de.freese.base.core.i18n;

/**
 * Allgemeines Interface für Übersetzer.<br>
 * Default: String.format(key, args)
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface Translator {
    String translate(String key, Object... args);
    // public default String translate(final String key, final Object...args)
    // {
    // return String.format(key, args);
    // }
}
