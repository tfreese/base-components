package de.freese.base.swing.components.tree;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import de.freese.base.core.model.NameProvider;
import de.freese.base.core.model.provider.IconProvider;

/**
 * {@link TreeCellRenderer} fuer {@link NameProvider} Objekte.
 * 
 * @author Thomas Freese
 */
public class NameProviderTreeCellRenderer extends DefaultTreeCellRenderer
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4513648521147965024L;

	/**
	 * Erstellt ein neues {@link NameProviderTreeCellRenderer} Objekt.
	 */
	public NameProviderTreeCellRenderer()
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
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

		if (value instanceof NameProvider)
		{
			NameProvider nameProvider = (NameProvider) value;
			setText(nameProvider.getName());
		}

		if (value instanceof IconProvider)
		{
			IconProvider iconProvider = (IconProvider) value;
			setIcon(iconProvider.getIcon(null));
		}

		return this;
	}
}
