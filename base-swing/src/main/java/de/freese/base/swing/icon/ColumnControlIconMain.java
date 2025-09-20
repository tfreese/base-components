// Created: 13.11.22
package de.freese.base.swing.icon;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

/**
 * @author Thomas Freese
 */
public final class ColumnControlIconMain {
    static void main() {
        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        final JLabel label = new JLabel(new ColumnControlIcon());
        frame.getContentPane().add(BorderLayout.CENTER, label);
        frame.setSize(300, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private ColumnControlIconMain() {
        super();
    }
}
