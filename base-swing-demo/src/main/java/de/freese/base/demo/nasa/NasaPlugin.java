/**
 * Created: 17.07.2011
 */

package de.freese.base.demo.nasa;

import de.freese.base.demo.nasa.bp.NasaBP;
import de.freese.base.demo.nasa.view.NasaView;
import de.freese.base.mvc.AbstractMVCPlugin;

/**
 * Nasa Demo Plugin.
 * 
 * @author Thomas Freese
 */
public class NasaPlugin extends AbstractMVCPlugin
{
	/**
	 * Erstellt ein neues {@link NasaPlugin} Object.
	 */
	public NasaPlugin()
	{
		super();
	}

	/**
	 * @see de.freese.base.mvc.AbstractMVCPlugin#getBundleName()
	 */
	@Override
	protected String getBundleName()
	{
		return "bundles/nasa";
	}

	/**
	 * @see de.freese.base.core.model.NameProvider#getName()
	 */
	@Override
	public String getName()
	{
		return "nasa";
	}

	/**
	 * @see de.freese.base.core.model.Initializeable#initialize()
	 */
	@Override
	public void initialize()
	{
		NasaBP process = new NasaBP();
		NasaView view = new NasaView(process, getApplication().getContext());
		setView(view);
	}
}
