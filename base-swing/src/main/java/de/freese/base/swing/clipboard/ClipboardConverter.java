package de.freese.base.swing.clipboard;

/**
 * Konvertiert Daten aus der Zwischenablage in konkrete Java Objekte.
 *
 * @author Thomas Freese
 */
public interface ClipboardConverter
{
    /**
     * Konvertiert aus einem String der Zwischenablage ein Java Objekt.
     * 
     * @param value String
     * @return Object
     */
    public Object fromClipboard(String value);

    /**
     * Konvertiert aus einem Java Objekt einen String fuer die Zwischenablage.
     * 
     * @param object Object
     * @return String
     */
    public String toClipboard(Object object);
}
