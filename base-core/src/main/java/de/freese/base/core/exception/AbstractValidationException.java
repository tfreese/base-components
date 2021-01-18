package de.freese.base.core.exception;

import de.freese.base.core.i18n.Translator;

/**
 * ValidationException mit der Möglichkeit der Angabe für übersetzbare Texte mit Parametern.
 *
 * @author Thomas Freese
 */
public abstract class AbstractValidationException extends Exception
{
    /**
     *
     */
    private static final long serialVersionUID = 9102013053396263064L;

    /**
     *
     */
    private final String appendMessage;

    /**
     *
     */
    private final String[] parameters;

    /**
     * Creates a new {@link AbstractValidationException} object.
     *
     * @param message String
     */
    protected AbstractValidationException(final String message)
    {
        this(message, null, null);
    }

    /**
     * Creates a new {@link AbstractValidationException} object.
     *
     * @param message String
     * @param appendMessage String
     */
    protected AbstractValidationException(final String message, final String appendMessage)
    {
        this(message, null, appendMessage);
    }

    /**
     * Creates a new {@link AbstractValidationException} object.
     *
     * @param message String
     * @param parameters String[]
     */
    protected AbstractValidationException(final String message, final String[] parameters)
    {
        this(message, parameters, null);
    }

    /**
     * Creates a new {@link AbstractValidationException} object.
     *
     * @param message String
     * @param parameters String[]
     * @param appendMessage String
     */
    protected AbstractValidationException(final String message, final String[] parameters, final String appendMessage)
    {
        super(message);

        this.parameters = parameters;
        this.appendMessage = appendMessage;
    }

    /**
     * Zusatztext.
     *
     * @return String
     */
    public String getAppendMessage()
    {
        return this.appendMessage;
    }

    /**
     * Zusatzparameter.
     *
     * @return String[]
     */
    public String[] getParameters()
    {
        return this.parameters;
    }

    /**
     * Uebersetzt den Inhalt der Exception.
     *
     * @param translatorAdapter {@link Translator}
     * @return String
     */
    public String translate(final Translator translatorAdapter)
    {
        StringBuilder sb = new StringBuilder();

        if ((getParameters() != null) && (getParameters().length > 0))
        {
            sb.append(translatorAdapter.translate(getMessage(), (Object[]) getParameters()));
        }
        else
        {
            sb.append(translatorAdapter.translate(getMessage()));
        }

        sb.append("\n\n");
        sb.append(getAppendMessage());

        return sb.toString();
    }
}
