package de.freese.base.swing.fontchange.handler;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import javax.swing.text.JTextComponent;

/**
 * Defaultimplementierung fuer die Fontaenderung einer {@link JTextComponent}.
 *
 * @author Thomas Freese
 */
public class TextComponentFontChangeHandler extends ComponentFontChangeHandler
{
    /**
     * @see de.freese.base.swing.fontchange.handler.ComponentFontChangeHandler#fontChanged(java.awt.Font, java.lang.Object)
     */
    @Override
    public void fontChanged(final Font newFont, final Object object)
    {
        super.fontChanged(newFont, object);

        JTextComponent textComponent = (JTextComponent) object;

        FontMetrics fontMetrics = textComponent.getFontMetrics(textComponent.getFont());
        int newHeight = fontMetrics.getMaxAscent() + fontMetrics.getMaxAscent() + 1;

        // Noch etwas abnehmen, damit die JTextComponent nich zu hoch wird
        if (newFont.getSize() > 10)
        {
            newHeight -= fontMetrics.getDescent();
            newHeight -= 3;
        }

        Dimension newSize = new Dimension(textComponent.getWidth(), newHeight);
        textComponent.setPreferredSize(newSize);
    }
}
