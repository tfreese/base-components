package de.freese.base.swing.components.segment;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * @author Thomas Freese
 * @since 13.11.2022
 */
public final class Segment7Main {
    static void main() {
        final Segment7 seg = new Segment7();

        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(seg);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private Segment7Main() {
        super();
    }
}
