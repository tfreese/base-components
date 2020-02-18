package de.freese.base.swing.components;

import java.awt.Frame;
import java.awt.GraphicsConfiguration;

import javax.swing.JFrame;

/**
 * {@link JFrame} mit der Moeglichkeit per Methode den State zu aendern.<br>
 * deiconify, iconify, maximize,minimize.<br>
 * 
 * @author Thomas Freese
 */
public class ExtFrame extends JFrame
{
	/**
     * 
     */
	private static final long serialVersionUID = 4014880096241781642L;

	/**
	 * Erstellt ein neues {@link ExtFrame} Object.
	 */
	public ExtFrame()
	{
		super();
	}

	/**
	 * Erstellt ein neues {@link ExtFrame} Object.
	 * 
	 * @param gc {@link GraphicsConfiguration}
	 */
	public ExtFrame(final GraphicsConfiguration gc)
	{
		super(gc);
	}

	/**
	 * Erstellt ein neues {@link ExtFrame} Object.
	 * 
	 * @param title String
	 */
	public ExtFrame(final String title)
	{
		super(title);
	}

	/**
	 * Erstellt ein neues {@link ExtFrame} Object.
	 * 
	 * @param title String
	 * @param gc {@link GraphicsConfiguration}
	 */
	public ExtFrame(final String title, final GraphicsConfiguration gc)
	{
		super(title, gc);
	}

	/**
	 * This method deiconifies a frame; the maximized bits are not affected.
	 */
	public void deiconify()
	{
		int state = this.getExtendedState();

		// Clear the iconified bit
		state &= ~Frame.ICONIFIED;

		// Deiconify the frame
		setExtendedState(state);
	}

	/**
	 * This method iconifies a frame; the maximized bits are not affected.
	 */
	public void iconify()
	{
		int state = getExtendedState();

		// Set the iconified bit
		state |= Frame.ICONIFIED;

		// Iconify the frame
		setExtendedState(state);
	}

	/**
	 * This method minimizes a frame; the iconified bit is not affected.
	 */
	public void maximize()
	{
		int state = getExtendedState();

		// Set the maximized bits
		state |= Frame.MAXIMIZED_BOTH;

		// Maximize the frame
		setExtendedState(state);
	}

	/**
	 * This method minimizes a frame; the iconified bit is not affected.
	 */
	public void minimize()
	{
		int state = getExtendedState();

		// Clear the maximized bits
		state &= ~Frame.MAXIMIZED_BOTH;

		// Maximize the frame
		setExtendedState(state);
	}
}
