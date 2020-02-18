package de.freese.base.demo.example.view;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import de.freese.base.core.model.Initializeable;

/**
 * Panel als Beispiel.
 * 
 * @author Thomas Freese
 */
public class ExamplePanel extends JPanel implements Initializeable
{
	/**
	 *
	 */
	private static final long serialVersionUID = -1162909250924225209L;

	/**
	 *
	 */
	private JButton buttonTaskStatistik = null;

	/**
	 * Erstellt ein neues {@link ExamplePanel} Object.
	 */
	public ExamplePanel()
	{
		super();
	}

	/**
	 * @return {@link JButton}
	 */
	protected JButton getButtonTaskStatistik()
	{
		return this.buttonTaskStatistik;
	}

	/**
	 * @see de.freese.base.core.model.Initializeable#initialize()
	 */
	@Override
	public void initialize()
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		this.buttonTaskStatistik = new JButton();
		add(this.buttonTaskStatistik);
	}
}
