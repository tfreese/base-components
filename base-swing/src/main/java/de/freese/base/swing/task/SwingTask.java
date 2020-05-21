/**
 * Created: 18.05.2020
 */

package de.freese.base.swing.task;

import javax.swing.SwingWorker;
import javafx.concurrent.Task;

/**
 * API für asynchrone Ausführungen unter Swing, analog zur JavaFX-Implementierung {@link Task}.
 *
 * @author Thomas Freese
 * @see SwingWorker
 * @see Task
 */
public interface SwingTask
{
    /**
     * Wurde bewusst abgebrochen.
     */
    public static final String PROPERTY_CANCELLED = "cancelled";

    /**
     * Abbruch mit Fehler.
     */
    public static final String PROPERTY_FAILED = "failed";

    /**
     * Ausführung von Zwischenergebnissen.
     */
    public static final String PROPERTY_PROCESS = "process";

    /**
     * Fortschrittsanzeige.
     */
    public static final String PROPERTY_PROGRESS = "progress";

    /**
     * Begin der Verarbeitung.
     */
    public static final String PROPERTY_STARTED = "started";

    /**
     * Änderung des Untertitels.
     */
    public static final String PROPERTY_SUBTITLE = "subtitle";

    /**
     * Erfolgreich beendet.
     */
    public static final String PROPERTY_SUCCEEDED = "succeeded";

    /**
     * Änderung des Titels.
     */
    public static final String PROPERTY_TITLE = "title";
}
