package de.freese.base.demo.fibonacci.view;

import de.freese.base.mvc.View;

/**
 * @author Thomas Freese
 */
public interface FibonacciView extends View
{
    /**
     * @see de.freese.base.mvc.View#getComponent()
     */
    @Override
    FibonacciPanel getComponent();

    void setResult(long value);
}
