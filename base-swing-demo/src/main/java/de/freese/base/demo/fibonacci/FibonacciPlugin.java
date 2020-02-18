/**
 * Created: 17.07.2011
 */

package de.freese.base.demo.fibonacci;

import de.freese.base.demo.fibonacci.bp.FibonacciBP;
import de.freese.base.demo.fibonacci.view.FibonacciView;
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
	 * @see de.freese.base.core.model.NameProvider#getName()
	 */
	@Override
	public String getName()
	{
		return "fibonacci";
	}

	/**
	 * @see de.freese.base.core.model.Initializeable#initialize()
	 */
	@Override
	public void initialize()
	{
		FibonacciBP process = new FibonacciBP();
		FibonacciView view = new FibonacciView(process, getApplication().getContext());
		setView(view);
	}
}
