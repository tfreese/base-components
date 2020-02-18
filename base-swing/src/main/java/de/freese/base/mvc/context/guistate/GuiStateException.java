package de.freese.base.mvc.context.guistate;

import de.freese.base.swing.state.GUIState;

/**
 * Exception fuer die {@link GUIState}s.
 * 
 * @author Thomas Freese
 */
public class GuiStateException extends Exception
{
	/**
	 *
	 */
	private static final long serialVersionUID = -7706760520460154749L;

	/**
	 * Erstellt ein neues {@link GuiStateException} Object.
	 */
	public GuiStateException()
	{
		super();
	}

	/**
	 * Erstellt ein neues {@link GuiStateException} Object.
	 * 
	 * @param message String
	 */
	protected GuiStateException(final String message)
	{
		super(message);
	}

	/**
	 * Erstellt ein neues {@link GuiStateException} Object.
	 * 
	 * @param message String
	 * @param cause {@link Throwable}
	 */
	public GuiStateException(final String message, final Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * Erstellt ein neues {@link GuiStateException} Object.
	 * 
	 * @param cause {@link Throwable}
	 */
	protected GuiStateException(final Throwable cause)
	{
		super(cause);
	}
}
