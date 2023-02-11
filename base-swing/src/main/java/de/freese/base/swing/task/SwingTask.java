// Created: 18.05.2020
package de.freese.base.swing.task;

import javax.swing.SwingWorker;

/**
 * API für asynchrone Ausführungen unter Swing, analog zur JavaFX Task-Implementierung.
 *
 * @author Thomas Freese
 * @see SwingWorker
 */
@SuppressWarnings("checkstyle:InterfaceIsType")
public interface SwingTask {
    /**
     * Wurde bewusst abgebrochen.
     */
    String PROPERTY_CANCELLED = "cancelled";

    /**
     * Abbruch mit Fehler.
     */
    String PROPERTY_FAILED = "failed";

    /**
     * Ausführung von Zwischenergebnissen.
     */
    String PROPERTY_PROCESS = "process";

    /**
     * Fortschrittsanzeige.
     */
    String PROPERTY_PROGRESS = "progress";

    /**
     * Begin der Verarbeitung.
     */
    String PROPERTY_STARTED = "started";

    /**
     * Änderung des Untertitels.
     */
    String PROPERTY_SUBTITLE = "subtitle";

    /**
     * Erfolgreich beendet.
     */
    String PROPERTY_SUCCEEDED = "succeeded";

    /**
     * Änderung des Titels.
     */
    String PROPERTY_TITLE = "title";
}
