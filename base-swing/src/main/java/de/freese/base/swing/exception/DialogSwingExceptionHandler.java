// Created: 17.07.2012
package de.freese.base.swing.exception;

import java.awt.Component;

import org.jdesktop.swingx.error.ErrorInfo;
import org.slf4j.Logger;

import de.freese.base.core.i18n.Translator;

/**
 * @author Thomas Freese
 */
public class DialogSwingExceptionHandler extends DefaultSwingExceptionHandler {
    private final boolean enableSendMail;

    public DialogSwingExceptionHandler() {
        super();

        enableSendMail = false;
    }

    @Override
    public void handleException(final Throwable throwable, final Logger logger, final Component parentComponent, final Translator translatorAdapter) {
        logger.error(throwable.getMessage(), throwable);

        final String message = getTranslatedMessage(throwable, translatorAdapter);

        showErrorPane(parentComponent, message, throwable);
    }

    private void showErrorPane(final Component parentComponent, final String message, final Throwable throwable) {
        final ErrorInfo errorInfo = new ErrorInfo("ERROR", message, null, null, throwable, null, null);

        ErrorPane.showDialog(parentComponent, errorInfo, enableSendMail);
    }
}
