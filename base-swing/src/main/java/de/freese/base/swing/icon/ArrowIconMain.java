// Created: 13.11.22
package de.freese.base.swing.icon;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

/**
 * @author Thomas Freese
 */
public final class ArrowIconMain {
    static void main() {
        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        final JLabel label = new JLabel(new ArrowIcon(30, 30, SwingConstants.NORTH, Color.MAGENTA));
        frame.getContentPane().add(BorderLayout.CENTER, label);
        frame.setSize(300, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private ArrowIconMain() {
        super();
    }
}
