package de.freese.base.mvc.storage;

import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Encoder/Decoder einer Bean fuer XML.
 *
 * @author Thomas Freese
 */
public final class XMLStorage
{
    /**
     * ExceptionListener fuer den {@link XMLEncoder}/{@link XMLDecoder}.
     *
     * @author Thomas Freese
     */
    private static class XMLExceptionListener implements ExceptionListener
    {
        /**
        *
        */
        public Exception exception;

        /**
         * Erstellt ein neues {@link XMLExceptionListener} Object.
         */
        public XMLExceptionListener()
        {
            super();
        }

        /**
         * @see java.beans.ExceptionListener#exceptionThrown(java.lang.Exception)
         */
        @Override
        public void exceptionThrown(final Exception ex)
        {
            if (this.exception == null)
            {
                this.exception = ex;
            }
        }
    }

    /**
     * Laden der Bean von XML ueber {@link XMLDecoder}.
     *
     * @param inputStream {@link InputStream}
     * @param <T> Konkreter Typ des Objectes
     * @return Object
     * @throws IOException Falls was schief geht.
     */
    @SuppressWarnings("unchecked")
    public static <T> T loadBean(final InputStream inputStream) throws IOException
    {
        XMLExceptionListener el = new XMLExceptionListener();

        try (XMLDecoder decoder = new XMLDecoder(inputStream))
        {
            decoder.setExceptionListener(el);
            Object bean = decoder.readObject();

            if (el.exception != null)
            {
                IOException ioex = new IOException(el.exception.getMessage());
                ioex.setStackTrace(el.exception.getStackTrace());

                throw ioex;
            }

            return (T) bean;
        }
    }

    /**
     * Speichert das Bean als XML ueber {@link XMLEncoder}.
     *
     * @param bean Object
     * @return {@link ByteArrayOutputStream}
     * @throws IOException Falls was schief geht.
     */
    public static ByteArrayOutputStream saveBean(final Object bean) throws IOException
    {
        XMLExceptionListener el = new XMLExceptionListener();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (XMLEncoder encoder = new XMLEncoder(baos))
        {
            encoder.setExceptionListener(el);
            encoder.writeObject(bean);
        }

        if (el.exception != null)
        {
            IOException ioex = new IOException(el.exception.getMessage());
            ioex.setStackTrace(el.exception.getStackTrace());

            throw ioex;
        }

        return baos;
    }

    /**
     * Speichert das Bean als XML ueber {@link XMLEncoder}.
     *
     * @param outputStream {@link OutputStream}
     * @param bean Object
     * @throws IOException Falls was schief geht.
     */
    public static void saveBean(final OutputStream outputStream, final Object bean) throws IOException
    {
        XMLExceptionListener el = new XMLExceptionListener();

        try (XMLEncoder encoder = new XMLEncoder(outputStream))
        {
            encoder.setExceptionListener(el);
            encoder.writeObject(bean);
        }

        if (el.exception != null)
        {
            IOException ioex = new IOException(el.exception.getMessage());
            ioex.setStackTrace(el.exception.getStackTrace());

            throw ioex;
        }
    }

    /**
     * Erstellt ein neues {@link XMLStorage} Object.
     */
    private XMLStorage()
    {
        super();
    }
}
