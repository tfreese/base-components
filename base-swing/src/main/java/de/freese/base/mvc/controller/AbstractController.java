package de.freese.base.mvc.controller;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.base.mvc.ApplicationContext;
import de.freese.base.resourcemap.ResourceMap;

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
    private final ApplicationContext context;

    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
    *
    */
    private final ResourceMap resourceMap;

    /**
     * Erstellt ein neues {@link AbstractController} Object.
     *
     * @param context {@link ApplicationContext}
     */
    public AbstractController(final ApplicationContext context)
    {
        super();

        this.context = Objects.requireNonNull(context, "context required");

        this.resourceMap = ResourceMap.create(getBundleName());

        this.resourceMap.setParent(context.getResourceMapRoot());
        this.context.addResourceMap(getName(), this.resourceMap);
    }

    /**
     * Liefert den Namen des ResourceBundles.
     *
     * @return String
     */
    protected abstract String getBundleName();

    /**
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
     * @see de.freese.base.mvc.controller.Controller#getResourceMap()
     */
    @Override
    public ResourceMap getResourceMap()
    {
        return this.resourceMap;
    }

    /**
     * @see de.freese.base.mvc.controller.Controller#handleException(java.lang.Throwable)
     */
    @Override
    public void handleException(final Throwable throwable)
    {
        getView().handleException(throwable);
    }

    /**
     * @see de.freese.base.mvc.controller.Controller#initialize()
     */
    @Override
    public void initialize()
    {
        getView().createGUI();
        getView().restoreState();
    }

    /**
     * @see de.freese.base.mvc.controller.Controller#release()
     */
    @Override
    public void release()
    {
        getView().saveState();
    }
}
