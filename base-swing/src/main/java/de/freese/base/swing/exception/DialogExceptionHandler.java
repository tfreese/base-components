// Created: 17.07.2012
package de.freese.base.swing.exception;

import java.awt.Component;

import de.freese.base.core.i18n.Translator;
import org.jdesktop.swingx.error.ErrorInfo;
import org.slf4j.Logger;

/**
 * Swing Implementierung des ExceptionHandlers mit Fehlerdialog.
 *
 * @author Thomas Freese
 */
public class DialogExceptionHandler extends DefaultExceptionHandler
{
    /**
     *
     */
    private final boolean enableSendMail;

    /**
     * Erstellt ein neues {@link DialogExceptionHandler} Object.
     */
    public DialogExceptionHandler()
    {
        super();

        this.enableSendMail = false;
    }

    /**
     * @see de.freese.base.swing.exception.DefaultExceptionHandler#handleException(java.lang.Throwable, org.slf4j.Logger, java.awt.Component,
     * de.freese.base.core.i18n.Translator)
     */
    @Override
    public void handleException(final Throwable throwable, final Logger logger, final Component parentComponent, final Translator translatorAdapter)
    {
        logger.error(throwable.getMessage(), throwable);

        String message = getTranslatedMessage(throwable, translatorAdapter);

        showErrorPane(parentComponent, message, throwable);
    }

    /**
     * @param parentComponent {@link Component}
     * @param message String
     * @param throwable {@link Throwable}
     */
    private void showErrorPane(final Component parentComponent, final String message, final Throwable throwable)
    {
        ErrorInfo errorInfo = new ErrorInfo("ERROR", message, null, null, throwable, null, null);

        ErrorPane.showDialog(parentComponent, errorInfo, this.enableSendMail);
    }
}
