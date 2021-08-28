package de.freese.base.mvc;

import java.awt.Component;

/**
 * Interface einer View.
 *
 * @author Thomas Freese
 */
public interface View
{
    /**
     * Aufbau der GUI.
     */
    void createGUI();

    /**
     * Liefert die Komponente der View.
     *
     * @return {@link Component}
     */
    Component getComponent();

    /**
     * Fehlerbehandlung.
     *
     * @param throwable {@link Throwable}
     */
    void handleException(Throwable throwable);

    /**
     * Setzt den Status der View.
     */
    void restoreState();

    /**
     * Speichert den Status der View.
     */
    void saveState();
}
