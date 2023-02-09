package de.freese.base.swing.fontchange.handler;

import java.awt.Component;
import java.awt.Font;

import de.freese.base.swing.fontchange.FontChangeHandler;

/**
 * @author Thomas Freese
 */
public class ComponentFontChangeHandler implements FontChangeHandler
{
    /**
     * @see de.freese.base.swing.fontchange.FontChangeHandler#fontChanged(java.awt.Font, java.lang.Object)
     */
    @Override
    public void fontChanged(final Font newFont, final Object object)
    {
        Component component = (Component) object;

        Font oldFont = component.getFont();

        if (oldFont == null)
        {
            component.setFont(newFont);
        }
        else
        {
            component.setFont(oldFont.deriveFont(newFont.getSize2D()));
        }
    }
}
