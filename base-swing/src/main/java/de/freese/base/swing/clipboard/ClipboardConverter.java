package de.freese.base.swing.clipboard;

/**
 * Konvertiert Daten aus der Zwischenablage in konkrete Java Objekte.
 *
 * @author Thomas Freese
 */
public interface ClipboardConverter {
    /**
     * Konvertiert aus einem String der Zwischenablage ein Java Objekt.
     */
    Object fromClipboard(String value);

    /**
     * Konvertiert aus einem Java Objekt einen String für die Zwischenablage.
     */
    String toClipboard(Object object);
}
