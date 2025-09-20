package de.freese.base.swing.ui;

import java.awt.BorderLayout;

import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicColorChooserUI;

import org.jdesktop.swingx.color.EyeDropperColorChooserPanel;

/**
 * @author Thomas Freese
 */
public class ColorChooserUI extends BasicColorChooserUI {
    public static ComponentUI createUI(final JComponent c) {
        return new ColorChooserUI();
    }

    static void main() {
        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        final JColorChooser colorChooser = new JColorChooser();
        colorChooser.addChooserPanel(new EyeDropperColorChooserPanel());

        frame.getContentPane().add(BorderLayout.CENTER, colorChooser);
        frame.setSize(300, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    @Override
    protected AbstractColorChooserPanel[] createDefaultChoosers() {
        final AbstractColorChooserPanel[] chooserPanels = super.createDefaultChoosers();

        // SwingX Panel dranhängen
        final AbstractColorChooserPanel[] panels = new AbstractColorChooserPanel[chooserPanels.length + 1];
        System.arraycopy(chooserPanels, 0, panels, 0, chooserPanels.length);

        panels[chooserPanels.length] = new EyeDropperColorChooserPanel();

        return panels;
    }
}
