package de.freese.base.swing.exception;

import java.awt.Component;

import de.freese.base.core.function.ExceptionHandler;
import de.freese.base.core.i18n.Translator;
import org.slf4j.Logger;

/**
 * @author Thomas Freese
 */
public interface SwingExceptionHandler extends ExceptionHandler
{
    void handleException(Throwable throwable, Logger logger);

    void handleException(Throwable throwable, Logger logger, Component parentComponent);

    void handleException(Throwable throwable, Logger logger, Component parentComponent, Translator translator);
}
