package de.freese.base.core.save.command;

import java.io.Serializable;
import java.util.Collection;

import de.freese.base.core.command.AbstractRemoteCommand;
import de.freese.base.core.save.SaveContext;
import de.freese.base.core.save.service.IInsertService;

/**
 * Basisklasse des Command-Patterns fuer ein Insert-Kommando.<br>
 * Wird die {@link #execute()} Methode nicht ueberschrieben, wird versucht den CommandInvoker auf
 * {@link IInsertService} zu casten.
 * 
 * @author Thomas Freese
 * @see IInsertService
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
		IInsertService service = (IInsertService) getCommandInvoker();
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
