package de.freese.base.swing.components.label;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serial;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JToggleButton;
import javax.swing.Timer;
import javax.swing.WindowConstants;

/**
 * Label mit einer drehenden Sanduhr.
 *
 * @author Thomas Freese
 */
public class BusySanduhrLabel extends JLabel {
    @Serial
    private static final long serialVersionUID = -1861610997435401369L;

    public static void main(final String[] args) {
        final JFrame frame = new JFrame(BusySanduhrLabel.class.getSimpleName());
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.setSize(new Dimension(400, 400));

        final BusySanduhrLabel busyLabel = new BusySanduhrLabel("BusyLabel");
        // busyLabel.setIcon(ImageUtils.createEmptyIcon());

        final JToggleButton jToggleButton = new JToggleButton("Start/Stop");
        jToggleButton.addActionListener(event -> busyLabel.setBusy(jToggleButton.isSelected()));

        frame.getContentPane().add(jToggleButton, BorderLayout.NORTH);
        frame.getContentPane().add(busyLabel, BorderLayout.CENTER);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent event) {
                // Otherwise the Timer is still running and the JRE won't exit.
                busyLabel.setBusy(false);
            }
        });
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private final Timer animateTimer;
    private final ImageIcon[] icons;

    private int imageIndex;

    public BusySanduhrLabel() {
        this(null);
    }

    public BusySanduhrLabel(final String text) {
        this(text, WaitIcons.getWaitIcons());
    }

    public BusySanduhrLabel(final String text, final ImageIcon[] icons) {
        super(text);

        this.icons = Arrays.copyOf(icons, icons.length);

        this.animateTimer = new Timer(150, event -> performAnimation());
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
            setIcon(null);
            repaint();
        }
    }

    protected void performAnimation() {
        if (!isVisible()) {
            animateTimer.stop();

            return;
        }

        imageIndex++;

        if (imageIndex == icons.length) {
            imageIndex = 0;
        }

        setIcon(icons[imageIndex]);

        repaint();

        // The Toolkit.getDefaultToolkit().sync() synchronises the painting on systems that buffer graphics events.
        // Without this line, the animation might not be smooth on Linux.
        Toolkit.getDefaultToolkit().sync();
    }
}
