package de.freese.base.core.save.command;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;

import de.freese.base.core.command.AbstractRemoteCommand;
import de.freese.base.core.save.SaveContext;
import de.freese.base.core.save.service.UpdateService;

/**
 * Basisklasse des Command-Patterns für ein Update-Kommando.<br>
 * Wird die {@link #execute()} Methode nicht überschrieben, wird versucht den CommandInvoker auf {@link UpdateService} zu casten.
 *
 * @author Thomas Freese
 * @see UpdateService
 */
public class UpdateCommand extends AbstractRemoteCommand
{
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 2163481914421663100L;

    /**
     * Erstellt ein neues {@link UpdateCommand} Object.
     *
     * @param source {@link Serializable}
     */
    public UpdateCommand(final Serializable source)
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
        UpdateService service = (UpdateService) getCommandInvoker();
        SaveContext context = getPayload();

        if (getSource() instanceof Collection)
        {
            service.updateAll((Collection<? extends Serializable>) getSource(), context);
        }
        else
        {
            service.update(getSource(), context);
        }
    }
}
