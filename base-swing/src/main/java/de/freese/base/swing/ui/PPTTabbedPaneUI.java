package de.freese.base.swing.ui;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

/**
 * An implementation of the TabbedPaneUI that looks like the tabs that are used in Microsoft
 * Powerpoint Copyright (C) 2005 by Jon Lipsky Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. Y ou may obtain a
 * copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable
 * law or agreed to in writing, software d istributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 * for the specific language governing permissions and limitations under the License.
 */
public class PPTTabbedPaneUI extends BasicTabbedPaneUI
{
	/**
	 * Creates a new {@link PPTTabbedPaneUI} object.
	 */
	public PPTTabbedPaneUI()
	{
		super();
	}

	/**
	 * @param c {@link JComponent}
	 * @return {@link ComponentUI}
	 */
	public static ComponentUI createUI(final JComponent c)
	{
		return new PPTTabbedPaneUI();
	}

	/**
	 * @see javax.swing.plaf.basic.BasicTabbedPaneUI#paintText(java.awt.Graphics, int,
	 *      java.awt.Font, java.awt.FontMetrics, int, java.lang.String, java.awt.Rectangle, boolean)
	 */
	@Override
	protected void paintText(final Graphics g, final int tabPlacement, final Font font,
								final FontMetrics metrics, final int tabIndex, final String title,
								final Rectangle textRect, final boolean isSelected)
	{
		if (isSelected)
		{
			Font boldFont = this.tabPane.getFont().deriveFont(Font.BOLD);
			FontMetrics boldFontMetrics = this.tabPane.getFontMetrics(boldFont);

			int vDifference =
					(int) (boldFontMetrics.getStringBounds(title, g).getWidth()) - textRect.width;
			textRect.x -= (vDifference / 2);

			super.paintText(g, tabPlacement, boldFont, boldFontMetrics, tabIndex, title, textRect,
					isSelected);
		}
		else
		{
			super.paintText(g, tabPlacement, font, metrics, tabIndex, title, textRect, isSelected);
		}
	}
}
