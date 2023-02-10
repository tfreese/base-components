// Created: 13.11.22
package de.freese.base.swing.components.text;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

/**
 * @author Thomas Freese
 */
public final class AutoCompleteableTextFieldMain {
    public static void main(final String[] args) {
        JFrame frame = new JFrame("AutoCompleteableTextField");

        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());

        JLabel label = new JLabel("Text: ");

        label.setFocusable(true);

        frame.getContentPane().add(label, BorderLayout.WEST);
        frame.getContentPane().add(new AutoCompleteableTextField(50), BorderLayout.CENTER);

        frame.pack();

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private AutoCompleteableTextFieldMain() {
        super();
    }
}
