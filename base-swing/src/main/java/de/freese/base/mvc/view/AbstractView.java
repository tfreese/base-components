// Created: 05.02.23
package de.freese.base.mvc.view;

import java.awt.Component;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.base.mvc.ApplicationContext;
import de.freese.base.resourcemap.ResourceMap;
import de.freese.base.swing.exception.SwingExceptionHandler;

/**
 * @author Thomas Freese
 */
public abstract class AbstractView implements View {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private ApplicationContext applicationContext;
    private Component component;

    @Override
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public Component getComponent() {
        return component;
    }

    @Override
    public <T> T getService(final Class<T> clazz) {
        return getApplicationContext().getService(clazz);
    }

    @Override
    public void handleException(final Throwable throwable) {
        final SwingExceptionHandler exceptionHandler = getService(SwingExceptionHandler.class);

        exceptionHandler.handleException(throwable, getLogger(), getComponent(), (key, args) -> getResourceMap().getString(key, args));

        // getLogger().error(throwable.getMessage(), throwable);
        //
        // // Dialoge sollten nicht die Tasks blockieren bei Fehlermeldungen
        // SwingUtilities.invokeLater(new Runnable()
        // {
        // @Override
        // public void run()
        // {
        // JOptionPane.showMessageDialog(getViewComponent(), throwable, throwable.getClass()
        // .getSimpleName(), JOptionPane.ERROR_MESSAGE);
        // }
        // });
    }

    @Override
    public View initComponent(final ApplicationContext applicationContext) {
        this.applicationContext = Objects.requireNonNull(applicationContext, "applicationContext required");

        return this;
    }

    @Override
    public <T> void registerService(final Class<T> clazz, final T service) {
        getApplicationContext().registerService(clazz, service);
    }

    protected Logger getLogger() {
        return logger;
    }

    protected abstract ResourceMap getResourceMap();

    protected void setComponent(final Component component) {
        this.component = component;
    }
}
