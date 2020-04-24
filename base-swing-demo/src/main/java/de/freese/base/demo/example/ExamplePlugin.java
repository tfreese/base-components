/**
 * Created: 17.07.2011
 */

package de.freese.base.demo.example;

import de.freese.base.demo.example.view.ExampleView;
import de.freese.base.mvc.AbstractMVCPlugin;
import de.freese.base.mvc.process.EmptyBusinessProcess;

/**
 * Example Demo Plugin.
 *
 * @author Thomas Freese
 */
public class ExamplePlugin extends AbstractMVCPlugin
{
    /**
     * Erstellt ein neues {@link ExamplePlugin} Object.
     */
    public ExamplePlugin()
    {
        super();
    }

    /**
     * @see de.freese.base.mvc.AbstractMVCPlugin#getBundleName()
     */
    @Override
    protected String getBundleName()
    {
        return "bundles/example";
    }

    /**
     * @see de.freese.base.mvc.MVCPlugin#getName()
     */
    @Override
    public String getName()
    {
        return "example";
    }

    /**
     * @see de.freese.base.mvc.MVCPlugin#initialize()
     */
    @Override
    public void initialize()
    {
        EmptyBusinessProcess process = new EmptyBusinessProcess();
        ExampleView view = new ExampleView(process, getApplication().getContext());
        setView(view);
    }
}
