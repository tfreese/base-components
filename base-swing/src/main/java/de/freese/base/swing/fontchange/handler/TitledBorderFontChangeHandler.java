package de.freese.base.swing.fontchange.handler;

import java.awt.Font;

import javax.swing.border.TitledBorder;

import de.freese.base.swing.fontchange.FontChangeHandler;

/**
 * @author Thomas Freese
 */
public class TitledBorderFontChangeHandler implements FontChangeHandler {
    /**
     * @see de.freese.base.swing.fontchange.FontChangeHandler#fontChanged(java.awt.Font, java.lang.Object)
     */
    @Override
    public void fontChanged(final Font newFont, final Object object) {
        TitledBorder titledBorder = (TitledBorder) object;

        Font oldFont = titledBorder.getTitleFont();

        if (oldFont == null) {
            titledBorder.setTitleFont(newFont);
        }
        else {
            titledBorder.setTitleFont(oldFont.deriveFont(newFont.getSize2D()));
        }
    }
}
