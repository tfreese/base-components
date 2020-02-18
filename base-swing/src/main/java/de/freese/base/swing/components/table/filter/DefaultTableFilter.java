package de.freese.base.swing.components.table.filter;

import de.freese.base.core.model.provider.TableContentProvider;

/**
 * Diese Klasse ermoeglicht das Spaltenweise Filtern der Zeilen einer Tabelle.
 * 
 * @author Thomas Freese
 */
public class DefaultTableFilter extends AbstractTableFilter
{
	/**
	 * 
	 */
	private final TableContentProvider tableContentProvider;

	/**
	 * Erstellt ein neues {@link DefaultTableFilter} Object.
	 * 
	 * @param tableContentProvider {@link TableContentProvider}
	 */
	public DefaultTableFilter(final TableContentProvider tableContentProvider)
	{
		super();

		this.tableContentProvider = tableContentProvider;
	}

	/**
	 * @see de.freese.base.core.model.provider.TableContentProvider#getValueAt(java.lang.Object, int)
	 */
	@Override
	public Object getValueAt(final Object object, final int column)
	{
		return this.tableContentProvider.getValueAt(object, column);
	}
}
