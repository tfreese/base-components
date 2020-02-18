package de.freese.base.swing.task.inputblocker;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import de.freese.base.swing.TranslucentGlassPane;
import de.freese.base.swing.task.AbstractTask;

/**
 * InputBlocker der die gesamte Anwendung mit einer GlassPane blockiert.
 *
 * @author Thomas Freese
 */
public abstract class AbstractGlassPaneInputBlocker extends AbstractInputBlocker<Component>
{
    /**
     * 
     */
    private JComponent glassPane = null;

    /**
     * Erstellt ein neues {@link AbstractGlassPaneInputBlocker} Object.
     * 
     * @param task {@link AbstractTask}
     * @param target {@link Component}
     */
    public AbstractGlassPaneInputBlocker(final AbstractTask<?, ?> task, final Component target)
    {
        super(task, target);

        setChangeMouseCursor(true);

        task.addPropertyChangeListener(event -> handleTaskEvent(event));
    }

    /**
     * @see de.freese.base.swing.task.inputblocker.InputBlocker#block()
     */
    @Override
    public void block()
    {
        setGlassPaneVisible(true);
    }

    /**
     * @return {@link JComponent}
     */
    protected JComponent getGlassPane()
    {
        if (this.glassPane == null)
        {
            TranslucentGlassPane gp = new TranslucentGlassPane();
            gp.setShowDelay(100);
            gp.setTimerIncrement(10);

            this.glassPane = gp;
        }

        return this.glassPane;
    }

    /**
     * Verarbeitet das Event des zur Zeit ausgefuehrten Tasks.
     * 
     * @param evt {@link PropertyChangeEvent}
     */
    protected abstract void handleTaskEvent(PropertyChangeEvent evt);

    /**
     * Einblenden der GlassPane.
     * 
     * @param visible boolean
     */
    protected void setGlassPaneVisible(final boolean visible)
    {
        JRootPane rootPane = getActiveRootPane();

        if (rootPane == null)
        {
            getGlassPane().setVisible(true);
            setMouseCursorBusy(visible);

            return;
        }

        if (visible)
        {
            rootPane.setGlassPane(getGlassPane());
            getGlassPane().setVisible(true);
        }
        else
        {
            rootPane.getGlassPane().setVisible(false);
            rootPane.setGlassPane(Box.createGlue());
        }

        setMouseCursorBusy(visible);
    }

    /**
     * @see de.freese.base.swing.task.inputblocker.InputBlocker#unblock()
     */
    @Override
    public void unblock()
    {
        setGlassPaneVisible(false);
    }
}
