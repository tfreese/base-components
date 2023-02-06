// Created: 03.02.23
package de.freese.base.demo2.example;

import de.freese.base.demo.example.DurationStatistikTask;
import de.freese.base.mvc2.ApplicationContext;
import de.freese.base.mvc2.view.AbstractView;
import de.freese.base.resourcemap.ResourceMap;
import de.freese.base.swing.task.AbstractSwingTask;
import de.freese.base.swing.task.DurationStatistikTaskListener;
import de.freese.base.swing.task.TaskManager;
import de.freese.base.swing.task.inputblocker.DefaultGlassPaneInputBlocker;

/**
 * @author Thomas Freese
 */
public class ExampleView extends AbstractView
{
    @Override
    public ExampleView initComponent(final ApplicationContext applicationContext)
    {
        super.initComponent(applicationContext);

        ResourceMap resourceMap = getResourceMap();

        ExamplePanel examplePanel = new ExamplePanel();
        setComponent(examplePanel);

        examplePanel.init();
        examplePanel.getButtonTaskStatistik().setText(resourceMap.getString("example.button.task.statistik.text"));

        examplePanel.getButtonTaskStatistik().addActionListener(event ->
        {
            AbstractSwingTask<?, ?> task = new DurationStatistikTask();
            task.setInputBlocker(new DefaultGlassPaneInputBlocker(examplePanel));

            // Könnte als konfigurierbare Funktion im Task implementiert werden.
            task.addPropertyChangeListener(new DurationStatistikTaskListener());

            applicationContext.getService(TaskManager.class).execute(task);
        });

        return this;
    }

    @Override
    protected ResourceMap getResourceMap()
    {
        return getApplicationContext().getResourceMap("bundles/example");
    }
}
