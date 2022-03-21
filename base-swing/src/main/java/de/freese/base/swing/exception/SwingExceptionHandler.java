package de.freese.base.swing.exception;

import java.awt.Component;

import de.freese.base.core.function.ExceptionHandler;
import de.freese.base.core.i18n.Translator;
import org.slf4j.Logger;

/**
 * Interface eines ExceptionHandlers.
 *
 * @author Thomas Freese
 */
public interface SwingExceptionHandler extends ExceptionHandler
{
    /**
     * Verarbeitet ein Throwable und loggt die Fehlermeldung.
     *
     * @param throwable {@link Throwable}
     * @param logger {@link Logger}
     */
    void handleException(Throwable throwable, Logger logger);

    /**
     * Verarbeitet ein Throwable, loggt die Fehlermeldung und nutzt die ParentComponent für einen FehlerDialog.
     *
     * @param throwable {@link Throwable}
     * @param logger {@link Logger}
     * @param parentComponent {@link Component}
     */
    void handleException(Throwable throwable, Logger logger, Component parentComponent);

    /**
     * Verarbeitet ein Throwable, loggt die Fehlermeldung und nutzt die ParentComponent für einen FehlerDialog.
     *
     * @param throwable {@link Throwable}
     * @param logger {@link Logger}
     * @param parentComponent {@link Component}
     * @param translator {@link Translator}
     */
    void handleException(Throwable throwable, Logger logger, Component parentComponent, Translator translator);
}
