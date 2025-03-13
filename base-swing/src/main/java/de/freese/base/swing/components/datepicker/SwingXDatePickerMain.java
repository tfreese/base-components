// Created: 13.11.22
package de.freese.base.swing.components.datepicker;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * Alternative: <a href="https://github.com/LGoodDatePicker/LGoodDatePicker">LGoodDatePicker</a>
 *
 * @author Thomas Freese
 */
public final class SwingXDatePickerMain {
    public static void main(final String[] args) {
        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.getContentPane().add(BorderLayout.CENTER, new SwingXDatePicker());

        frame.setSize(300, 300);
        // frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private SwingXDatePickerMain() {
        super();
    }
}
