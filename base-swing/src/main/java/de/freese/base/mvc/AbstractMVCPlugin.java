/**
 * Created: 29.07.2011
 */

package de.freese.base.mvc;

import java.awt.Component;
import de.freese.base.mvc.view.View;
import de.freese.base.resourcemap.ResourceMap;
import de.freese.base.swing.exception.ReleaseVetoException;

/**
 * Basisklase eines MVC-Plugins.
 *
 * @author Thomas Freese
 */
public abstract class AbstractMVCPlugin implements MVCPlugin
{
    /**
     *
     */
    private AbstractApplication application = null;

    /**
     *
     */
    private ResourceMap resourceMap = null;

    /**
     *
     */
    private View view = null;

    /**
     * Erstellt ein neues {@link AbstractMVCPlugin} Object.
     */
    public AbstractMVCPlugin()
    {
        super();
    }

    /**
     * @return {@link AbstractApplication}
     */
    protected AbstractApplication getApplication()
    {
        return this.application;
    }

    /**
     * Liefert den Namen des ResourceBundles.
     *
     * @return String
     */
    protected abstract String getBundleName();

    /**
     * @see de.freese.base.swing.ComponentProvider#getComponent()
     */
    @Override
    public Component getComponent()
    {
        return getView().getComponent();
    }

    /**
     * @see de.freese.base.mvc.MVCPlugin#getResourceMap()
     */
    @Override
    public ResourceMap getResourceMap()
    {
        if (this.resourceMap == null)
        {
            this.resourceMap = ResourceMap.create(getBundleName());

            this.resourceMap.setParent(getApplication().getResourceMapRoot());
            getApplication().getContext().addResourceMap(getName(), this.resourceMap);
        }

        return this.resourceMap;
    }

    /**
     * @return {@link View}
     */
    protected View getView()
    {
        return this.view;
    }

    /**
     * @see de.freese.base.mvc.MVCPlugin#prepareRelease()
     */
    @Override
    public void prepareRelease() throws ReleaseVetoException
    {
        this.view.saveState();
        this.view.getProcess().prepareRelease();
    }

    /**
     * @see de.freese.base.mvc.MVCPlugin#release()
     */
    @Override
    public void release()
    {
        this.view.getProcess().release();
    }

    /**
     * @see de.freese.base.mvc.MVCPlugin#setApplication(de.freese.base.mvc.AbstractApplication)
     */
    @Override
    public void setApplication(final AbstractApplication application)
    {
        this.application = application;
    }

    /**
     * @param view {@link View}
     */
    protected void setView(final View view)
    {
        this.view = view;

        getResourceMap();
        this.view.initialize();
        this.view.restoreState();
    }
}
