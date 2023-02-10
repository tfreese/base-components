package de.freese.base.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * @author Thomas Freese
 */
public final class XmlUtils {
    /**
     * Liefert das {@link Document} aus dem Byte-Array.
     */
    public static Document getDocument(final byte[] bytes) throws Exception {
        return getDocument(new ByteArrayInputStream(bytes));
    }

    /**
     * Liefert das {@link Document} aus der {@link InputSource}.
     */
    public static Document getDocument(final InputSource inputSource) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        // Protect against to XXE attacks.
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, ""); // Compliant
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, ""); // Compliant

        factory.setValidating(false);

        return factory.newDocumentBuilder().parse(inputSource);
    }

    /**
     * Liefert das {@link Document} aus dem Stream.<br>
     */
    public static Document getDocument(final InputStream inputStream) throws Exception {
        return getDocument(new InputSource(inputStream));
    }

    /**
     * Liefert das {@link Document} aus dem String.
     */
    public static Document getDocument(final String string) throws Exception {
        return getDocument(new InputSource(new StringReader(string)));
    }

    private XmlUtils() {
        super();
    }
}
