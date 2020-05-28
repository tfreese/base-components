/**
 * Created: 28.05.2020
 */

package de.freese.base.demo.fibonacci.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;
import de.freese.base.swing.task.SwingTask;

/**
 * {@link PropertyChangeListener} für das Fibonacci Beispiel.
 *
 * @author Thomas Freese
 */
public class FibonacciTaskListener implements PropertyChangeListener
{
    /**
     *
     */
    private final FibonacciView view;

    /**
     * Erstellt ein neues {@link FibonacciTaskListener} Object.
     *
     * @param view {@link FibonacciView}
     */
    public FibonacciTaskListener(final FibonacciView view)
    {
        super();

        this.view = Objects.requireNonNull(view, "view required");
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
            this.view.handleException((Throwable) event.getNewValue());
        }
        else if (SwingTask.PROPERTY_SUCCEEDED.equals(propertyName))
        {
            this.view.setResult((long) event.getNewValue());
        }
    }
}