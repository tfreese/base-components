// Created: 13.11.22
package de.freese.base.swing.components.segment;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * @author Thomas Freese
 */
public final class Segment7Main {
    public static void main(final String[] args) {
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
