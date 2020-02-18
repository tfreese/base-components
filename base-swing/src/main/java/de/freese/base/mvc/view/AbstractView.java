package de.freese.base.mvc.view;

import java.util.Objects;
import javax.swing.JComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.base.core.release.ReleaseVetoException;
import de.freese.base.mvc.context.ApplicationContext;
import de.freese.base.mvc.process.BusinessProcess;
import de.freese.base.resourcemap.IResourceMap;
import de.freese.base.swing.exception.SwingExceptionHandler;

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
    private final ApplicationContext context;

    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     *
     */
    private final BusinessProcess process;

    /**
     *
     */
    private JComponent viewComponent = null;

    /**
     * Erstellt ein neues {@link AbstractView} Object.
     *
     * @param process {@link BusinessProcess}
     * @param context {@link ApplicationContext}
     */
    public AbstractView(final BusinessProcess process, final ApplicationContext context)
    {
        super();

        this.process = Objects.requireNonNull(process, "process required");
        this.context = Objects.requireNonNull(context, "context required");
    }

    /**
     * @see de.freese.base.mvc.view.View#getComponent()
     */
    @Override
    public JComponent getComponent()
    {
        return this.viewComponent;
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
     * @see de.freese.base.mvc.view.View#getProcess()
     */
    @Override
    public BusinessProcess getProcess()
    {
        return this.process;
    }

    /**
     * Liefert die {@link IResourceMap} dieser View.
     *
     * @return {@link IResourceMap}
     */
    protected abstract IResourceMap getResourceMap();

    /**
     * @see de.freese.base.mvc.view.View#handleException(java.lang.Throwable)
     */
    @Override
    public void handleException(final Throwable throwable)
    {
        SwingExceptionHandler exceptionHandler = getContext().getExceptionHandler();

        exceptionHandler.handleException(throwable, getLogger(), getComponent(), getResourceMap());

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
     * @see de.freese.base.core.model.Initializeable#initialize()
     */
    @Override
    public void initialize()
    {
        getProcess().initialize();
    }

    /**
     * @see de.freese.base.core.release.ReleasePrepareable#prepareRelease()
     */
    @Override
    public void prepareRelease() throws ReleaseVetoException
    {
        // Empty
    }

    /**
     * @see de.freese.base.core.release.Releaseable#release()
     */
    @Override
    public void release()
    {
        getProcess().release();
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
     * @see de.freese.base.mvc.view.View#setComponent(javax.swing.JComponent)
     */
    @Override
    public void setComponent(final JComponent component)
    {
        this.viewComponent = Objects.requireNonNull(component, "component required");
    }
}
