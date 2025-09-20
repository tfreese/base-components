// Created: 24 Juli 2025
package de.freese.base.swing.components.label;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JToggleButton;
import javax.swing.WindowConstants;

/**
 * @author Thomas Freese
 */
public final class AnimatedDemo {
    static void main() {
        final JFrame frame = new JFrame(AnimatedDemo.class.getSimpleName());
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(new GridLayout(3, 1));
        frame.setSize(new Dimension(300, 150));

        final AnimatedIcon animatedIconHourGlass = new AnimatedIconHourGlass();
        final AnimatedLabel animatedLabelHourGlass = new AnimatedLabel("HourGlass", animatedIconHourGlass);
        animatedLabelHourGlass.setBorder(BorderFactory.createLineBorder(Color.RED));

        final AnimatedIconMozilla animatedIconMozilla = new AnimatedIconMozilla();
        final AnimatedLabel animatedLabelMozilla = new AnimatedLabel("Mozilla", animatedIconMozilla);
        animatedLabelMozilla.setBorder(BorderFactory.createLineBorder(Color.RED));

        animatedIconMozilla.setColorFirst(Color.BLUE);
        animatedIconMozilla.setColorLast(Color.RED);
        animatedIconMozilla.setIconSize(25);
        // animatedIconMozilla.setCircleCount(16);
        // animatedIconMozilla.setTrailCount(6);

        final JToggleButton jToggleButton = new JToggleButton("Start/Stop");
        jToggleButton.addActionListener(event -> {
            animatedLabelHourGlass.setBusy(jToggleButton.isSelected());
            animatedLabelMozilla.setBusy(jToggleButton.isSelected());
        });

        frame.getContentPane().add(jToggleButton);
        frame.getContentPane().add(animatedLabelHourGlass);
        frame.getContentPane().add(animatedLabelMozilla);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent event) {
                // Otherwise, the Timer is still running and the JRE won't exit.
                animatedLabelHourGlass.setBusy(false);
                animatedLabelMozilla.setBusy(false);
            }
        });
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private AnimatedDemo() {
        super();
    }
}
