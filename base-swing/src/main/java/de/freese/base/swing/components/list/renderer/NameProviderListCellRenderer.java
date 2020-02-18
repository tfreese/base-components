package de.freese.base.swing.components.list.renderer;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import de.freese.base.core.model.NameProvider;

/**
 * @author Thomas Freese
 */
public class NameProviderListCellRenderer extends DefaultListCellRenderer
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6808587590135224961L;

	/**
	 * Erstellt ein neues {@link NameProviderListCellRenderer} Object.
	 */
	public NameProviderListCellRenderer()
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
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

		if (value instanceof NameProvider)
		{
			NameProvider nameProvider = (NameProvider) value;
			setText(nameProvider.getName());
		}

		return this;
	}
}
