package de.freese.base.swing.components.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import de.freese.base.core.model.provider.TableColorProvider;
import de.freese.base.core.model.provider.TableFontProvider;
import de.freese.base.core.model.provider.TableIconProvider;
import de.freese.base.core.model.provider.TableLabelProvider;

/**
 * {@link TableCellRenderer} fuer die Verwendung der Provider-Interfaces.<br>
 * Sind keine Provider gesetzt wird das Value selbst versucht als Provider zu verwenden.
 * 
 * @author Thomas Freese
 */
public class ProviderTableCellRenderer extends DefaultTableCellRenderer
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3741789552079162522L;

	/**
	 * 
	 */
	private TableColorProvider colorProvider = null;

	/**
	 * 
	 */
	private TableFontProvider fontProvider = null;

	/**
	 * 
	 */
	private TableIconProvider iconProvider = null;

	/**
	 * 
	 */
	private TableLabelProvider labelProvider = null;

	/**
	 * Erstellt ein neues {@link ProviderTableCellRenderer} Object.
	 */
	public ProviderTableCellRenderer()
	{
		super();
	}

	/**
	 * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable,
	 *      java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value,
													final boolean isSelected,
													final boolean hasFocus, final int row,
													final int column)
	{
		setToolTipText(null);

		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		renderLabelProvider(value, column);
		renderColorProvider(value, column);
		renderFontProvider(value, column);
		renderIconProvider(value, column);

		return this;
	}

	/**
	 * @param value Object
	 * @param column int
	 */
	private void renderColorProvider(final Object value, final int column)
	{
		TableColorProvider colorProvider = this.colorProvider;

		if ((colorProvider == null) && (value instanceof TableColorProvider))
		{
			colorProvider = (TableColorProvider) value;
		}

		if (colorProvider == null)
		{
			return;
		}

		Color foreground = colorProvider.getForeground(value, column);

		if (foreground != null)
		{
			setForeground(foreground);
		}

		Color background = colorProvider.getBackground(value, column);

		if (background != null)
		{
			setBackground(background);
		}
	}

	/**
	 * @param value Object
	 * @param column int
	 */
	private void renderFontProvider(final Object value, final int column)
	{
		TableFontProvider fontProvider = this.fontProvider;

		if ((fontProvider == null) && (value instanceof TableFontProvider))
		{
			fontProvider = (TableFontProvider) value;
		}

		if (fontProvider == null)
		{
			return;
		}

		Font font = fontProvider.getFont(value, column);

		if (font != null)
		{
			setFont(font);
		}
	}

	/**
	 * @param value Object
	 * @param column int
	 */
	private void renderIconProvider(final Object value, final int column)
	{
		TableIconProvider iconProvider = this.iconProvider;

		if ((iconProvider == null) && (value instanceof TableIconProvider))
		{
			iconProvider = (TableIconProvider) value;
		}

		if (iconProvider == null)
		{
			return;
		}

		Icon icon = iconProvider.getIcon(value, column);

		if (icon != null)
		{
			setIcon(icon);
		}
	}

	/**
	 * @param value Object
	 * @param column int
	 * @throws NullPointerException if no {@link TableLabelProvider} is set and value is not
	 *             instance of
	 */
	private void renderLabelProvider(final Object value, final int column)
	{
		TableLabelProvider labelProvider = this.labelProvider;

		if ((labelProvider == null) && (value instanceof TableLabelProvider))
		{
			labelProvider = (TableLabelProvider) value;
		}

		if (labelProvider == null)
		{
			setText(value != null ? value.toString() : null);
			setToolTipText(null);

			return;
		}

		String text = labelProvider.getText(value, column);
		setText(text);

		String tooltip = labelProvider.getTooltip(value, column);
		setToolTipText(tooltip);
	}

	/**
	 * @param colorProvider {@link TableColorProvider}
	 */
	public void setColorProvider(final TableColorProvider colorProvider)
	{
		this.colorProvider = colorProvider;
	}

	/**
	 * @param fontProvider {@link TableFontProvider}
	 */
	public void setFontProvider(final TableFontProvider fontProvider)
	{
		this.fontProvider = fontProvider;
	}

	/**
	 * @param iconProvider {@link TableIconProvider}
	 */
	public void setIconProvider(final TableIconProvider iconProvider)
	{
		this.iconProvider = iconProvider;
	}

	/**
	 * @param labelProvider {@link TableLabelProvider}
	 */
	public void setLabelProvider(final TableLabelProvider labelProvider)
	{
		this.labelProvider = labelProvider;
	}
}
