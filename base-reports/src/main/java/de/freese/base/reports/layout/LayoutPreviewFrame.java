// Created: 15.04.2008
package de.freese.base.reports.layout;

import java.awt.HeadlessException;
import java.awt.Image;
import java.io.Serial;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

/**
 * Übersicht des Layouts.
 *
 * @author Thomas Freese
 */
public class LayoutPreviewFrame extends JFrame {
    @Serial
    private static final long serialVersionUID = -2245301418603208848L;

    public LayoutPreviewFrame(final AbstractLayoutElement layoutElement) throws HeadlessException {
        super();

        final Image designImage = layoutElement.createImage();

        // Darstellung
        final ImageIcon imageIcon = new ImageIcon(designImage);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        final JLabel label = new JLabel();
        label.setIcon(imageIcon);

        getContentPane().add(label);

        pack();
    }
}
