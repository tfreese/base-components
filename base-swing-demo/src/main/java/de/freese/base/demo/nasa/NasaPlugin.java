/**
 * Created: 17.07.2011
 */

package de.freese.base.demo.nasa;

import de.freese.base.demo.nasa.bp.DefaultNasaBP;
import de.freese.base.demo.nasa.view.DefaultNasaView;
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
     * @see de.freese.base.mvc.MVCPlugin#getName()
     */
    @Override
    public String getName()
    {
        return "nasa";
    }

    /**
     * @see de.freese.base.mvc.MVCPlugin#initialize()
     */
    @Override
    public void initialize()
    {
        DefaultNasaBP process = new DefaultNasaBP();
        DefaultNasaView view = new DefaultNasaView(process, getApplication().getContext());
        setView(view);
    }
}
