package de.freese.base.swing.task.inputblocker;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JRootPane;
import javax.swing.RootPaneContainer;

import de.freese.base.swing.task.AbstractSwingTask;
import de.freese.base.utils.GuiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * InputBlocker können für einen {@link AbstractSwingTask} die GUI-Elemente für die Eingabe blockieren.
 *
 * @param <T> Konkreter Typ des Targets
 *
 * @author Thomas Freese
 */
public abstract class AbstractInputBlocker<T> implements InputBlocker
{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final List<T> targets = new ArrayList<>();

    private boolean changeMouseCursor;

    private JRootPane rootPane;

    /**
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    @Override
    public void propertyChange(final PropertyChangeEvent event)
    {
        // Empty
    }

    public void setRootPane(final JRootPane rootPane)
    {
        this.rootPane = rootPane;
    }

    protected void addTarget(final T target)
    {
        this.targets.add(target);
    }

    protected JRootPane detectRootPane()
    {
        JRootPane rp = null;

        for (T target : getTargets())
        {
            if (rp != null)
            {
                break;
            }

            if (target instanceof Component root)
            {
                RootPaneContainer rpc = null;

                while (root != null)
                {
                    if (root instanceof RootPaneContainer c)
                    {
                        rpc = c;
                        break;
                    }

                    root = root.getParent();
                }

                rp = rpc != null ? rpc.getRootPane() : null;
            }
        }

        // Fallback: Das aktuelle Fenster holen.
        if (rp == null)
        {
            Frame activeFrame = GuiUtils.getActiveFrame();

            if (activeFrame instanceof RootPaneContainer c)
            {
                rp = c.getRootPane();
            }
        }

        if (rp == null)
        {
            getLogger().warn("Keine JRootPane gefunden");
        }

        return rp;
    }

    protected Logger getLogger()
    {
        return this.logger;
    }

    protected JRootPane getRootPane()
    {
        if (this.rootPane != null)
        {
            return this.rootPane;
        }

        // Fallback: Von den Targets holen
        return detectRootPane();
    }

    /**
     * Liefert die zu blockenden Objekte (JComponent, Action etc.).
     */
    protected List<T> getTargets()
    {
        return this.targets;
    }

    protected void setChangeMouseCursor(final boolean changeMouseCursor)
    {
        this.changeMouseCursor = changeMouseCursor;
    }

    protected void setMouseCursorBusy(final boolean busy)
    {
        if (!this.changeMouseCursor)
        {
            return;
        }

        JRootPane rp = getRootPane();

        if (rp == null)
        {
            return;
        }

        if (busy)
        {
            rp.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        }
        else
        {
            rp.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }
}
