package de.freese.base.swing.components.dialog;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JOptionPane;

import de.freese.base.resourcemap.ResourceMap;

/**
 * Factory fuer {@link ExtDialog}e.
 * 
 * @author Thomas Freese
 */
public final class DialogFactory
{
	/**
	 * Erzeugt einen Dialog.
	 * 
	 * @param parent {@link Component}
	 * @param title String
	 * @param message Object
	 * @param messagetType int
	 * @param optionType int
	 * @param icon {@link Icon}
	 * @param show boolean
	 * @return {@link ExtDialog}
	 */
	public static ExtDialog create(final Component parent, final String title,
									final Object message, final int messagetType,
									final int optionType, final Icon icon, final boolean show)
	{
		ExtDialogConfig config = new ExtDialogConfig();
		config.setOwner(parent);
		config.setTitle(title);
		config.setMessage(message);
		config.setMessageType(messagetType);
		config.setOptionType(optionType);
		config.setIcon(icon);

		ExtDialog dialog = new ExtDialog();
		dialog.configure(config);
		dialog.setVisible(show);

		return dialog;
	}

	/**
	 * Erzeugt einen Error-Dialog mit OK Option.
	 * 
	 * @param parent {@link Component}
	 * @param resourceMap {@link ResourceMap}
	 * @param message Object
	 * @param show boolean
	 * @return {@link ExtDialog}
	 */
	public static ExtDialog createFehler(final Component parent, final ResourceMap resourceMap,
											final Object message, final boolean show)
	{
		return create(parent, resourceMap.getString("titel.fehler"), message,
				JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null, show);
	}

	/**
	 * Erzeugt einen Information-Dialog mit OK Option als Dummy.
	 * 
	 * @param parent {@link Component}
	 * @param resourceMap {@link ResourceMap}
	 * @param show boolean
	 * @return {@link ExtDialog}
	 */
	public static ExtDialog createInArbeit(final Component parent, final ResourceMap resourceMap,
											final boolean show)
	{
		return create(parent, resourceMap.getString("titel.info"),
				resourceMap.getString("titel.in_arbeit"), JOptionPane.INFORMATION_MESSAGE,
				JOptionPane.DEFAULT_OPTION, resourceMap.getIcon("icons/schleimmann.gif"), show);
	}

	/**
	 * Erzeugt einen Information-Dialog mit OK Option.
	 * 
	 * @param parent {@link Component}
	 * @param resourceMap {@link ResourceMap}
	 * @param message Object
	 * @param show boolean
	 * @return {@link ExtDialog}
	 */
	public static ExtDialog createInfo(final Component parent, final ResourceMap resourceMap,
										final Object message, final boolean show)
	{
		return create(parent, resourceMap.getString("titel.info"), message,
				JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, show);
	}

	/**
	 * Erzeugt einen Question-Dialog mit YES-NO Option.
	 * 
	 * @param parent {@link Component}
	 * @param resourceMap {@link ResourceMap}
	 * @param message Object
	 * @param show boolean
	 * @return {@link ExtDialog}
	 */
	public static ExtDialog createJaNein(final Component parent, final ResourceMap resourceMap,
											final Object message, final boolean show)
	{
		return createJaNein(parent, resourceMap.getString("titel.ja_nein"), message,
				JOptionPane.QUESTION_MESSAGE, show);
	}

	/**
	 * Erzeugt einen Question-Dialog mit YES-NO Option.
	 * 
	 * @param parent {@link Component}
	 * @param title String
	 * @param message Object
	 * @param show boolean
	 * @return {@link ExtDialog}
	 */
	public static ExtDialog createJaNein(final Component parent, final String title,
											final Object message, final boolean show)
	{
		return createJaNein(parent, title, message, JOptionPane.QUESTION_MESSAGE, show);
	}

	/**
	 * Erzeugt einen Dialog mit YES-NO Option.
	 * 
	 * @param parent {@link Component}
	 * @param title String
	 * @param message Object
	 * @param messageType int
	 * @param show boolean
	 * @return {@link ExtDialog}
	 */
	public static ExtDialog createJaNein(final Component parent, final String title,
											final Object message, final int messageType,
											final boolean show)
	{
		return create(parent, title, message, messageType, JOptionPane.YES_NO_OPTION, null, show);
	}

	/**
	 * Erzeugt einen Plain-Dialog mit OK-CANCEL Option.
	 * 
	 * @param parent {@link Component}
	 * @param resourceMap {@link ResourceMap}
	 * @param message Object
	 * @param show boolean
	 * @return {@link ExtDialog}
	 */
	public static ExtDialog createOkAbbrechen(final Component parent,
												final ResourceMap resourceMap,
												final Object message, final boolean show)
	{
		return createOkAbbrechen(parent, resourceMap.getString("titel.ok_abbrechen"), message, show);
	}

	/**
	 * Erzeugt einen Dialog mit OK-CANCEL Option.
	 * 
	 * @param parent {@link Component}
	 * @param title String
	 * @param message Object
	 * @param show boolean
	 * @return {@link ExtDialog}
	 */
	public static ExtDialog createOkAbbrechen(final Component parent, final String title,
												final Object message, final boolean show)
	{
		return createOkAbbrechen(parent, title, message, JOptionPane.PLAIN_MESSAGE, show);
	}

	/**
	 * Erzeugt einen Dialog mit OK-CANCEL Option.
	 * 
	 * @param parent {@link Component}
	 * @param title String
	 * @param message Object
	 * @param messageType int
	 * @param show boolean
	 * @return {@link ExtDialog}
	 */
	public static ExtDialog createOkAbbrechen(final Component parent, final String title,
												final Object message, final int messageType,
												final boolean show)
	{
		return create(parent, title, message, messageType, JOptionPane.OK_CANCEL_OPTION, null, show);
	}

	/**
	 * Erzeugt einen Warning-Dialog mit OK Option.
	 * 
	 * @param parent {@link Component}
	 * @param resourceMap {@link ResourceMap}
	 * @param message Object
	 * @param show boolean
	 * @return {@link ExtDialog}
	 */
	public static ExtDialog createWarnung(final Component parent, final ResourceMap resourceMap,
											final Object message, final boolean show)
	{
		return create(parent, resourceMap.getString("titel.warnung"), message,
				JOptionPane.WARNING_MESSAGE, JOptionPane.DEFAULT_OPTION, null, show);
	}
}
