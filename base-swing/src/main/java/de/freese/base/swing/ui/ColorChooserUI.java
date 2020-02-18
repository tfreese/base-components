package de.freese.base.swing.ui;

import javax.swing.JComponent;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicColorChooserUI;

import org.jdesktop.swingx.color.EyeDropperColorChooserPanel;

/**
 * UI des ColorChooser mit erweitertem Panel von SwingX.
 * 
 * @author Thomas Freese
 */
public class ColorChooserUI extends BasicColorChooserUI
{
	/**
	 * Creates a new {@link ColorChooserUI} object.
	 */
	public ColorChooserUI()
	{
		super();
	}

	/**
	 * Erzeugt eine UI Instanz.
	 * 
	 * @param c {@link JComponent}
	 * @return {@link ComponentUI}
	 */
	public static ComponentUI createUI(final JComponent c)
	{
		return new ColorChooserUI();
	}

	/**
	 * @see javax.swing.plaf.basic.BasicColorChooserUI#createDefaultChoosers()
	 */
	@Override
	protected AbstractColorChooserPanel[] createDefaultChoosers()
	{
		AbstractColorChooserPanel[] chooserPanels = super.createDefaultChoosers();

		// SwingX Panel dranhaengen
		AbstractColorChooserPanel[] panels =
				new AbstractColorChooserPanel[chooserPanels.length + 1];
		System.arraycopy(chooserPanels, 0, panels, 0, chooserPanels.length);

		panels[chooserPanels.length] = new EyeDropperColorChooserPanel();

		return panels;
	}
}
