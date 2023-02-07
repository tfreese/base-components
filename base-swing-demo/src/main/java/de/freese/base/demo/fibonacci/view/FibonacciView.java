package de.freese.base.demo.fibonacci.view;

import de.freese.base.mvc.Releasable;
import de.freese.base.mvc.view.View;

/**
 * @author Thomas Freese
 */
public interface FibonacciView extends View, Releasable
{
    void setResult(long value);
}
