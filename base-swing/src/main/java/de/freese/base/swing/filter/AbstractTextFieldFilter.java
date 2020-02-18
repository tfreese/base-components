package de.freese.base.swing.filter;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

/**
 * Filter der ein {@link DocumentListener} ist.
 * 
 * @author Thomas Freese
 */
public abstract class AbstractTextFieldFilter extends AbstractFilter implements DocumentListener
{
	/**
	 * Creates a new {@link AbstractTextFieldFilter} object.
	 */
	public AbstractTextFieldFilter()
	{
		super();
	}

	/**
	 * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
	 */
	@Override
	public void changedUpdate(final DocumentEvent e)
	{
		handleDocumentEvent(e);
	}

	/**
	 * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
	 */
	@Override
	public void insertUpdate(final DocumentEvent e)
	{
		handleDocumentEvent(e);
	}

	/**
	 * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
	 */
	@Override
	public void removeUpdate(final DocumentEvent e)
	{
		handleDocumentEvent(e);
	}

	/**
	 * Ermittelt den aktuellen Text aus dem {@link Document}.
	 * 
	 * @param event {@link DocumentEvent}
	 */
	private void handleDocumentEvent(final DocumentEvent event)
	{
		Document document = event.getDocument();
		String newValue = null;

		try
		{
			newValue = document.getText(0, document.getLength());
			setFilterValue(newValue);
		}
		catch (Exception ex)
		{
			getLogger().error(null, ex);
		}
	}
}
