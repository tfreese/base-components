package de.freese.base.swing.exception;

import java.awt.Component;

import de.freese.base.core.exception.AbstractValidationException;
import de.freese.base.core.i18n.Translator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public class DefaultSwingExceptionHandler implements SwingExceptionHandler
{
    private static final Translator DEFAULT_TRANSLATOR_ADAPTER = String::format;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @see de.freese.base.core.function.ExceptionHandler#handle(java.lang.Exception)
     */
    @Override
    public void handle(final Exception ex)
    {
        handleException(ex, getLogger());
    }

    /**
     * @see de.freese.base.swing.exception.SwingExceptionHandler#handleException(java.lang.Throwable, org.slf4j.Logger)
     */
    @Override
    public void handleException(final Throwable throwable, final Logger logger)
    {
        handleException(throwable, logger, null);
    }

    /**
     * @see de.freese.base.swing.exception.SwingExceptionHandler#handleException(java.lang.Throwable, org.slf4j.Logger, java.awt.Component)
     */
    @Override
    public void handleException(final Throwable throwable, final Logger logger, final Component parentComponent)
    {
        handleException(throwable, logger, parentComponent, null);
    }

    /**
     * @see de.freese.base.swing.exception.SwingExceptionHandler#handleException(java.lang.Throwable, org.slf4j.Logger, java.awt.Component,
     * de.freese.base.core.i18n.Translator)
     */
    @Override
    public void handleException(final Throwable throwable, final Logger logger, final Component parentComponent, final Translator translator)
    {
        String message = getTranslatedMessage(throwable, translator);

        logger.error(message);
        logger.error(throwable.getMessage(), throwable);
    }

    protected Logger getLogger()
    {
        return this.logger;
    }

    /**
     * Liefert die übersetzte Exception (HumanReadable).
     */
    protected String getTranslatedMessage(final Throwable throwable, final Translator translator)
    {
        String message = throwable.getLocalizedMessage();

        if (message == null)
        {
            message = throwable.getMessage();
        }

        if (throwable instanceof NullPointerException)
        {
            message = String.format("%s: Object not exist", throwable.getClass().getSimpleName());
        }
        else if (throwable instanceof UnsupportedOperationException)
        {
            message = String.format("%s: %s", throwable.getClass().getSimpleName(), throwable.getStackTrace()[0].getMethodName());
        }
        else if (throwable instanceof AbstractValidationException ve)
        {
            Translator ta = translator;

            if (ta == null)
            {
                ta = DEFAULT_TRANSLATOR_ADAPTER;
            }

            message = ve.translate(ta);
        }

        return message;
    }
}
