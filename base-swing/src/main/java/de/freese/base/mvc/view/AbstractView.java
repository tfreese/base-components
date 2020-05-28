package de.freese.base.mvc.view;

import java.awt.Component;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.base.mvc.ApplicationContext;
import de.freese.base.resourcemap.ResourceMap;
import de.freese.base.swing.exception.SwingExceptionHandler;

/**
 * BasisImplementierung einer IView.
 *
 * @author Thomas Freese
 * @param <C> Typ der Komponente
 */
public abstract class AbstractView<C extends Component> implements View<C>
{
    /**
     *
     */
    private C component = null;

    /**
     *
     */
    private final ApplicationContext context;

    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Erstellt ein neues {@link AbstractView} Object.
     *
     * @param context {@link ApplicationContext}
     */
    public AbstractView(final ApplicationContext context)
    {
        super();

        this.context = Objects.requireNonNull(context, "context required");
    }

    /**
     * @see de.freese.base.mvc.view.View#getComponent()
     */
    @Override
    public C getComponent()
    {
        return this.component;
    }

    /**
     * Liefert den {@link ApplicationContext}.
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
    protected abstract ResourceMap getResourceMap();

    /**
     * @see de.freese.base.mvc.view.View#handleException(java.lang.Throwable)
     */
    @Override
    public void handleException(final Throwable throwable)
    {
        SwingExceptionHandler exceptionHandler = getContext().getExceptionHandler();

        exceptionHandler.handleException(throwable, getLogger(), getComponent(), (key, args) -> getResourceMap().getString(key, args));

        // getLogger().error(null, throwable);
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
     * @see de.freese.base.mvc.view.View#restoreState()
     */
    @Override
    public void restoreState()
    {
        // Empty
    }

    /**
     * @see de.freese.base.mvc.view.View#saveState()
     */
    @Override
    public void saveState()
    {
        // Empty
    }

    /**
     * @param component {@link Component}
     */
    protected void setComponent(final C component)
    {
        this.component = Objects.requireNonNull(component, "component required");
    }
}
