// Created: 28.05.2020
package de.freese.base.demo.example;

import de.freese.base.demo.example.view.ExamplePanel;
import de.freese.base.demo.example.view.ExampleView;
import de.freese.base.mvc.AbstractController;
import de.freese.base.swing.task.AbstractSwingTask;
import de.freese.base.swing.task.DurationStatistikTaskListener;
import de.freese.base.swing.task.inputblocker.DefaultGlassPaneInputBlocker;

/**
 * @author Thomas Freese
 */
public class ExampleController extends AbstractController
{
    private final ExampleView view;

    public ExampleController()
    {
        super();

        this.view = new ExampleView();
    }

    /**
     * @see de.freese.base.mvc.Controller#getView()
     */
    @Override
    public ExampleView getView()
    {
        return this.view;
    }

    /**
     * @see de.freese.base.mvc.AbstractController#initialize()
     */
    @Override
    public void initialize()
    {
        super.initialize();

        ExamplePanel panel = getView().getComponent();

        panel.getButtonTaskStatistik().addActionListener(event ->
        {
            AbstractSwingTask<?, ?> task = new DurationStatistikTask();
            task.setInputBlocker(new DefaultGlassPaneInputBlocker(panel));

            // Könnte als konfigurierbare Funktion im Task implementiert werden.
            task.addPropertyChangeListener(new DurationStatistikTaskListener());

            getContext().getTaskManager().execute(task);
        });
    }
}
