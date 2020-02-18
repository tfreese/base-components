package de.freese.base.swing.components.list.renderer;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;

/**
 * ListCellRenderer zum Rendern von {@link JCheckBox} und {@link JToggleButton}.
 * 
 * @author Thomas Freese
 */
public class ToggleButtonListCellRenderer implements ListCellRenderer<Object>
{
	/**
	 * Methode erstellt ein {@link ToggleButtonListCellRenderer}, der ein {@link JCheckBox}
	 * verwendet.
	 * 
	 * @return {@link ToggleButtonListCellRenderer}
	 */
	public static final ToggleButtonListCellRenderer createCheckBoxRenderer()
	{
		return new ToggleButtonListCellRenderer(new JCheckBox());
	}

	/**
	 * Methode erstellt ein {@link ToggleButtonListCellRenderer}, der ein {@link JRadioButton}
	 * verwendet.
	 * 
	 * @return {@link ToggleButtonListCellRenderer}
	 */
	public static final ToggleButtonListCellRenderer createRadioButtonRenderer()
	{
		return new ToggleButtonListCellRenderer(new JRadioButton());
	}

	/**
	 * 
	 */
	private DefaultListCellRenderer labelRenderer;

	/**
	 *
	 */
	private JToggleButton toggleButton;

	/**
	 * Erstellt ein neues {@link ToggleButtonListCellRenderer} Objekt. Konstruktor ist private, um
	 * eine direkte Erstellung zu verhindern.
	 * 
	 * @param toggleButton {@link JToggleButton}
	 */
	private ToggleButtonListCellRenderer(final JToggleButton toggleButton)
	{
		super();

		this.toggleButton = toggleButton;
		this.toggleButton.setHorizontalAlignment(SwingConstants.CENTER);
		this.labelRenderer = new DefaultListCellRenderer();
		this.labelRenderer.setHorizontalAlignment(SwingConstants.CENTER);
	}

	/**
	 * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList,
	 *      java.lang.Object, int, boolean, boolean)
	 */
	@Override
	public Component getListCellRendererComponent(final JList<?> list, final Object value,
													final int index, final boolean isSelected,
													final boolean cellHasFocus)
	{
		if (value instanceof Boolean)
		{
			if (isSelected)
			{
				this.toggleButton.setBackground(list.getSelectionBackground());
				this.toggleButton.setForeground(list.getSelectionForeground());
			}
			else
			{
				this.toggleButton.setBackground(list.getBackground());
				this.toggleButton.setForeground(list.getForeground());
			}

			this.toggleButton.setSelected(((Boolean) value).booleanValue());

			return this.toggleButton;
		}

		return this.labelRenderer.getListCellRendererComponent(list, value, index, isSelected,
				cellHasFocus);
	}
}
