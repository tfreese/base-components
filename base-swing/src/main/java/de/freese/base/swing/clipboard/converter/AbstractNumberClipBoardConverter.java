package de.freese.base.swing.clipboard.converter;

/**
 * @author Thomas Freese
 */
public abstract class AbstractNumberClipBoardConverter extends AbstractClipboardConverter {
    /**
     * Normalisiert den String für die Umwandlung, entfernt ungültige Zeichen, Buchstaben etc.
     */
    protected String normalize(final String value) {
        String temp = value;
        temp = temp.replaceAll("[a-zA-Z]", "");
        temp = temp.replace("%", "");

        return temp.strip();
    }

    /**
     * Normalisiert den String für die Umwandlung, entfernt ungültige Zeichen, Buchstaben etc.<br>
     * für Komma-Zahlen.
     */
    protected String normalizeFraction(final String value) {
        String temp = normalize(value);
        temp = temp.replace("\\,", ".");

        return temp;
    }

    /**
     * Normalisiert den String für die Umwandlung, entfernt ungültige Zeichen, Buchstaben etc.<br>
     * für ganze Zahlen.
     */
    protected String normalizeNonFraction(final String value) {
        String temp = normalize(value);
        temp = temp.replace("\\.", ",");

        int index = temp.indexOf(',');

        // Nur den Wert VOR dem Komma verwenden !
        if (index != -1) {
            temp = temp.substring(0, index);
        }

        return temp;
    }
}
