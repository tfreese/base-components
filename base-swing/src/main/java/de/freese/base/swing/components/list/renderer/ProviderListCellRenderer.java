package de.freese.base.swing.components.list.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import de.freese.base.core.model.provider.ColorProvider;
import de.freese.base.core.model.provider.FontProvider;
import de.freese.base.core.model.provider.IconProvider;
import de.freese.base.core.model.provider.TextProvider;

/**
 * {@link ListCellRenderer} fuer die Verwendung der Provider-Interfaces.<br>
 * Sind keine Provider gesetzt wird das Value selbst versucht als Provider zu verwenden.
 * 
 * @author Thomas Freese
 */
public class ProviderListCellRenderer extends DefaultListCellRenderer
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8052078503640380397L;

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
	 * Erstellt ein neues {@link ProviderListCellRenderer} Object.
	 */
	public ProviderListCellRenderer()
	{
		super();
	}

	/**
	 * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList,
	 *      java.lang.Object, int, boolean, boolean)
	 */
	@Override
	public Component getListCellRendererComponent(final JList<?> list, final Object value,
													final int index, final boolean isSelected,
													final boolean cellHasFocus)
	{
		setToolTipText(null);

		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

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

		if (textProvider != null)
		{
			setText(textProvider.getText(value));
			setToolTipText(textProvider.getTooltip(value));
		}
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
