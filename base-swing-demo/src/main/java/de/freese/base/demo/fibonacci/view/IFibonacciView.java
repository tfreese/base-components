package de.freese.base.demo.fibonacci.view;

import de.freese.base.mvc.view.View;

/**
 * Viewinterface fuer die Fibonacci Demo.
 * 
 * @author Thomas Freese
 */
public interface IFibonacciView extends View
{
	/**
	 * Setzt das Ergebniss des Prozesses in die IView.
	 * 
	 * @param value long
	 */
	public void setResult(long value);
}
