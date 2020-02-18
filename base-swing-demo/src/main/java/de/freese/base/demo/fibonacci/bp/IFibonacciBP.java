package de.freese.base.demo.fibonacci.bp;

import java.util.function.LongConsumer;

import de.freese.base.mvc.process.BusinessProcess;

/**
 * Interface des BuisinessProcesses fuer das Fibonacci Beispiel.
 *
 * @author Thomas Freese
 */
public interface IFibonacciBP extends BusinessProcess
{
    /**
     * Liefert den Fibonacci-Wert des Parameters.
     * 
     * @param n int
     * @param operationConsumer {@link LongConsumer}
     * @return long
     */
    public long fibonacci(int n, LongConsumer operationConsumer);

    /**
     * Liefert die Anzahl der benoetigten mathematischen Operationen zurueck.<br>
     * ACHTUNG: Dieser Wert ist bedeutend groesser als das Ergebnis !
     * 
     * @param n int
     * @return long
     */
    public long operations(final int n);
}
