package de.freese.base.swing.fontchange.handler;

import java.awt.Font;
import org.jdesktop.swingx.JXTitledPanel;

/**
 * Defaultimplementierung fuer die Fontaenderung eines {@link JXTitledPanel}.
 *
 * @author Thomas Freese
 */
public class TitledPanelFontChangeHandler extends ComponentFontChangeHandler
{
    /**
     * @see de.freese.base.swing.fontchange.handler.ComponentFontChangeHandler#fontChanged(java.awt.Font, java.lang.Object)
     */
    @Override
    public void fontChanged(final Font newFont, final Object object)
    {
        super.fontChanged(newFont, object);

        JXTitledPanel titledPanel = (JXTitledPanel) object;

        Font oldFont = titledPanel.getFont();

        // Nur die Schriftgroesse aendern
        if (oldFont == null)
        {
            titledPanel.setTitleFont(newFont.deriveFont(Font.BOLD));
        }
        else
        {
            titledPanel.setTitleFont(oldFont.deriveFont(Font.BOLD, newFont.getSize2D()));
        }
    }
}
