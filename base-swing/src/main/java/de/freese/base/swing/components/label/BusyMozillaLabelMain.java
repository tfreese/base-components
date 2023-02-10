// Created: 13.11.22
package de.freese.base.swing.components.label;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * @author Thomas Freese
 */
public final class BusyMozillaLabelMain {
    public static void main(final String[] args) {
        final JFrame frame = new JFrame("GlassPaneDemo");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.setSize(new Dimension(400, 400));

        BusyMozillaLabel mozillaLabel = new BusyMozillaLabel("Taeschd");

        // mozillaLabel.setTrail(7);
        frame.getContentPane().add(mozillaLabel, BorderLayout.NORTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private BusyMozillaLabelMain() {
        super();
    }
}
