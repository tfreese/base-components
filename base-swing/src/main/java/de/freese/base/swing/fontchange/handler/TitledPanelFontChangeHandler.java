package de.freese.base.swing.fontchange.handler;

import java.awt.Font;

import org.jdesktop.swingx.JXTitledPanel;

/**
 * @author Thomas Freese
 */
public class TitledPanelFontChangeHandler extends ComponentFontChangeHandler {
    @Override
    public void fontChanged(final Font newFont, final Object object) {
        super.fontChanged(newFont, object);

        JXTitledPanel titledPanel = (JXTitledPanel) object;

        Font oldFont = titledPanel.getFont();

        if (oldFont == null) {
            titledPanel.setTitleFont(newFont.deriveFont(Font.BOLD));
        }
        else {
            titledPanel.setTitleFont(oldFont.deriveFont(Font.BOLD, newFont.getSize2D()));
        }
    }
}
