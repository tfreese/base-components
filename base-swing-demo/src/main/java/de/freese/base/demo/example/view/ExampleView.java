package de.freese.base.demo.example.view;

import de.freese.base.mvc.AbstractView;
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
     */
    public ExampleView()
    {
        super();
    }

    /**
     * @see de.freese.base.mvc.View#createGUI()
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
     * @see de.freese.base.mvc.AbstractView#getComponent()
     */
    @Override
    public ExamplePanel getComponent()
    {
        return super.getComponent();
    }
}
