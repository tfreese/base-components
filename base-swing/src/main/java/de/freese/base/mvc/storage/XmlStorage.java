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
public final class XmlStorage {
    private static final class XMLExceptionListener implements ExceptionListener {
        private Exception exception;

        @Override
        public void exceptionThrown(final Exception ex) {
            if (this.exception == null) {
                this.exception = ex;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T loadBean(final InputStream inputStream) throws IOException {
        XMLExceptionListener el = new XMLExceptionListener();

        try (XMLDecoder decoder = new XMLDecoder(inputStream)) {
            decoder.setExceptionListener(el);
            Object bean = decoder.readObject();

            if (el.exception != null) {
                IOException ex = new IOException(el.exception.getMessage());
                ex.setStackTrace(el.exception.getStackTrace());

                throw ex;
            }

            return (T) bean;
        }
    }

    public static ByteArrayOutputStream saveBean(final Object bean) throws IOException {
        XMLExceptionListener el = new XMLExceptionListener();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (XMLEncoder encoder = new XMLEncoder(baos)) {
            encoder.setExceptionListener(el);
            encoder.writeObject(bean);
        }

        if (el.exception != null) {
            IOException ex = new IOException(el.exception.getMessage());
            ex.setStackTrace(el.exception.getStackTrace());

            throw ex;
        }

        return baos;
    }

    public static void saveBean(final OutputStream outputStream, final Object bean) throws IOException {
        XMLExceptionListener el = new XMLExceptionListener();

        try (XMLEncoder encoder = new XMLEncoder(outputStream)) {
            encoder.setExceptionListener(el);
            encoder.writeObject(bean);
        }

        if (el.exception != null) {
            IOException ex = new IOException(el.exception.getMessage());
            ex.setStackTrace(el.exception.getStackTrace());

            throw ex;
        }
    }

    private XmlStorage() {
        super();
    }
}
