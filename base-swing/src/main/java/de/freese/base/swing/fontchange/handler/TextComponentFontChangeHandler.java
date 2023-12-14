package de.freese.base.swing.fontchange.handler;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;

import javax.swing.text.JTextComponent;

/**
 * @author Thomas Freese
 */
public class TextComponentFontChangeHandler extends ComponentFontChangeHandler {
    @Override
    public void fontChanged(final Font newFont, final Object object) {
        super.fontChanged(newFont, object);

        final JTextComponent textComponent = (JTextComponent) object;

        final FontMetrics fontMetrics = textComponent.getFontMetrics(textComponent.getFont());
        int newHeight = fontMetrics.getMaxAscent() + fontMetrics.getMaxAscent() + 1;

        if (newFont.getSize() > 10) {
            newHeight -= fontMetrics.getDescent();
            newHeight -= 3;
        }

        final Dimension newSize = new Dimension(textComponent.getWidth(), newHeight);
        textComponent.setPreferredSize(newSize);
    }
}
