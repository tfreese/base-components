package de.freese.base.swing.ui;

import javax.swing.JComponent;
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
