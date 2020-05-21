package de.freese.base.demo.fibonacci.view;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import de.freese.base.demo.fibonacci.bp.FibonacciBP;
import de.freese.base.demo.fibonacci.task.FibonacciTask;
import de.freese.base.mvc.context.ApplicationContext;
import de.freese.base.mvc.view.AbstractView;
import de.freese.base.resourcemap.ResourceMap;
import de.freese.base.swing.task.SwingTask;
import de.freese.base.swing.task.inputblocker.ComponentInputBlocker;
import de.freese.base.swing.task.inputblocker.DefaultGlassPaneInputBlocker;

/**
 * Konkrete Implementierung der IView.
 *
 * @author Thomas Freese
 */
public class DefaultFibonacciView extends AbstractView implements FibonacciView
{
    /**
     * ITaskListener fuer das Fibonacci Beispiel.
     *
     * @author Thomas Freese
     */
    private class FibonacciTaskListener implements PropertyChangeListener
    {
        /**
         * Erstellt ein neues {@link FibonacciTaskListener} Object.
         */
        public FibonacciTaskListener()
        {
            super();
        }

        /**
         * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
         */
        @Override
        public void propertyChange(final PropertyChangeEvent event)
        {
            String propertyName = event.getPropertyName();

            if (SwingTask.PROPERTY_FAILED.equals(propertyName))
            {
                handleException((Throwable) event.getNewValue());
            }
            else if (SwingTask.PROPERTY_SUCCEEDED.equals(propertyName))
            {
                setResult((long) event.getNewValue());

                getLogger().info("Succeeded");
            }
        }
    }

    /**
     * Erstellt ein neues {@link DefaultFibonacciView} Object.
     *
     * @param process {@link FibonacciBP}
     * @param context {@link ApplicationContext}
     */
    public DefaultFibonacciView(final FibonacciBP process, final ApplicationContext context)
    {
        super(process, context);
    }

    /**
     * @see de.freese.base.mvc.view.AbstractView#getComponent()
     */
    @Override
    public FibonacciPanel getComponent()
    {
        return (FibonacciPanel) super.getComponent();
    }

    /**
     * @see de.freese.base.mvc.view.AbstractView#getProcess()
     */
    @Override
    public FibonacciBP getProcess()
    {
        return (FibonacciBP) super.getProcess();
    }

    /**
     * @see de.freese.base.mvc.view.AbstractView#getResourceMap()
     */
    @Override
    protected ResourceMap getResourceMap()
    {
        return getContext().getResourceMap("fibonacci");
    }

    /**
     * @see de.freese.base.mvc.view.AbstractView#initialize()
     */
    @Override
    public void initialize()
    {
        super.initialize();

        setComponent(new FibonacciPanel());
        getComponent().initialize();

        getComponent().getTextField().setText("44");

        ResourceMap resourceMap = getResourceMap();

        getComponent().getLabel().setText(resourceMap.getString("fibonacci.label"));
        getComponent().getLabelResult().setText(resourceMap.getString("fibonacci.result", Long.valueOf(0)));

        getComponent().getButtonGlassPaneBlock().setText(resourceMap.getString("fibonacci.button.glasspane.text"));
        getComponent().getButtonGlassPaneBlock().addActionListener(event -> {
            setResult(0);

            // Task mit GlassPaneInputBlocker
            FibonacciTask task = new FibonacciTask(getProcess(), getResourceMap());
            task.setInputBlocker(new DefaultGlassPaneInputBlocker(getComponent()));

            task.setValue(Integer.parseInt(getComponent().getTextField().getText()));
            task.addPropertyChangeListener(new FibonacciTaskListener());

            getContext().getTaskManager().execute(task);
        });

        getComponent().getButtonComponentBlock().setText(resourceMap.getString("fibonacci.button.component.text"));
        getComponent().getButtonComponentBlock().addActionListener(event -> {
            setResult(0);

            // Task mit ComponentInputBlocker
            FibonacciTask task = new FibonacciTask(getProcess(), getResourceMap());
            task.setInputBlocker(new ComponentInputBlocker((Component) event.getSource()));

            task.setValue(Integer.parseInt(getComponent().getTextField().getText()));
            task.addPropertyChangeListener(new FibonacciTaskListener());

            getContext().getTaskManager().execute(task);
        });
    }

    /**
     * @see de.freese.base.mvc.view.AbstractView#restoreState()
     */
    @Override
    public void restoreState()
    {
        try
        {
            getContext().getGuiStateManager().restore(getComponent().getTextField(), "fibonacci.textfield");
        }
        catch (Exception ex)
        {
            getLogger().error(null, ex);
        }
    }

    /**
     * @see de.freese.base.mvc.view.AbstractView#saveState()
     */
    @Override
    public void saveState()
    {
        try
        {
            getContext().getGuiStateManager().store(getComponent().getTextField(), "fibonacci.textfield");
        }
        catch (Exception ex)
        {
            getLogger().error(null, ex);
        }
    }

    /**
     * @see de.freese.base.demo.fibonacci.view.FibonacciView#setResult(long)
     */
    @Override
    public void setResult(final long value)
    {
        ResourceMap resourceMap = getResourceMap();

        String text = resourceMap.getString("fibonacci.result", Long.valueOf(value));
        getComponent().getLabelResult().setText(text);
    }
}
