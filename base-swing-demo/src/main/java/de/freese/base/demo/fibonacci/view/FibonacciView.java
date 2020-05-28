package de.freese.base.demo.fibonacci.view;

import de.freese.base.mvc.view.View;

/**
 * Viewinterface für die Fibonacci Demo.
 *
 * @author Thomas Freese
 */
public interface FibonacciView extends View<FibonacciPanel>
{
    /**
     * Setzt das Ergebniss des Prozesses in die IView.
     *
     * @param value long
     */
    public void setResult(long value);
}
