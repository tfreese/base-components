package de.freese.base.demo.fibonacci.view;

import de.freese.base.mvc.AbstractView;
import de.freese.base.resourcemap.ResourceMap;

/**
 * Konkrete Implementierung der IView.
 *
 * @author Thomas Freese
 */
public class DefaultFibonacciView extends AbstractView implements FibonacciView
{
    /**
     * @see de.freese.base.mvc.View#createGUI()
     */
    @Override
    public void createGUI()
    {
        setComponent(new FibonacciPanel());
        getComponent().initialize();

        getComponent().getTextField().setText("44");

        ResourceMap resourceMap = getResourceMap();

        getComponent().getLabel().setText(resourceMap.getString("fibonacci.label"));
        getComponent().getLabelResult().setText(resourceMap.getString("fibonacci.result", Long.valueOf(0)));
        getComponent().getButtonGlassPaneBlock().setText(resourceMap.getString("fibonacci.button.glasspane.text"));
        getComponent().getButtonComponentBlock().setText(resourceMap.getString("fibonacci.button.component.text"));
    }

    /**
     * @see de.freese.base.mvc.AbstractView#getComponent()
     */
    @Override
    public FibonacciPanel getComponent()
    {
        return (FibonacciPanel) super.getComponent();
    }

    /**
     * @see de.freese.base.mvc.AbstractView#restoreState()
     */
    @Override
    public void restoreState()
    {
        getContext().getGuiStateManager().restore(getComponent().getTextField(), "fibonacci.textfield");
    }

    /**
     * @see de.freese.base.mvc.AbstractView#saveState()
     */
    @Override
    public void saveState()
    {
        getContext().getGuiStateManager().store(getComponent().getTextField(), "fibonacci.textfield");
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
