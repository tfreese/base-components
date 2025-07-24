package de.freese.base.swing.components.label;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serial;

import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import de.freese.base.utils.GuiUtils;

/**
 * JLabel as a ColorPicker.
 *
 * @author Thomas Freese
 */
public class FarbCodeLabel extends JLabel {
    public static final String BACKGROUND_CHANGED = "BACKGROUND_CHANGED";

    @Serial
    private static final long serialVersionUID = 1L;

    public static void main(final String[] args) {
        final JFrame frame = new JFrame(FarbCodeLabel.class.getSimpleName());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new GridLayout(2, 1));
        frame.setSize(new Dimension(300, 200));

        final FarbCodeLabel farbCodeLabel = new FarbCodeLabel();

        frame.getContentPane().add(new JLabel("Click on Colour to choose new"));
        frame.getContentPane().add(farbCodeLabel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

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

                    // Own Event, because 'background' is fired too often.
                    firePropertyChange(BACKGROUND_CHANGED, oldRGB, newColor.getRGB());
                }
            }
        });
    }
}
