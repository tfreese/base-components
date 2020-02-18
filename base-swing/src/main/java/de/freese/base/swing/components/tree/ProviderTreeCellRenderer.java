package de.freese.base.swing.components.tree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import de.freese.base.core.model.provider.ColorProvider;
import de.freese.base.core.model.provider.FontProvider;
import de.freese.base.core.model.provider.IconProvider;
import de.freese.base.core.model.provider.TextProvider;

/**
 * {@link TreeCellRenderer} fuer die Verwendung der Provider-Interfaces.<br>
 * Sind keine Provider gesetzt wird das Value selbst versucht als Provider zu verwenden.
 * 
 * @author Thomas Freese
 */
public class ProviderTreeCellRenderer extends DefaultTreeCellRenderer
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2850843340147042691L;

	/**
	 * 
	 */
	private ColorProvider colorProvider = null;

	/**
	 * 
	 */
	private FontProvider fontProvider = null;

	/**
	 * 
	 */
	private IconProvider iconProvider = null;

	/**
	 * 
	 */
	private TextProvider textProvider = null;

	/**
	 * Erstellt ein neues {@link ProviderTreeCellRenderer} Object.
	 */
	public ProviderTreeCellRenderer()
	{
		super();
	}

	/**
	 * @see javax.swing.tree.DefaultTreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree,
	 *      java.lang.Object, boolean, boolean, boolean, int, boolean)
	 */
	@Override
	public Component getTreeCellRendererComponent(final JTree tree, final Object value,
													final boolean sel, final boolean expanded,
													final boolean leaf, final int row,
													final boolean hasFocus)
	{
		setToolTipText(null);

		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

		renderTextProvider(value);
		renderColorProvider(value);
		renderFontProvider(value);
		renderIconProvider(value);

		return this;
	}

	/**
	 * @param value Object
	 */
	private void renderColorProvider(final Object value)
	{
		ColorProvider colorProvider = this.colorProvider;

		if ((colorProvider == null) && (value instanceof ColorProvider))
		{
			colorProvider = (ColorProvider) value;
		}

		if (colorProvider == null)
		{
			return;
		}

		Color foreground = colorProvider.getForeground(value);

		if (foreground != null)
		{
			setForeground(foreground);
		}

		Color background = colorProvider.getBackground(value);

		if (background != null)
		{
			setBackground(background);
		}
	}

	/**
	 * @param value Object
	 */
	private void renderFontProvider(final Object value)
	{
		FontProvider fontProvider = this.fontProvider;

		if ((fontProvider == null) && (value instanceof FontProvider))
		{
			fontProvider = (FontProvider) value;
		}

		if (fontProvider == null)
		{
			return;
		}

		Font font = fontProvider.getFont(value);

		if (font != null)
		{
			setFont(font);
		}
	}

	/**
	 * @param value Object
	 */
	private void renderIconProvider(final Object value)
	{
		IconProvider iconProvider = this.iconProvider;

		if ((iconProvider == null) && (value instanceof IconProvider))
		{
			iconProvider = (IconProvider) value;
		}

		if (iconProvider == null)
		{
			return;
		}

		Icon icon = iconProvider.getIcon(value);

		if (icon != null)
		{
			setIcon(icon);
		}
	}

	/**
	 * @param value Object
	 * @throws NullPointerException if no {@link TextProvider} is set and value is not instance of
	 */
	private void renderTextProvider(final Object value)
	{
		TextProvider textProvider = this.textProvider;

		if ((textProvider == null) && (value instanceof TextProvider))
		{
			textProvider = (TextProvider) value;
		}

		if (textProvider == null)
		{
			setText(value != null ? value.toString() : null);
			setToolTipText(null);

			return;
		}

		String text = textProvider.getText(value);
		setText(text);

		String tooltip = textProvider.getTooltip(value);
		setToolTipText(tooltip);
	}

	/**
	 * @param colorProvider {@link ColorProvider}
	 */
	public void setColorProvider(final ColorProvider colorProvider)
	{
		this.colorProvider = colorProvider;
	}

	/**
	 * @param fontProvider {@link FontProvider}
	 */
	public void setFontProvider(final FontProvider fontProvider)
	{
		this.fontProvider = fontProvider;
	}

	/**
	 * @param iconProvider {@link IconProvider}
	 */
	public void setIconProvider(final IconProvider iconProvider)
	{
		this.iconProvider = iconProvider;
	}

	/**
	 * @param textProvider {@link TextProvider}
	 */
	public void setLabelProvider(final TextProvider textProvider)
	{
		this.textProvider = textProvider;
	}
}
