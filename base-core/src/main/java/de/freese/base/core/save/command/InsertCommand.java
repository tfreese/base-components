package de.freese.base.core.save.command;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;

import de.freese.base.core.command.AbstractRemoteCommand;
import de.freese.base.core.save.SaveContext;
import de.freese.base.core.save.service.InsertService;

/**
 * Basisklasse des Command-Patterns für ein Insert-Kommando.<br>
 * Wird die {@link #execute()} Methode nicht überschrieben, wird versucht den CommandInvoker auf {@link InsertService} zu casten.
 *
 * @author Thomas Freese
 * @see InsertService
 */
public class InsertCommand extends AbstractRemoteCommand
{
    @Serial
    private static final long serialVersionUID = -8490815726760924228L;

    public InsertCommand(final Serializable source)
    {
        super(source);
    }

    /**
     * @see de.freese.base.core.command.Command#execute()
     */
    @Override
    public void execute() throws Exception
    {
        InsertService service = (InsertService) getCommandInvoker();
        SaveContext context = getPayload();

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
