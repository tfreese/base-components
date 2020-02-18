package de.freese.base.swing.eventlist;

import javax.swing.RowFilter;
import javax.swing.RowFilter.Entry;

/**
 * Objekt fuer die Nutzung der {@link RowFilter}-API.
 * 
 * @author Thomas Freese
 * @param <M> Konkreter Typ des zu filternden Objektes.
 */
public class FilterEntry<M> extends Entry<M, Integer>
{
	/**
	 * 
	 */
	private final M model;

	/**
	 * 
	 */
	private final int column;

	/**
	 * Erstellt ein neues {@link FilterEntry} Object.
	 * 
	 * @param model Object
	 * @param column int
	 */
	public FilterEntry(final M model, final int column)
	{
		super();

		this.model = model;
		this.column = column;
	}

	/**
	 * @see javax.swing.RowFilter.Entry#getIdentifier()
	 */
	@Override
	public Integer getIdentifier()
	{
		return Integer.valueOf(this.column);
	}

	/**
	 * @see javax.swing.RowFilter.Entry#getModel()
	 */
	@Override
	public M getModel()
	{
		return this.model;
	}

	/**
	 * @see javax.swing.RowFilter.Entry#getValue(int)
	 */
	@Override
	public Object getValue(final int index)
	{
		throw new UnsupportedOperationException("getValue(int)");
	}

	/**
	 * @see javax.swing.RowFilter.Entry#getValueCount()
	 */
	@Override
	public int getValueCount()
	{
		return 1;
	}
}
