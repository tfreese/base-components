package de.freese.base.mvc;

import de.freese.base.resourcemap.ResourceMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BasisImplementierung eines {@link Controller}.
 *
 * @author Thomas Freese
 */
public abstract class AbstractController implements Controller
{
    /**
     *
     */
    private ApplicationContext context;
    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());
    /**
     *
     */
    private String name;
    /**
     *
     */
    private ResourceMap resourceMap;

    /**
     * @see de.freese.base.mvc.Controller#getName()
     */
    @Override
    public String getName()
    {
        return this.name;
    }

    /**
     * @see de.freese.base.mvc.Controller#getResourceMap()
     */
    @Override
    public ResourceMap getResourceMap()
    {
        return this.resourceMap;
    }

    /**
     * @see de.freese.base.mvc.Controller#handleException(java.lang.Throwable)
     */
    @Override
    public void handleException(final Throwable throwable)
    {
        getView().handleException(throwable);
    }

    /**
     * @see de.freese.base.mvc.Controller#initialize()
     */
    @Override
    public void initialize()
    {
        getView().createGUI();
        getView().restoreState();
    }

    /**
     * @see de.freese.base.mvc.Controller#release()
     */
    @Override
    public void release()
    {
        getView().saveState();
    }

    /**
     * Liefert den {@link ApplicationContext} dieses Controllers.
     *
     * @return {@link ApplicationContext}
     */
    protected ApplicationContext getContext()
    {
        return this.context;
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }

    /**
     * Setzt den {@link ApplicationContext} dieses Controllers.
     *
     * @param context {@link ApplicationContext}
     */
    void setContext(final ApplicationContext context)
    {
        this.context = context;

        if (getView() instanceof AbstractView v)
        {
            v.setContext(context);
        }
    }

    /**
     * Setzt den Namen dieses Controllers.
     *
     * @param name String
     */
    void setName(final String name)
    {
        this.name = name;
    }

    /**
     * Setzt die {@link ResourceMap} dieses Controllers.
     *
     * @param resourceMap {@link ResourceMap}
     */
    void setResourceMap(final ResourceMap resourceMap)
    {
        this.resourceMap = resourceMap;

        if (getView() instanceof AbstractView v)
        {
            v.setResourceMap(resourceMap);
        }
    }
}
