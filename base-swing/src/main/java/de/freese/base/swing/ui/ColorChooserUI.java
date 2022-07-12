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
 * UI des ColorChooser mit erweitertem Panel von SwingX.
 *
 * @author Thomas Freese
 */
public class ColorChooserUI extends BasicColorChooserUI
{
    /**
     * Erzeugt eine UI Instanz.
     *
     * @param c {@link JComponent}
     *
     * @return {@link ComponentUI}
     */
    public static ComponentUI createUI(final JComponent c)
    {
        return new ColorChooserUI();
    }

    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JColorChooser colorChooser = new JColorChooser();
        colorChooser.addChooserPanel(new EyeDropperColorChooserPanel());

        frame.getContentPane().add(BorderLayout.CENTER, colorChooser);
        frame.setSize(300, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * @see javax.swing.plaf.basic.BasicColorChooserUI#createDefaultChoosers()
     */
    @Override
    protected AbstractColorChooserPanel[] createDefaultChoosers()
    {
        AbstractColorChooserPanel[] chooserPanels = super.createDefaultChoosers();

        // SwingX Panel dranhängen
        AbstractColorChooserPanel[] panels = new AbstractColorChooserPanel[chooserPanels.length + 1];
        System.arraycopy(chooserPanels, 0, panels, 0, chooserPanels.length);

        panels[chooserPanels.length] = new EyeDropperColorChooserPanel();

        return panels;
    }
}
