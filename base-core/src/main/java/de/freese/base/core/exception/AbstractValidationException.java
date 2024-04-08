package de.freese.base.core.exception;

import java.io.Serial;

import de.freese.base.core.i18n.Translator;

/**
 * @author Thomas Freese
 */
public abstract class AbstractValidationException extends Exception {
    @Serial
    private static final long serialVersionUID = 9102013053396263064L;

    private final String appendMessage;
    private final String[] parameters;

    protected AbstractValidationException(final String message) {
        this(message, null, null);
    }

    protected AbstractValidationException(final String message, final String appendMessage) {
        this(message, null, appendMessage);
    }

    protected AbstractValidationException(final String message, final String[] parameters) {
        this(message, parameters, null);
    }

    protected AbstractValidationException(final String message, final String[] parameters, final String appendMessage) {
        super(message);

        this.parameters = parameters;
        this.appendMessage = appendMessage;
    }

    public String getAppendMessage() {
        return this.appendMessage;
    }

    public String[] getParameters() {
        return this.parameters;
    }

    public String translate(final Translator translatorAdapter) {
        final StringBuilder sb = new StringBuilder();

        if (getParameters() != null && getParameters().length > 0) {
            sb.append(translatorAdapter.translate(getMessage(), (Object[]) getParameters()));
        }
        else {
            sb.append(translatorAdapter.translate(getMessage()));
        }

        sb.append("\n\n");
        sb.append(getAppendMessage());

        return sb.toString();
    }
}
