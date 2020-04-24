package de.freese.base.demo.fibonacci.bp;

import java.util.function.LongConsumer;
import de.freese.base.mvc.process.BusinessProcess;

/**
 * Interface des BuisinessProcesses für das Fibonacci Beispiel.
 *
 * @author Thomas Freese
 */
public interface FibonacciBP extends BusinessProcess
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
     * Liefert die Anzahl der benötigten mathematischen Operationen zurück.<br>
     * ACHTUNG: Dieser Wert ist bedeutend grösser als das Ergebnis !
     *
     * @param n int
     * @return long
     */
    public long getOperationCount(final int n);
}
