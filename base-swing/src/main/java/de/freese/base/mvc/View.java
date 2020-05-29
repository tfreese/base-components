package de.freese.base.mvc;

import java.awt.Component;

/**
 * Interface einer View.
 *
 * @author Thomas Freese
 * @param <C> Typ der Komponente
 */
public interface View<C extends Component>
{
    /**
     * Aufbau der GUI.
     */
    public void createGUI();

    /**
     * Liefert die Komponente der View.
     *
     * @return {@link Component}
     */
    public C getComponent();

    /**
     * Fehlerbehandlung.
     *
     * @param throwable {@link Throwable}
     */
    public void handleException(Throwable throwable);

    /**
     * Setzt den Status der View.
     */
    public void restoreState();

    /**
     * Speichert den Status der View.
     */
    public void saveState();
}
