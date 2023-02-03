// Created: 03.02.23
package de.freese.base.demo2.example;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Map;

import de.freese.base.demo.example.DurationStatistikTask;
import de.freese.base.mvc2.ApplicationContext;
import de.freese.base.mvc2.View;
import de.freese.base.resourcemap.ResourceMap;
import de.freese.base.swing.task.AbstractSwingTask;
import de.freese.base.swing.task.DurationStatistikTaskListener;
import de.freese.base.swing.task.TaskManager;
import de.freese.base.swing.task.inputblocker.DefaultGlassPaneInputBlocker;

/**
 * @author Thomas Freese
 */
public class ExampleView implements View
{
    @Override
    public Map<String, ActionListener> getInterestedMenuAndToolbarActions()
    {
        return Collections.emptyMap();
    }

    @Override
    public Component initComponent(final ApplicationContext applicationContext)
    {
        ResourceMap resourceMap = applicationContext.getResourceMap("bundles/example");

        ExamplePanel examplePanel = new ExamplePanel();
        examplePanel.initialize();
        examplePanel.getButtonTaskStatistik().setText(resourceMap.getString("example.button.task.statistik.text"));

        examplePanel.getButtonTaskStatistik().addActionListener(event ->
        {
            AbstractSwingTask<?, ?> task = new DurationStatistikTask();
            task.setInputBlocker(new DefaultGlassPaneInputBlocker(examplePanel));

            // Könnte als konfigurierbare Funktion im Task implementiert werden.
            task.addPropertyChangeListener(new DurationStatistikTaskListener());

            applicationContext.getService(TaskManager.class).execute(task);
        });

        return examplePanel;
    }
}
