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
 * A ButtonUI that paints the button in HTML Style, as a underlined link upon rollover. <br>
 * If the button is enabled and rolled-over then the text of the button is painted blu and
 * underlined.
 * 
 * @author Thomas Freese
 */
public class HTMLTextButtonUI extends BasicButtonUI
{
	/**
     * 
     */
	private final Color rolloverColor;

	/**
	 * Creates a new {@link HTMLTextButtonUI} object.
	 */
	public HTMLTextButtonUI()
	{
		this(Color.BLUE);
	}

	/**
	 * Creates a new {@link HTMLTextButtonUI} object.
	 * 
	 * @param rolloverColor {@link Color}
	 */
	public HTMLTextButtonUI(final Color rolloverColor)
	{
		super();

		this.rolloverColor = rolloverColor;
	}

	/**
	 * Methode wurde ueberschrieben, da beim oeffnen neuer Panels, die sich ueber den Button legen,
	 * dieser das abschliessende RolloverEvent nicht bekommt und blau unterstrichen bleibt.
	 * 
	 * @see javax.swing.plaf.basic.BasicButtonUI#createButtonListener(javax.swing.AbstractButton)
	 */
	@Override
	protected BasicButtonListener createButtonListener(final AbstractButton b)
	{
		return new BasicButtonListener(b)
		{
			/**
			 * @see javax.swing.plaf.basic.BasicButtonListener#mouseReleased(java.awt.event.MouseEvent)
			 */
			@Override
			public void mouseReleased(final MouseEvent e)
			{
				super.mouseReleased(e);

				if (SwingUtilities.isLeftMouseButton(e))
				{
					ButtonModel model = ((AbstractButton) e.getSource()).getModel();
					model.setRollover(false);
				}
			}
		};
	}

	/**
	 * @see javax.swing.plaf.basic.BasicButtonUI#paintText(java.awt.Graphics,
	 *      javax.swing.JComponent, java.awt.Rectangle, java.lang.String)
	 */
	@Override
	protected void paintText(final Graphics g, final JComponent c, final Rectangle textRect,
								final String text)
	{
		// Diese Methode wurde ueberschrieben, da der DisabledText um 1 Pixel nach Links
		// gerueckt wurde und somit der erste Vuchstabe abgeschnitten wurde.
		AbstractButton b = (AbstractButton) c;
		ButtonModel model = b.getModel();
		FontMetrics fm = g.getFontMetrics();
		int mnemonicIndex = b.getDisplayedMnemonicIndex();

		// Draw the Text
		if (model.isEnabled())
		{
			// Paint the text normally
			g.setColor(b.getForeground());
			BasicGraphicsUtils.drawStringUnderlineCharAt(g, text, mnemonicIndex, textRect.x
					+ getTextShiftOffset(), textRect.y + fm.getAscent() + getTextShiftOffset());
		}
		else
		{
			// Paint the text disabled
			g.setColor(b.getBackground().brighter());
			BasicGraphicsUtils.drawStringUnderlineCharAt(g, text, mnemonicIndex, textRect.x,
					textRect.y + fm.getAscent());
			g.setColor(b.getBackground().darker());

			// BasicGraphicsUtils.drawStringUnderlineCharAt(g,text, mnemonicIndex,
			// textRect.x - 1, textRect.y + fm.getAscent() - 1);
			BasicGraphicsUtils.drawStringUnderlineCharAt(g, text, mnemonicIndex, textRect.x,
					textRect.y + fm.getAscent());
		}
	}

	/**
	 * @see javax.swing.plaf.basic.BasicButtonUI#paintText(java.awt.Graphics,
	 *      javax.swing.AbstractButton, java.awt.Rectangle, java.lang.String)
	 */
	@Override
	protected void paintText(final Graphics g, final AbstractButton b, final Rectangle textRect,
								final String text)
	{
		super.paintText(g, b, textRect, text);

		if (b.getModel().isRollover() && b.getModel().isEnabled())
		{
			FontMetrics fm = g.getFontMetrics();
			AttributedString s = new AttributedString(text);
			s.addAttribute(TextAttribute.FONT, b.getFont());
			s.addAttribute(TextAttribute.FOREGROUND, this.rolloverColor);
			s.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
			g.drawString(s.getIterator(), textRect.x + getTextShiftOffset(),
					textRect.y + fm.getAscent() + getTextShiftOffset());
		}
	}
}
