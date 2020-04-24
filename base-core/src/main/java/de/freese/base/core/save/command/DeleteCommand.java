package de.freese.base.core.save.command;

import java.io.Serializable;
import java.util.Collection;

import de.freese.base.core.command.AbstractRemoteCommand;
import de.freese.base.core.save.SaveContext;
import de.freese.base.core.save.service.DeleteService;

/**
 * Basisklasse des Command-Patterns fuer ein Delete-Kommando.<br>
 * Wird die {@link #execute()} Methode nicht ueberschrieben, wird versucht den CommandInvoker auf
 * {@link DeleteService} zu casten.
 * 
 * @author Thomas Freese
 * @see DeleteService
 */
public class DeleteCommand extends AbstractRemoteCommand
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5013006885212315385L;

	/**
	 * Erstellt ein neues {@link DeleteCommand} Object.
	 * 
	 * @param source {@link Serializable}
	 */
	public DeleteCommand(final Serializable source)
	{
		super(source);
	}

	/**
	 * @see de.freese.base.core.command.Command#execute()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws Exception
	{
		DeleteService service = (DeleteService) getCommandInvoker();
		SaveContext context = (SaveContext) getPayload();

		if (getSource() instanceof Collection)
		{
			service.deleteAll((Collection<? extends Serializable>) getSource(), context);
		}
		else
		{
			service.delete(getSource(), context);
		}
	}
}
