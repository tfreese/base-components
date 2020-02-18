package de.freese.base.demo.fibonacci.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.freese.base.core.model.Initializeable;

/**
 * Panel fuer die Fibonacci Demo.
 * 
 * @author Thomas Freese
 */
public class FibonacciPanel extends JPanel implements Initializeable
{
	/**
	 *
	 */
	private static final long serialVersionUID = -2648632168520329957L;

	/**
	 *
	 */
	private JButton buttonComponentBlock = null;

	/**
	 *
	 */
	private JButton buttonGlassPaneBlock = null;

	/**
	 *
	 */
	private JLabel label = null;

	/**
	 *
	 */
	private JLabel labelResult = null;

	/**
	 *
	 */
	private JTextField textField = null;

	/**
	 * Erstellt ein neues {@link FibonacciPanel} Object.
	 */
	public FibonacciPanel()
	{
		super();
	}

	/**
	 * @return {@link JButton}
	 */
	public JButton getButtonComponentBlock()
	{
		if (this.buttonComponentBlock == null)
		{
			this.buttonComponentBlock = new JButton();
		}

		return this.buttonComponentBlock;
	}

	/**
	 * @return {@link JButton}
	 */
	public JButton getButtonGlassPaneBlock()
	{
		if (this.buttonGlassPaneBlock == null)
		{
			this.buttonGlassPaneBlock = new JButton();
		}

		return this.buttonGlassPaneBlock;
	}

	/**
	 * @return {@link JLabel}
	 */
	public JLabel getLabel()
	{
		if (this.label == null)
		{
			this.label = new JLabel("#Fibonacci: Wert");
		}

		return this.label;
	}

	/**
	 * @return {@link JLabel}
	 */
	public JLabel getLabelResult()
	{
		if (this.labelResult == null)
		{
			this.labelResult = new JLabel();
		}

		return this.labelResult;
	}

	/**
	 * @return {@link JTextField}
	 */
	public JTextField getTextField()
	{
		if (this.textField == null)
		{
			this.textField = new JTextField();
		}

		return this.textField;
	}

	/**
	 * @see de.freese.base.core.model.Initializeable#initialize()
	 */
	@Override
	public void initialize()
	{
		setLayout(new GridBagLayout());
		setBorder(BorderFactory.createEmptyBorder());

		Insets insets = new Insets(5, 5, 5, 5);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = insets;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(getLabel(), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.insets = insets;
		gbc.gridwidth = 3;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(getTextField(), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridwidth = 2;
		gbc.insets = insets;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(getButtonGlassPaneBlock(), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridwidth = 2;
		gbc.insets = insets;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(getButtonComponentBlock(), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.weightx = 0;
		gbc.weighty = 1;
		gbc.gridwidth = 2;
		gbc.insets = insets;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		add(getLabelResult(), gbc);
	}
}
