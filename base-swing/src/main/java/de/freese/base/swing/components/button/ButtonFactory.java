package de.freese.base.swing.components.button;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import de.freese.base.swing.ui.HtmlTextButtonUI;
import de.freese.base.utils.FontUtils;

/**
 * @author Thomas Freese
 */
public final class ButtonFactory {
    /**
     * Enum for Arrows of "Move"-Buttons from the Marvosym-Font.
     *
     * @author Thomas Freese
     */
    public enum ArrowDirection {
        DOWN(187),
        LEFT(182),
        RIGHT(183),
        UP(186);

        private final String text;

        ArrowDirection(final int value) {
            this.text = String.valueOf((char) value);
        }

        public String getText() {
            return this.text;
        }
    }

    public static JButton createHTMLTextButton() {
        return createHTMLTextButton(Color.BLUE);
    }

    public static JButton createHTMLTextButton(final Color rolloverColor) {
        final JButton button = new JButton();

        decorateToHTMLButton(button, rolloverColor);

        return button;
    }

    public static JButton createMoveToolBarButton16x16(final ArrowDirection direction) {
        final JButton button = createToolbarButton16x16();

        decorateMoveButton(button, direction, 15);

        return button;
    }

    public static JButton createToolbarButton(final int size) {
        final JButton button = new JButton();

        button.setBorder(BorderFactory.createEmptyBorder());
        button.setFocusPainted(false);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setPreferredSize(new Dimension(size, size));
        button.setMaximumSize(new Dimension(size, size));

        button.setRequestFocusEnabled(false);
        button.setFocusable(false);
        button.setOpaque(false);
        button.setText(null);

        return button;
    }

    public static JButton createToolbarButton16x16() {
        return createToolbarButton(16);
    }

    public static void decorateMoveButton(final JButton button, final ArrowDirection direction, final int fontSize) {
        button.setFont(FontUtils.getSymbolFont().deriveFont(Font.PLAIN, fontSize));
        button.setText(direction.getText());
    }

    public static void decorateToHTMLButton(final JButton button, final Color rolloverColor) {
        button.setUI(new HtmlTextButtonUI(rolloverColor));
        button.setBorderPainted(false);
        button.setBorder(null);

        // button.setHorizontalTextPosition(SwingConstants.LEFT);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setRolloverEnabled(true);
        button.setOpaque(false);
    }

    private ButtonFactory() {
        super();
    }
}
