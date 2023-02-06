// Created: 05.02.23
package de.freese.base.mvc2.view;

import java.awt.Component;
import java.util.Objects;

import de.freese.base.mvc2.ApplicationContext;
import de.freese.base.resourcemap.ResourceMap;
import de.freese.base.swing.exception.SwingExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public abstract class AbstractView implements View
{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private ApplicationContext applicationContext;

    private Component component;

    @Override
    public Component getComponent()
    {
        return component;
    }

    @Override
    public void handleException(final Throwable throwable)
    {
        SwingExceptionHandler exceptionHandler = getApplicationContext().getService(SwingExceptionHandler.class);

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

    @Override
    public View initComponent(final ApplicationContext applicationContext)
    {
        this.applicationContext = Objects.requireNonNull(applicationContext, "applicationContext required");

        return this;
    }

    protected ApplicationContext getApplicationContext()
    {
        return applicationContext;
    }

    protected Logger getLogger()
    {
        return logger;
    }

    protected abstract ResourceMap getResourceMap();

    protected void setComponent(final Component component)
    {
        this.component = component;
    }
}
