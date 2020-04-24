/**
 * Created: 17.07.2011
 */

package de.freese.base.demo.fibonacci;

import de.freese.base.demo.fibonacci.bp.DefaultFibonacciBP;
import de.freese.base.demo.fibonacci.view.DefaultFibonacciView;
import de.freese.base.mvc.AbstractMVCPlugin;

/**
 * Fibonacci Demo Plugin.
 *
 * @author Thomas Freese
 */
public class FibonacciPlugin extends AbstractMVCPlugin
{
    /**
     * Erstellt ein neues {@link FibonacciPlugin} Object.
     */
    public FibonacciPlugin()
    {
        super();
    }

    /**
     * @see de.freese.base.mvc.AbstractMVCPlugin#getBundleName()
     */
    @Override
    protected String getBundleName()
    {
        return "bundles/fibonacci";
    }

    /**
     * @see de.freese.base.mvc.MVCPlugin#getName()
     */
    @Override
    public String getName()
    {
        return "fibonacci";
    }

    /**
     * @see de.freese.base.mvc.MVCPlugin#initialize()
     */
    @Override
    public void initialize()
    {
        DefaultFibonacciBP process = new DefaultFibonacciBP();
        DefaultFibonacciView view = new DefaultFibonacciView(process, getApplication().getContext());
        setView(view);
    }
}
