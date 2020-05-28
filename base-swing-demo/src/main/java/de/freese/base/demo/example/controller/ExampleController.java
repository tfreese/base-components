/**
 * Created: 28.05.2020
 */

package de.freese.base.demo.example.controller;

import de.freese.base.demo.example.view.ExamplePanel;
import de.freese.base.demo.example.view.ExampleView;
import de.freese.base.demo.task.DurationStatisikTask;
import de.freese.base.mvc.ApplicationContext;
import de.freese.base.mvc.controller.AbstractController;
import de.freese.base.mvc.controller.Controller;
import de.freese.base.swing.task.AbstractSwingTask;
import de.freese.base.swing.task.DurationStatisikTaskListener;
import de.freese.base.swing.task.inputblocker.DefaultGlassPaneInputBlocker;

/**
 * Beispiel-{@link Controller}
 *
 * @author Thomas Freese
 */
public class ExampleController extends AbstractController
{
    /**
    *
    */
    private final ExampleView view;

    /**
     * Erstellt ein neues {@link ExampleController} Object.
     *
     * @param context {@link ApplicationContext}
     */
    public ExampleController(final ApplicationContext context)
    {
        super(context);

        this.view = new ExampleView(context);
    }

    /**
     * @see de.freese.base.mvc.controller.AbstractController#getBundleName()
     */
    @Override
    protected String getBundleName()
    {
        return "bundles/example";
    }

    /**
     * @see de.freese.base.mvc.controller.Controller#getName()
     */
    @Override
    public String getName()
    {
        return "example";
    }

    /**
     * @see de.freese.base.mvc.controller.Controller#getView()
     */
    @SuppressWarnings("unchecked")
    @Override
    public ExampleView getView()
    {
        return this.view;
    }

    /**
     * @see de.freese.base.mvc.controller.AbstractController#initialize()
     */
    @Override
    public void initialize()
    {
        super.initialize();

        ExamplePanel panel = getView().getComponent();

        panel.getButtonTaskStatistik().addActionListener(event -> {
            AbstractSwingTask<?, ?> task = new DurationStatisikTask();
            task.setInputBlocker(new DefaultGlassPaneInputBlocker(panel));

            // Könnte als konfigurierbare Funktion im Task implementiert werden.
            task.addPropertyChangeListener(new DurationStatisikTaskListener());

            getContext().getTaskManager().execute(task);
        });
    }
}
