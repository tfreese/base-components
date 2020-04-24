package de.freese.base.core.save.command;

import java.io.Serializable;
import java.util.Collection;

import de.freese.base.core.command.AbstractRemoteCommand;
import de.freese.base.core.save.SaveContext;
import de.freese.base.core.save.service.InsertService;

/**
 * Basisklasse des Command-Patterns fuer ein Insert-Kommando.<br>
 * Wird die {@link #execute()} Methode nicht ueberschrieben, wird versucht den CommandInvoker auf
 * {@link InsertService} zu casten.
 * 
 * @author Thomas Freese
 * @see InsertService
 */
public class InsertCommand extends AbstractRemoteCommand
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8490815726760924228L;

	/**
	 * Erstellt ein neues {@link InsertCommand} Object.
	 * 
	 * @param source {@link Serializable}
	 */
	public InsertCommand(final Serializable source)
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
		InsertService service = (InsertService) getCommandInvoker();
		SaveContext context = (SaveContext) getPayload();

		if (getSource() instanceof Collection)
		{
			service.insertAll((Collection<? extends Serializable>) getSource(), context);
		}
		else
		{
			service.insert(getSource(), context);
		}
	}
}
