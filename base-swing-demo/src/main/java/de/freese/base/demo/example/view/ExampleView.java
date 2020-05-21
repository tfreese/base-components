package de.freese.base.demo.example.view;

import de.freese.base.demo.task.DurationStatisikTask;
import de.freese.base.mvc.context.ApplicationContext;
import de.freese.base.mvc.process.BusinessProcess;
import de.freese.base.mvc.view.AbstractView;
import de.freese.base.resourcemap.ResourceMap;
import de.freese.base.swing.task.AbstractTask;
import de.freese.base.swing.task.DurationStatisikTaskListener;
import de.freese.base.swing.task.inputblocker.DefaultGlassPaneInputBlocker;

/**
 * BeispielView.
 *
 * @author Thomas Freese
 */
public class ExampleView extends AbstractView
{
    /**
     * Erstellt ein neues {@link ExampleView} Object.
     *
     * @param process {@link BusinessProcess}
     * @param context {@link ApplicationContext}
     */
    public ExampleView(final BusinessProcess process, final ApplicationContext context)
    {
        super(process, context);
    }

    /**
     * @see de.freese.base.mvc.view.AbstractView#getComponent()
     */
    @Override
    public ExamplePanel getComponent()
    {
        return (ExamplePanel) super.getComponent();
    }

    /**
     * @see de.freese.base.mvc.view.AbstractView#getResourceMap()
     */
    @Override
    protected ResourceMap getResourceMap()
    {
        return getContext().getResourceMap("example");
    }

    /**
     * @see de.freese.base.mvc.view.AbstractView#initialize()
     */
    @Override
    public void initialize()
    {
        super.initialize();

        setComponent(new ExamplePanel());
        getComponent().initialize();

        ResourceMap resourceMap = getResourceMap();

        getComponent().getButtonTaskStatistik().setText(resourceMap.getString("example.button.task.statistik.text"));

        getComponent().getButtonTaskStatistik().addActionListener(event -> {
            AbstractTask<?, ?> task = new DurationStatisikTask();
            task.setInputBlocker(new DefaultGlassPaneInputBlocker(getComponent()));

            // Könnte als konfigurierbare Funktion im Task implementiert werden
            task.addPropertyChangeListener(new DurationStatisikTaskListener());

            getContext().getTaskManager().execute(task);
        });
    }
}
