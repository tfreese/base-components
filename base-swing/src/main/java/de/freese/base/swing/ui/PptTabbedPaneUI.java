package de.freese.base.swing.ui;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

/**
 * @author Thomas Freese
 */
public class PptTabbedPaneUI extends BasicTabbedPaneUI {
    public static ComponentUI createUI(final JComponent c) {
        return new PptTabbedPaneUI();
    }

    @Override
    protected void paintText(final Graphics g, final int tabPlacement, final Font font, final FontMetrics metrics, final int tabIndex, final String title, final Rectangle textRect, final boolean isSelected) {
        if (isSelected) {
            Font boldFont = this.tabPane.getFont().deriveFont(Font.BOLD);
            FontMetrics boldFontMetrics = this.tabPane.getFontMetrics(boldFont);

            int vDifference = (int) (boldFontMetrics.getStringBounds(title, g).getWidth()) - textRect.width;
            textRect.x -= (vDifference / 2);

            super.paintText(g, tabPlacement, boldFont, boldFontMetrics, tabIndex, title, textRect, isSelected);
        }
        else {
            super.paintText(g, tabPlacement, font, metrics, tabIndex, title, textRect, isSelected);
        }
    }
}
