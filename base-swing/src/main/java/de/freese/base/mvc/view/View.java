package de.freese.base.mvc.view;

import javax.swing.JComponent;
import de.freese.base.mvc.process.BusinessProcess;
import de.freese.base.swing.ComponentProvider;
import de.freese.base.swing.exception.ReleaseVetoException;

/**
 * Interface einer IView.
 *
 * @author Thomas Freese
 */
public interface View extends ComponentProvider
{
    /**
     * Liefert den IBusinessProcess.
     *
     * @return {@link BusinessProcess}
     */
    public BusinessProcess getProcess();

    /**
     * Fehlerbehandlung.
     *
     * @param throwable {@link Throwable}
     */
    public void handleException(Throwable throwable);

    /**
     * Initialisiert die Viewt.
     */
    public void initialize();

    /**
     * Pruefung, ob das Release durchgefuehrt werden kann.
     *
     * @throws ReleaseVetoException Falls was schief geht.
     */
    public void prepareRelease() throws ReleaseVetoException;

    /**
     * Freigeben verwendeter Resourcen.
     */
    public void release();

    /**
     * Setzt den Status der IView.
     */
    public void restoreState();

    /**
     * Speichert den Status der IView.
     */
    public void saveState();

    /**
     * Setzt die Komponente der IView.
     *
     * @param component {@link JComponent}
     */
    public void setComponent(JComponent component);
}
