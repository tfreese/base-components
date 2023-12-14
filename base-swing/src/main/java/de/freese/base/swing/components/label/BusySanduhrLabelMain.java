// Created: 13.11.22
package de.freese.base.swing.components.label;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import de.freese.base.utils.ImageUtils;

/**
 * @author Thomas Freese
 */
public final class BusySanduhrLabelMain {
    public static void main(final String[] args) {
        final JFrame frame = new JFrame("GlassPaneDemo");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.setSize(new Dimension(400, 400));

        final BusySanduhrLabel busySanduhrLabel = new BusySanduhrLabel("Taeschd");
        busySanduhrLabel.setIcon(ImageUtils.createEmptyIcon());

        frame.getContentPane().add(busySanduhrLabel, BorderLayout.NORTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private BusySanduhrLabelMain() {
        super();
    }
}
