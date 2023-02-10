// Created: 13.11.22
package de.freese.base.swing.ui;

import java.awt.BorderLayout;

import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.jdesktop.swingx.color.EyeDropperColorChooserPanel;

/**
 * @author Thomas Freese
 */
public final class ColorChooserUIMain {
    public static void main(final String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JColorChooser colorChooser = new JColorChooser();
        colorChooser.addChooserPanel(new EyeDropperColorChooserPanel());

        frame.getContentPane().add(BorderLayout.CENTER, colorChooser);
        frame.setSize(300, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private ColorChooserUIMain() {
        super();
    }
}
