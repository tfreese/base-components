package de.freese.base.swing.ui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.text.AttributedString;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicButtonListener;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;

/**
 * A ButtonUI that paints the button in HTML Style, as an underlined link upon rollover. <br>
 * If the button is enabled and rolled-over then the text of the button is painted blu and underlined.
 *
 * @author Thomas Freese
 */
public class HtmlTextButtonUI extends BasicButtonUI {
    private final Color rolloverColor;

    public HtmlTextButtonUI() {
        this(Color.BLUE);
    }

    public HtmlTextButtonUI(final Color rolloverColor) {
        super();

        this.rolloverColor = rolloverColor;
    }

    /**
     * Methode wurde überschrieben, da beim Öffnen neuer Panels, die sich über den Button legen, dieser das abschliessende RolloverEvent nicht bekommt und
     * blau unterstrichen bleibt.
     */
    @Override
    protected BasicButtonListener createButtonListener(final AbstractButton b) {
        return new BasicButtonListener(b) {
            @Override
            public void mouseReleased(final MouseEvent event) {
                super.mouseReleased(event);

                if (SwingUtilities.isLeftMouseButton(event)) {
                    final ButtonModel model = ((AbstractButton) event.getSource()).getModel();
                    model.setRollover(false);
                }
            }
        };
    }

    @Override
    protected void paintText(final Graphics g, final AbstractButton b, final Rectangle textRect, final String text) {
        super.paintText(g, b, textRect, text);

        if (b.getModel().isRollover() && b.getModel().isEnabled()) {
            final FontMetrics fm = g.getFontMetrics();
            final AttributedString s = new AttributedString(text);
            s.addAttribute(TextAttribute.FONT, b.getFont());
            s.addAttribute(TextAttribute.FOREGROUND, this.rolloverColor);
            s.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            g.drawString(s.getIterator(), textRect.x + getTextShiftOffset(), textRect.y + fm.getAscent() + getTextShiftOffset());
        }
    }

    @Override
    protected void paintText(final Graphics g, final JComponent c, final Rectangle textRect, final String text) {
        // Diese Methode wurde überschrieben, da der DisabledText um 1 Pixel nach Links
        // gerückt wurde und somit der erste Buchstabe abgeschnitten wurde.
        final AbstractButton b = (AbstractButton) c;
        final ButtonModel model = b.getModel();
        final FontMetrics fm = g.getFontMetrics();
        final int mnemonicIndex = b.getDisplayedMnemonicIndex();

        // Draw the Text
        if (model.isEnabled()) {
            // Paint the text normally
            g.setColor(b.getForeground());
            BasicGraphicsUtils.drawStringUnderlineCharAt(g, text, mnemonicIndex, textRect.x + getTextShiftOffset(), textRect.y + fm.getAscent() + getTextShiftOffset());
        }
        else {
            // Paint the text disabled
            g.setColor(b.getBackground().brighter());
            BasicGraphicsUtils.drawStringUnderlineCharAt(g, text, mnemonicIndex, textRect.x, textRect.y + fm.getAscent());
            g.setColor(b.getBackground().darker());

            // BasicGraphicsUtils.drawStringUnderlineCharAt(g,text, mnemonicIndex,
            // textRect.x - 1, textRect.y + fm.getAscent() - 1);
            BasicGraphicsUtils.drawStringUnderlineCharAt(g, text, mnemonicIndex, textRect.x, textRect.y + fm.getAscent());
        }
    }
}
