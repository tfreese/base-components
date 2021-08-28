package de.freese.base.demo.fibonacci.view;

import de.freese.base.mvc.View;

/**
 * Viewinterface für die Fibonacci Demo.
 *
 * @author Thomas Freese
 */
public interface FibonacciView extends View
{
    /**
     * @see de.freese.base.mvc.View#getComponent()
     */
    @Override
    FibonacciPanel getComponent();

    /**
     * Setzt das Ergebniss des Prozesses in die IView.
     *
     * @param value long
     */
    void setResult(long value);
}
