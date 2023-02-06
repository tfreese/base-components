package de.freese.base.demo2.fibonacci.view;

import de.freese.base.mvc2.Releasable;
import de.freese.base.mvc2.view.View;

/**
 * @author Thomas Freese
 */
public interface FibonacciView extends View, Releasable
{
    void setResult(long value);
}
