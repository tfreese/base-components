package de.freese.base.swing.components.table.filter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;

import javax.swing.JTable;

import de.freese.base.swing.components.table.ExtTable;
import de.freese.base.swing.components.table.header.FilterTableHeader;
import de.freese.base.swing.filter.Filter;
import de.freese.base.swing.filter.FilterCondition;
import de.freese.base.swing.filter.editor.FilterEditor;

/**
 * Verwaltet das Filtern ueber die Spalten einer {@link JTable}.<br>
 * Der FilterHandler registriert sich als Listener bei den {@link FilterEditor}en, um deren
 * Aenderungen an den TableFilter weiterzuleiten.
 * 
 * @author Thomas Freese
 */
public class TableFilterEditorHandler implements PropertyChangeListener
{
	/**
	 * Array mit den {@link FilterEditor} Objekten pro Spalte.
	 */
	private final FilterEditor[] editorArray;

	/**
	 * 
	 */
	private final ExtTable table;

	/**
	 * 
	 */
	private final AbstractTableFilter tableFilter;

	/**
	 * Erstellt ein neues {@link TableFilterEditorHandler} Object.
	 * 
	 * @param table {@link ExtTable}
	 * @param tableFilter {@link AbstractTableFilter}
	 */
	public TableFilterEditorHandler(final ExtTable table, final AbstractTableFilter tableFilter)
	{
		super();

		this.table = table;
		this.tableFilter = tableFilter;

		// 100 moegliche Spaltenfilter sollten reichen
		this.editorArray = new FilterEditor[100];
		Arrays.fill(this.editorArray, null);
	}

	/**
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(final PropertyChangeEvent evt)
	{
		FilterEditor editor = (FilterEditor) evt.getSource();
		Object filterContent = evt.getNewValue();

		FilterCondition filterCondition = this.tableFilter.getFilter(editor.getColumn());

		if (filterCondition instanceof Filter)
		{
			Filter filter = (Filter) filterCondition;
			filter.setFilterValue(filterContent);
		}
	}

	/**
	 * Installiert einen Spaltenfilter.
	 * 
	 * @param editor {@link FilterEditor}
	 * @param filter {@link Filter}
	 */
	public void addColumnFilter(final FilterEditor editor, final Filter filter)
	{
		if (editor != null)
		{
			FilterEditor oldEditor = this.editorArray[editor.getColumn()];

			if (oldEditor != null)
			{
				oldEditor.removePropertyChangeListener(oldEditor.getFilterPropertyName(), this);
			}

			this.editorArray[editor.getColumn()] = editor;

			editor.addPropertyChangeListener(editor.getFilterPropertyName(), this);

			this.tableFilter.setFilter(editor.getColumn(), filter);
		}
	}

	/**
	 * Liefert ein {@link FilterEditor} Objekt fuer eine Tabellenspalte.
	 * 
	 * @param columnIndex int
	 * @return {@link FilterEditor}
	 */
	public FilterEditor getFilterEditor(final int columnIndex)
	{
		if (columnIndex >= this.editorArray.length)
		{
			return null;
		}

		return this.editorArray[columnIndex];
	}

	/**
	 * Liefert die Tabelle.
	 * 
	 * @return {@link ExtTable}
	 */
	public ExtTable getTable()
	{
		return this.table;
	}

	/**
	 * Installiert die Tabellenfilterkomponente im ColummHeader der Scrollpane.
	 */
	public void installFilterColumnHeader()
	{
		FilterTableHeader filterHeader = new FilterTableHeader(this);
		getTable().installColumnHeader(filterHeader);
	}

	/**
	 * Installiert die Tabellenfilterkomponente im RowHeader der Scrollpane.
	 */
	public void installFilterRowHeader()
	{
		FilterTableHeader filterHeader = new FilterTableHeader(this);
		getTable().installRowHeader(filterHeader);
	}
}
