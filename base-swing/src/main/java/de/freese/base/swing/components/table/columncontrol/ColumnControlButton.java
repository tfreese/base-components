package de.freese.base.swing.components.table.columncontrol;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;

import de.freese.base.swing.components.table.ExtTable;
import de.freese.base.swing.components.table.column.IExtTableColumnModel;
import de.freese.base.swing.icon.ColumnControlIcon;

/**
 * Button fuer die Spaltenkontrolle.
 * 
 * @author Thomas Freese
 */
public class ColumnControlButton extends JButton
{
	/**
	 *
	 */
	private static final long serialVersionUID = -1209957795127294654L;

	/**
	 *
	 */
	private final List<ExtTable> tables = new ArrayList<>();

	/**
	 *
	 */
	private ColumnControlWindow columnControlWindow = null;

	/**
	 * Erstellt ein neues {@link ColumnControlButton} Object.
	 * 
	 * @param table {@link ExtTable}
	 */
	public ColumnControlButton(final ExtTable table)
	{
		super();

		if (!(table.getColumnModel() instanceof IExtTableColumnModel))
		{
			throw new IllegalArgumentException(
					"TableColumnModel muss vom Typ IExtTableColumnModel sein !");
		}

		this.tables.add(table);
		setIcon(new ColumnControlIcon());
		addActionListener(new ActionListener()
		{
			/**
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				getColumnControlWindow().clear();
				getColumnControlWindow().fill();
				getColumnControlWindow().show();
			}
		});
	}

	/**
	 * Popup Komponente erzeugen.
	 * 
	 * @return {@link ColumnControlWindow}
	 */
	protected ColumnControlWindow createColumnControlWindow()
	{
		return new ColumnControlWindow(this);
	}

	/**
	 * @return {@link ColumnControlWindow}
	 */
	private ColumnControlWindow getColumnControlWindow()
	{
		if (this.columnControlWindow == null)
		{
			this.columnControlWindow = createColumnControlWindow();
		}

		return this.columnControlWindow;
	}

	/**
	 * @return {@link List}
	 */
	public List<ExtTable> getTables()
	{
		return this.tables;
	}
}
