// Created: 13.11.22
package de.freese.base.swing.components.label;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

/**
 * @author Thomas Freese
 */
public final class FarbCodeLabelMain {
    public static void main(final String[] args) {
        final JFrame frame = new JFrame("GlassPaneDemo");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.setSize(new Dimension(400, 400));

        FarbCodeLabel farbCodeLabel = new FarbCodeLabel();

        frame.getContentPane().add(new JLabel("Click on Colour to choose new"), BorderLayout.NORTH);
        frame.getContentPane().add(farbCodeLabel, BorderLayout.CENTER);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private FarbCodeLabelMain() {
        super();
    }
}
