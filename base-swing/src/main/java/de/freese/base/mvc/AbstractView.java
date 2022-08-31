package de.freese.base.mvc;

import java.awt.Component;
import java.util.Objects;

import de.freese.base.resourcemap.ResourceMap;
import de.freese.base.swing.exception.SwingExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BasisImplementierung einer IView.
 *
 * @author Thomas Freese
 */
public abstract class AbstractView implements View
{
    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());
    /**
     *
     */
    private Component component;
    /**
     *
     */
    private ApplicationContext context;
    /**
     *
     */
    private ResourceMap resourceMap;

    /**
     * @see de.freese.base.mvc.View#getComponent()
     */
    @Override
    public Component getComponent()
    {
        return this.component;
    }

    /**
     * @see de.freese.base.mvc.View#handleException(java.lang.Throwable)
     */
    @Override
    public void handleException(final Throwable throwable)
    {
        SwingExceptionHandler exceptionHandler = getContext().getExceptionHandler();

        exceptionHandler.handleException(throwable, getLogger(), getComponent(), (key, args) -> getResourceMap().getString(key, args));

        // getLogger().error(throwable.getMessage(), throwable);
        //
        // // Dialoge sollten nicht die Tasks blockieren bei Fehlermeldungen
        // SwingUtilities.invokeLater(new Runnable()
        // {
        // /**
        // * @see java.lang.Runnable#run()
        // */
        // @Override
        // public void run()
        // {
        // JOptionPane.showMessageDialog(getViewComponent(), throwable, throwable.getClass()
        // .getSimpleName(), JOptionPane.ERROR_MESSAGE);
        // }
        // });
    }

    /**
     * @see de.freese.base.mvc.View#restoreState()
     */
    @Override
    public void restoreState()
    {
        // Empty
    }

    /**
     * @see de.freese.base.mvc.View#saveState()
     */
    @Override
    public void saveState()
    {
        // Empty
    }

    /**
     * Setzt den {@link ApplicationContext} dieser View
     *
     * @param context {@link ApplicationContext}
     */
    void setContext(final ApplicationContext context)
    {
        this.context = context;
    }

    /**
     * Setzt die {@link ResourceMap} dieser View.
     *
     * @param resourceMap {@link ResourceMap}
     */
    void setResourceMap(final ResourceMap resourceMap)
    {
        this.resourceMap = resourceMap;
    }

    /**
     * Liefert den {@link ApplicationContext} dieser View.
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
     * Liefert die {@link ResourceMap} dieser View.
     *
     * @return {@link ResourceMap}
     */
    protected ResourceMap getResourceMap()
    {
        return this.resourceMap;
    }

    /**
     * @param component {@link Component}
     */
    protected void setComponent(final Component component)
    {
        this.component = Objects.requireNonNull(component, "component required");
    }
}
