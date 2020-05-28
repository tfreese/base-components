package de.freese.base.demo.example.view;

import de.freese.base.mvc.ApplicationContext;
import de.freese.base.mvc.view.AbstractView;
import de.freese.base.resourcemap.ResourceMap;

/**
 * BeispielView.
 *
 * @author Thomas Freese
 */
public class ExampleView extends AbstractView<ExamplePanel>
{
    /**
     * Erstellt ein neues {@link ExampleView} Object.
     *
     * @param context {@link ApplicationContext}
     */
    public ExampleView(final ApplicationContext context)
    {
        super(context);
    }

    /**
     * @see de.freese.base.mvc.view.View#createGUI()
     */
    @Override
    public void createGUI()
    {
        setComponent(new ExamplePanel());
        getComponent().initialize();

        ResourceMap resourceMap = getResourceMap();

        getComponent().getButtonTaskStatistik().setText(resourceMap.getString("example.button.task.statistik.text"));
    }

    /**
     * @see de.freese.base.mvc.view.AbstractView#getComponent()
     */
    @Override
    public ExamplePanel getComponent()
    {
        return super.getComponent();
    }

    /**
     * @see de.freese.base.mvc.view.AbstractView#getResourceMap()
     */
    @Override
    protected ResourceMap getResourceMap()
    {
        return getContext().getResourceMap("example");
    }
}
