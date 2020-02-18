package de.freese.base.swing.components.dialog;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JOptionPane;

/**
 * Konfigurationsobjekt fuer den {@link ExtDialog}.
 * 
 * @author Thomas Freese
 */
public class ExtDialogConfig
{
	/**
	 * 
	 */
	private Map<Integer, ActionListener> buttonActionListeners = new HashMap<>();

	/**
	 * 
	 */
	private Icon icon = null;

	/**
	 * {@link Component} oder String
	 */
	private Object message = "";

	/**
	 * @see JOptionPane#PLAIN_MESSAGE
	 * @see JOptionPane#INFORMATION_MESSAGE
	 * @see JOptionPane#QUESTION_MESSAGE
	 * @see JOptionPane#WARNING_MESSAGE
	 * @see JOptionPane#ERROR_MESSAGE
	 */
	private int messageType = JOptionPane.PLAIN_MESSAGE;

	/**
	 * 
	 */
	private boolean modal = true;

	/**
	 * Eigene Texte.
	 */
	private String[] options = null;

	/**
	 * @see JOptionPane#DEFAULT_OPTION
	 * @see JOptionPane#YES_NO_OPTION
	 * @see JOptionPane#YES_NO_CANCEL_OPTION
	 * @see JOptionPane#OK_CANCEL_OPTION
	 */
	private int optionType = JOptionPane.OK_OPTION;

	/**
	 * 
	 */
	private Component owner = null;

	/**
	 * 
	 */
	private boolean resizeable = true;

	/**
	 * 
	 */
	private String title = "";

	/**
	 * 
	 */
	private WindowListener windowListener = null;

	/**
	 * Erstellt ein neues {@link ExtDialogConfig} Object.
	 */
	public ExtDialogConfig()
	{
		super();
	}

	/**
	 * Liefert den {@link ActionListener} fuer eine Buttonoption oder null.
	 * 
	 * @param buttonIndex int
	 * @return {@link ActionListener}
	 */
	public ActionListener getButtonActionListener(final int buttonIndex)
	{
		return this.buttonActionListeners.get(buttonIndex);
	}

	/**
	 * @return {@link Icon}
	 */
	public Icon getIcon()
	{
		return this.icon;
	}

	/**
	 * Liefert die Message/Component.
	 * 
	 * @return {@link Component} oder String
	 */
	public Object getMessage()
	{
		return this.message;
	}

	/**
	 * Liefert den Message Typ.
	 * 
	 * @return int
	 * @see JOptionPane#PLAIN_MESSAGE
	 * @see JOptionPane#INFORMATION_MESSAGE
	 * @see JOptionPane#QUESTION_MESSAGE
	 * @see JOptionPane#WARNING_MESSAGE
	 * @see JOptionPane#ERROR_MESSAGE
	 */
	public int getMessageType()
	{
		return this.messageType;
	}

	/**
	 * Eigene Texte fuer die Buttons.
	 * 
	 * @return String[]
	 */
	public String[] getOptions()
	{
		return this.options;
	}

	/**
	 * Liefert die Dialogoptionen.
	 * 
	 * @return int
	 * @see JOptionPane#OK_OPTION
	 * @see JOptionPane#OK_CANCEL_OPTION
	 * @see JOptionPane#YES_OPTION
	 * @see JOptionPane#YES_NO_OPTION
	 * @see JOptionPane#YES_NO_CANCEL_OPTION
	 */
	public int getOptionType()
	{
		return this.optionType;
	}

	/**
	 * @return {@link Component}
	 */
	public Component getOwner()
	{
		return this.owner;
	}

	/**
	 * Liefert den Titel.
	 * 
	 * @return String
	 */
	public String getTitle()
	{
		return this.title;
	}

	/**
	 * Liefert den {@link WindowListener}.
	 * 
	 * @return {@link WindowListener}
	 */
	public WindowListener getWindowListener()
	{
		return this.windowListener;
	}

	/**
	 * @return boolean
	 */
	public boolean isModal()
	{
		return this.modal;
	}

	/**
	 * @return boolean
	 */
	public boolean isResizeable()
	{
		return this.resizeable;
	}

	/**
	 * Setzt den {@link ActionListener} fuer einen Button.
	 * 
	 * @param buttonIndex int
	 * @param actionListener {@link ActionListener}
	 */
	public void setButtonActionListener(final int buttonIndex, final ActionListener actionListener)
	{
		this.buttonActionListeners.put(buttonIndex, actionListener);
	}

	/**
	 * @param icon {@link Icon}
	 */
	public void setIcon(final Icon icon)
	{
		this.icon = icon;
	}

	/**
	 * Setzt die Message/Component.
	 * 
	 * @param message {@link Component} oder String
	 */
	public void setMessage(final Object message)
	{
		this.message = message;
	}

	/**
	 * Setzt den Message Typ.
	 * 
	 * @param messageType int
	 * @see JOptionPane#PLAIN_MESSAGE
	 * @see JOptionPane#INFORMATION_MESSAGE
	 * @see JOptionPane#QUESTION_MESSAGE
	 * @see JOptionPane#WARNING_MESSAGE
	 * @see JOptionPane#ERROR_MESSAGE
	 */
	public void setMessageType(final int messageType)
	{
		this.messageType = messageType;
	}

	/**
	 * @param modal boolean
	 */
	public void setModal(final boolean modal)
	{
		this.modal = modal;
	}

	/**
	 * Eigene Texte fuer die Buttons.
	 * 
	 * @param options String[]
	 */
	public void setOptions(final String...options)
	{
		this.options = options;
	}

	/**
	 * Setzt die Dialogoptionen.
	 * 
	 * @param optionType int
	 * @see JOptionPane#OK_OPTION
	 * @see JOptionPane#OK_CANCEL_OPTION
	 * @see JOptionPane#YES_OPTION
	 * @see JOptionPane#YES_NO_OPTION
	 * @see JOptionPane#YES_NO_CANCEL_OPTION
	 */
	public void setOptionType(final int optionType)
	{
		this.optionType = optionType;
	}

	/**
	 * @param owner {@link Component}
	 */
	public void setOwner(final Component owner)
	{
		this.owner = owner;
	}

	/**
	 * @param resizeable boolean
	 */
	public void setResizeable(final boolean resizeable)
	{
		this.resizeable = resizeable;
	}

	/**
	 * Setzt den Titel.
	 * 
	 * @param title String
	 */
	public void setTitle(final String title)
	{
		this.title = title;
	}

	/**
	 * Setzt den {@link WindowListener}.
	 * 
	 * @param windowListener {@link WindowListener}
	 */
	public void setWindowListener(final WindowListener windowListener)
	{
		this.windowListener = windowListener;
	}
}
