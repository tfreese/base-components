package de.freese.base.swing.components.label;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serial;

import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JLabel;

import de.freese.base.utils.GuiUtils;

/**
 * JLabel für die Farbauswahl.
 *
 * @author Thomas Freese
 */
public class FarbCodeLabel extends JLabel {
    public static final String BACKGROUND_CHANGED = "BACKGROUND_CHANGED";

    @Serial
    private static final long serialVersionUID = 1L;

    public FarbCodeLabel() {
        super();

        initialize();
    }

    private void initialize() {
        setPreferredSize(new Dimension(100, 20));
        setBackground(Color.BLACK);
        setOpaque(true);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(final MouseEvent event) {
                final int oldRGB = getBackground().getRGB();

                final Frame activeFrame = GuiUtils.getActiveFrame();

                final Color newColor = JColorChooser.showDialog(activeFrame, "Choose Color", getBackground());

                if (newColor != null) {
                    setBackground(newColor);

                    // Eigenes Event, da background zu oft gefeuert wird.
                    firePropertyChange(BACKGROUND_CHANGED, oldRGB, newColor.getRGB());
                }
            }
        });
    }
}
