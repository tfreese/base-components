package de.freese.base.mvc.storage;

import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Thomas Freese
 */
public final class XmlStorage
{
    /**
     * ExceptionListener für den {@link XMLEncoder}/{@link XMLDecoder}.
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
     * Laden der Bean von XML über {@link XMLDecoder}.
     *
     * @param inputStream {@link InputStream}
     * @param <T> Type
     *
     * @return Object
     *
     * @throws IOException Falls was schiefgeht.
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
                IOException ex = new IOException(el.exception.getMessage());
                ex.setStackTrace(el.exception.getStackTrace());

                throw ex;
            }

            return (T) bean;
        }
    }

    /**
     * Speichert das Bean als XML über {@link XMLEncoder}.
     *
     * @param bean Object
     *
     * @return {@link ByteArrayOutputStream}
     *
     * @throws IOException Falls was schiefgeht.
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
            IOException ex = new IOException(el.exception.getMessage());
            ex.setStackTrace(el.exception.getStackTrace());

            throw ex;
        }

        return baos;
    }

    /**
     * Speichert das Bean als XML über {@link XMLEncoder}.
     *
     * @param outputStream {@link OutputStream}
     * @param bean Object
     *
     * @throws IOException Falls was schiefgeht.
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
            IOException ex = new IOException(el.exception.getMessage());
            ex.setStackTrace(el.exception.getStackTrace());

            throw ex;
        }
    }

    /**
     * Erstellt ein neues {@link XmlStorage} Object.
     */
    private XmlStorage()
    {
        super();
    }
}
