package de.freese.base.swing.fontchange.handler;

import java.awt.Component;
import java.awt.Font;

import de.freese.base.swing.fontchange.FontChangeHandler;

/**
 * @author Thomas Freese
 */
public class ComponentFontChangeHandler implements FontChangeHandler {
    @Override
    public void fontChanged(final Font newFont, final Object object) {
        final Component component = (Component) object;

        final Font oldFont = component.getFont();

        if (oldFont == null) {
            component.setFont(newFont);
        }
        else {
            component.setFont(oldFont.deriveFont(newFont.getSize2D()));
        }
    }
}
