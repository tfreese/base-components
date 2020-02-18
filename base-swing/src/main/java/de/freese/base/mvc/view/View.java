package de.freese.base.mvc.view;

import javax.swing.JComponent;
import de.freese.base.core.model.Initializeable;
import de.freese.base.core.release.ReleasePrepareable;
import de.freese.base.core.release.Releaseable;
import de.freese.base.mvc.process.BusinessProcess;
import de.freese.base.swing.ComponentProvider;

/**
 * Interface einer IView.
 *
 * @author Thomas Freese
 */
public interface View extends ComponentProvider, Initializeable, Releaseable, ReleasePrepareable
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
