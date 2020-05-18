package de.freese.base.swing.task.inputblocker;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import javax.swing.JRootPane;
import javax.swing.RootPaneContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.base.utils.GuiUtils;

/**
 * InputBlocker koennen fuer einen AbstractTask GUI-Elemente fuer die Eingabe blockieren.
 *
 * @author Thomas Freese
 * @param <T> Konkreter Typ des Targets
 */
public abstract class AbstractInputBlocker<T> implements InputBlocker
{
    /**
     *
     */
    private boolean changeMouseCursor = false;

    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     *
     */
    private final T[] targets;

    /**
     * Erstellt ein neues {@link AbstractInputBlocker} Object.
     *
     * @param target Object
     * @param targets Object[]
     */
    @SuppressWarnings("unchecked")
    public AbstractInputBlocker(final T target, final T...targets)
    {
        super();

        T[] temp = Arrays.copyOf(targets, targets.length + 1);
        temp[targets.length] = target;

        this.targets = temp;
    }

    /**
     * Gibt die aktive {@link JRootPane} zurueck.
     *
     * @return {@link JRootPane}
     */
    protected JRootPane getActiveRootPane()
    {
        // Von den Targets holen
        JRootPane rootPane = null;

        for (T target : getTargets())
        {
            if (rootPane != null)
            {
                break;
            }

            if (target instanceof Component)
            {
                Component root = (Component) target;
                RootPaneContainer rpc = null;

                while (root != null)
                {
                    if (root instanceof RootPaneContainer)
                    {
                        rpc = (RootPaneContainer) root;
                        break;
                    }

                    root = root.getParent();
                }

                // Das aktuelle Fenster holen
                if (rpc == null)
                {
                    Frame activeFrame = GuiUtils.getActiveFrame();

                    if (activeFrame instanceof RootPaneContainer)
                    {
                        rpc = (RootPaneContainer) activeFrame;
                    }
                }

                rootPane = rpc != null ? rpc.getRootPane() : null;
            }
        }

        if (rootPane == null)
        {
            getLogger().warn("Keine JRootPane gefunden");
        }

        return rootPane;
        // if (rpc != null)
        // {
        // this.lastRootPane = rpc.getRootPane();
        // }
        //
        // return this.lastRootPane;
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }

    /**
     * Liefert die zu blockenden Objekte (JComponent, Action etc.).
     *
     * @return Object
     */
    protected T[] getTargets()
    {
        return this.targets;
    }

    /**
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    @Override
    public void propertyChange(final PropertyChangeEvent event)
    {
        // NO-OP
    }

    /**
     * Wenn false, wird der Mousecursor nicht veraendert.
     *
     * @param changeMouseCursor boolean
     */
    protected void setChangeMouseCursor(final boolean changeMouseCursor)
    {
        this.changeMouseCursor = changeMouseCursor;
    }

    /**
     * Aendert den MouseZeiger.
     *
     * @param busy boolean
     */
    protected void setMouseCursorBusy(final boolean busy)
    {
        if (!this.changeMouseCursor)
        {
            return;
        }

        JRootPane rootPane = getActiveRootPane();

        if (rootPane == null)
        {
            return;
        }

        if (busy)
        {
            rootPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        }
        else
        {
            rootPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }
}
