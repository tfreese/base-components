package de.freese.base.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * Toolkit zum Arbeiten mit XML.
 *
 * @author Thomas Freese
 */
public final class XMLUtils
{
    /**
     * Liefert das {@link Document} aus dem Byte-Array.
     *
     * @param bytes byte[]
     *
     * @return {@link Document}
     *
     * @throws Exception Falls was schief geht.
     */
    public static Document getDocument(final byte[] bytes) throws Exception
    {
        return getDocument(new ByteArrayInputStream(bytes));
    }

    /**
     * Liefert das {@link Document} aus der {@link InputSource}.
     *
     * @param inputSource {@link InputSource}
     *
     * @return {@link Document}
     *
     * @throws Exception Falls was schief geht.
     */
    public static Document getDocument(final InputSource inputSource) throws Exception
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        // Protect against to XXE attacks.
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, ""); // Compliant
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, ""); // Compliant

        factory.setValidating(false);

        Document document = factory.newDocumentBuilder().parse(inputSource);

        return document;
    }

    /**
     * Liefert das {@link Document} aus dem Stream.<br>
     *
     * @param inputStream {@link InputStream}
     *
     * @return {@link Document}
     *
     * @throws Exception Falls was schief geht.
     */
    public static Document getDocument(final InputStream inputStream) throws Exception
    {
        return getDocument(new InputSource(inputStream));
    }

    /**
     * Liefert das {@link Document} aus dem String.
     *
     * @param string String
     *
     * @return {@link Document}
     *
     * @throws Exception Falls was schief geht.
     */
    public static Document getDocument(final String string) throws Exception
    {
        return getDocument(new InputSource(new StringReader(string)));
    }

    /**
     * Erstellt ein neues {@link XMLUtils} Object.
     */
    private XMLUtils()
    {
        super();
    }
}
