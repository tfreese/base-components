package de.freese.base.swing.components.label;

import java.awt.Toolkit;
import java.io.Serial;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/**
 * Label for {@link AnimatedIcon}.
 *
 * @author Thomas Freese
 */
public class AnimatedLabel extends JLabel {
    @Serial
    private static final long serialVersionUID = -1861610997435401369L;

    private final Timer animateTimer;

    public AnimatedLabel() {
        this(null, null);
    }

    public AnimatedLabel(final AnimatedIcon icon) {
        this(null, icon);
    }

    public AnimatedLabel(final String text) {
        this(text, null);
    }

    public AnimatedLabel(final String text, final AnimatedIcon icon) {
        super(text, icon, SwingConstants.LEADING);

        animateTimer = new Timer(150, event -> performAnimation());
    }

    @Override
    public AnimatedIcon getIcon() {
        return (AnimatedIcon) super.getIcon();
    }

    public boolean isRunning() {
        return animateTimer.isRunning();
    }

    public void setBusy(final boolean busy) {
        if (busy && !isRunning()) {
            animateTimer.start();
        }
        else if (!busy) {
            animateTimer.stop();

            // setIcon(null);
            if (getIcon() != null) {
                getIcon().reset();
            }

            repaint();
        }
    }

    public void setIcon(final AnimatedIcon icon) {
        super.setIcon(icon);
    }

    protected void performAnimation() {
        if (!isVisible() || getIcon() == null) {
            animateTimer.stop();

            return;
        }

        getIcon().next();

        repaint();

        // The Toolkit.getDefaultToolkit().sync() synchronises the painting on systems that buffer graphics events.
        // Without this line, the animation might not be smooth on Linux.
        Toolkit.getDefaultToolkit().sync();
    }
}
