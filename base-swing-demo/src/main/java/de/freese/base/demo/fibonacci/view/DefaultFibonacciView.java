package de.freese.base.demo.fibonacci.view;

import java.awt.Component;

import de.freese.base.demo.fibonacci.FibonacciController;
import de.freese.base.demo.fibonacci.task.FibonacciTask;
import de.freese.base.mvc.ApplicationContext;
import de.freese.base.mvc.guistate.GuiStateManager;
import de.freese.base.mvc.view.AbstractView;
import de.freese.base.resourcemap.ResourceMap;
import de.freese.base.swing.task.TaskManager;
import de.freese.base.swing.task.inputblocker.DefaultGlassPaneInputBlocker;
import de.freese.base.swing.task.inputblocker.DefaultInputBlocker;

/**
 * @author Thomas Freese
 */
public class DefaultFibonacciView extends AbstractView implements FibonacciView {
    private FibonacciController controller;

    @Override
    public FibonacciPanel getComponent() {
        return (FibonacciPanel) super.getComponent();
    }

    @Override
    public FibonacciView initComponent(final ApplicationContext applicationContext) {
        super.initComponent(applicationContext);

        final FibonacciPanel fibonacciPanel = new FibonacciPanel();
        setComponent(fibonacciPanel);

        fibonacciPanel.init();

        fibonacciPanel.getTextField().setText("48");

        final ResourceMap resourceMap = getResourceMap();

        fibonacciPanel.getLabel().setText(resourceMap.getString("fibonacci.label"));
        fibonacciPanel.getLabelResult().setText(resourceMap.getString("fibonacci.result", 0));
        fibonacciPanel.getButtonGlassPaneBlock().setText(resourceMap.getString("fibonacci.button.glasspane.text"));
        fibonacciPanel.getButtonComponentBlock().setText(resourceMap.getString("fibonacci.button.component.text"));

        applicationContext.getService(GuiStateManager.class).restore(fibonacciPanel.getTextField(), "fibonacci.textfield");

        controller = new FibonacciController(this);

        fibonacciPanel.getButtonGlassPaneBlock().addActionListener(event -> {
            setResult(0);

            // Task mit GlassPaneInputBlocker
            final int value = Integer.parseInt(fibonacciPanel.getTextField().getText());

            final FibonacciTask task = new FibonacciTask(value, controller, resourceMap);
            task.setInputBlocker(new DefaultGlassPaneInputBlocker(fibonacciPanel));

            // Könnte auch im Task implementiert werden.
            task.addPropertyChangeListener(new FibonacciTaskListener(this));

            applicationContext.getService(TaskManager.class).execute(task);
        });

        fibonacciPanel.getButtonComponentBlock().addActionListener(event -> {
            setResult(0);

            // Task mit ComponentInputBlocker
            final int value = Integer.parseInt(fibonacciPanel.getTextField().getText());

            final FibonacciTask task = new FibonacciTask(value, controller, getResourceMap());
            task.setInputBlocker(new DefaultInputBlocker().add((Component) event.getSource()));

            // Könnte auch im Task implementiert werden.
            task.addPropertyChangeListener(new FibonacciTaskListener(this));

            applicationContext.getService(TaskManager.class).execute(task);
        });

        return this;
    }

    @Override
    public void release() {
        getService(GuiStateManager.class).store(getComponent().getTextField(), "fibonacci.textfield");

        this.controller.shutdown();
    }

    @Override
    public void setResult(final long value) {
        final String text = getResourceMap().getString("fibonacci.result", value);
        getComponent().getLabelResult().setText(text);
    }

    @Override
    protected ResourceMap getResourceMap() {
        return getApplicationContext().getResourceMap("bundles/fibonacci");
    }
}
