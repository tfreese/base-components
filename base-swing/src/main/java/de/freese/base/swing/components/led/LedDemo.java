// Created: 28.12.2020
package de.freese.base.swing.components.led;

import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import de.freese.base.swing.components.led.token.ArrowToken;
import de.freese.base.swing.components.led.token.ArrowToken.ArrowDirection;
import de.freese.base.swing.components.led.token.NumberToken;
import de.freese.base.swing.components.led.token.TextToken;

/**
 * @author Thomas Freese
 */
public class LedDemo
{
    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        LedPanel ledPanel = new LedPanel();

        JFrame frame = new JFrame("LED Panel");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setBackground(Color.BLACK);

        // frame.setUndecorated(true);
        // frame.setOpacity(0.55F);
        frame.setResizable(true);
        frame.add(ledPanel);
        frame.setSize(800, 100);
        frame.setLocationRelativeTo(null);

        SwingUtilities.invokeLater(() -> {
            frame.setVisible(true);
        });

        ledPanel.addToken(new TextToken("A"));
        ledPanel.addToken(new TextToken("B"));
        ledPanel.addToken(new TextToken("%"));
        ledPanel.addToken(new TextToken("?"));
        ledPanel.addToken(new TextToken("0"));
        ledPanel.addToken(new NumberToken(Color.RED, 1.9D, "%4.1f"));
        ledPanel.addToken(new ArrowToken(ArrowDirection.UP));
        ledPanel.addToken(new ArrowToken(ArrowDirection.DOWN));
        ledPanel.addToken(new ArrowToken(ArrowDirection.UNCHANGED));
        ledPanel.addToken(new ArrowToken(ArrowDirection.LEFT));
        ledPanel.addToken(new ArrowToken(ArrowDirection.RIGHT));
    }
}
