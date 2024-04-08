package de.freese.base.swing.task.inputblocker;

import java.awt.Component;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JRootPane;

import de.freese.base.swing.TranslucentGlassPane;

/**
 * InputBlocker der die gesamte Anwendung mit einer GlassPane blockiert.
 *
 * @author Thomas Freese
 */
public abstract class AbstractGlassPaneInputBlocker extends AbstractInputBlocker<Component> {
    private JComponent glassPane;

    protected AbstractGlassPaneInputBlocker(final Component target) {
        super();

        addTarget(target);
        setChangeMouseCursor(true);
    }

    @Override
    public void block() {
        setGlassPaneVisible(true);
    }

    @Override
    public void unblock() {
        setGlassPaneVisible(false);
    }

    protected JComponent getGlassPane() {
        if (this.glassPane == null) {
            final TranslucentGlassPane gp = new TranslucentGlassPane();
            gp.setShowDelayMillies(100);
            gp.setTimerIncrementMillies(10);

            this.glassPane = gp;
        }

        return this.glassPane;
    }

    protected void setGlassPaneVisible(final boolean visible) {
        final JRootPane rootPane = getRootPane();

        if (rootPane == null) {
            getGlassPane().setVisible(true);
            setMouseCursorBusy(visible);

            return;
        }

        if (visible) {
            rootPane.setGlassPane(getGlassPane());
            getGlassPane().setVisible(true);
        }
        else {
            rootPane.getGlassPane().setVisible(false);
            rootPane.setGlassPane(Box.createGlue());
        }

        setMouseCursorBusy(visible);
    }
}
