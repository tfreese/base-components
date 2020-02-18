/**
 * Created: 29.07.2011
 */

package de.freese.base.mvc;

import java.awt.Component;

import de.freese.base.core.release.ReleaseVetoException;
import de.freese.base.mvc.view.View;
import de.freese.base.resourcemap.IResourceMap;
import de.freese.base.resourcemap.ResourceMap;

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
	private IResourceMap resourceMap = null;

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
	public IResourceMap getResourceMap()
	{
		if (this.resourceMap == null)
		{
			this.resourceMap = new ResourceMap();
			this.resourceMap.setBaseName(getBundleName());

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
	 * @see de.freese.base.core.release.ReleasePrepareable#prepareRelease()
	 */
	@Override
	public void prepareRelease() throws ReleaseVetoException
	{
		this.view.saveState();
		this.view.getProcess().prepareRelease();
	}

	/**
	 * @see de.freese.base.core.release.Releaseable#release()
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
