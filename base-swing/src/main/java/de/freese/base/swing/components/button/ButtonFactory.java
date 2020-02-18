package de.freese.base.swing.components.button;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import de.freese.base.swing.font.SymbolFont;
import de.freese.base.swing.ui.HTMLTextButtonUI;

/**
 * Factory fuer verschiedene Buttontypen.
 * 
 * @author Thomas Freese
 */
public class ButtonFactory
{
	/**
	 * Enum fuer die Pfeilrichtung von "Move"-Buttons des Marvosym-Fonts.
	 * 
	 * @author Thomas Freese
	 */
	public enum ArrowDirection
	{
		/**
		 * 
		 */
		DOWN(187),

		/**
		 * 
		 */
		LEFT(182),

		/**
		 * 
		 */
		RIGHT(183),

		/**
		 * 
		 */
		UP(186);

		/**
		 * 
		 */
		private final String text;

		/**
		 * Erstellt ein neues {@link ArrowDirection} Object.
		 * 
		 * @param value int
		 */
		private ArrowDirection(final int value)
		{
			this.text = String.valueOf((char) value);
		}

		/**
		 * @return {@link String}
		 */
		public String getText()
		{
			return this.text;
		}
	}

	/**
	 * Erzeugt einen JButton, der wie ein HTML-Link aussieht.
	 * 
	 * @return {@link JButton}
	 */
	public static final JButton createHTMLTextButton()
	{
		return createHTMLTextButton(Color.BLUE);
	}

	/**
	 * Erzeugt einen JButton, der wie ein HTML-Link aussieht.
	 * 
	 * @param rolloverColor {@link Color}
	 * @return {@link JButton}
	 */
	public static final JButton createHTMLTextButton(final Color rolloverColor)
	{
		JButton button = new JButton();

		decorateToHTMLButton(button, rolloverColor);

		return button;
	}

	/**
	 * Liefert einen "Move"-Button mit 16 px Seitenlaenge.
	 * 
	 * @param direction {@link ArrowDirection}
	 * @return {@link JButton}
	 */
	public static final JButton createMoveToolBarButton16x16(final ArrowDirection direction)
	{
		JButton button = createToolbarButton16x16();
		decorateMoveButton(button, direction, 15);

		return button;
	}

	/**
	 * Liefert einen JButton mit bestimmter Seitenlaenge.
	 * 
	 * @param size Seitenlaenge des Buttons
	 * @return {@link JButton}
	 */
	public static final JButton createToolbarButton(final int size)
	{
		JButton button = new JButton();

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

	/**
	 * Liefert einen JButton mit 16 px Seitenlaenge.
	 * 
	 * @return {@link JButton}
	 */
	public static final JButton createToolbarButton16x16()
	{
		return createToolbarButton(16);
	}

	/**
	 * Dekoriert einen "Move"-Button.
	 * 
	 * @param button {@link JButton}
	 * @param direction {@link ArrowDirection}
	 * @param fontSize int
	 */
	public static final void decorateMoveButton(final JButton button,
												final ArrowDirection direction, final int fontSize)
	{
		button.setFont(SymbolFont.FONT_SYMBOL.deriveFont(Font.PLAIN, fontSize));
		button.setText(direction.getText());
	}

	/**
	 * Dekoriert einen JButton wie einen HTML-Link.
	 * 
	 * @param button {@link JButton}
	 * @param rolloverColor {@link Color}
	 */
	public static final void decorateToHTMLButton(final JButton button, final Color rolloverColor)
	{
		button.setUI(new HTMLTextButtonUI(rolloverColor));
		button.setBorderPainted(false);
		button.setBorder(null);

		// button.setHorizontalTextPosition(SwingConstants.LEFT);
		button.setHorizontalAlignment(SwingConstants.LEFT);
		button.setFocusPainted(false);
		button.setRolloverEnabled(true);
		button.setOpaque(false);
	}
}
