/**
 * Created: 15.06.2012
 */

package de.freese.base.core.xml;

import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * Decorator für einen {@link XMLStreamWriter} für "PrettyPrint"-Features.<br>
 * Relevante Methoden für das PrettyPrinting: <code>writeStartElement,writeEndElement,writeEmptyElement</code><br>
 * <br>
 * Defaultkonfiguration:
 * <ul>
 * <li>Encoding = UTF8</li>
 * <li>LineSeparator = System.getProperty("line.separator")</li>
 * <li>indentAmount = 4</li>
 * <li>indentChar = ' '</li>
 * <ul>
 * <br>
 *
 * @author Thomas Freese
 */
public class PrettyPrintXMLStreamWriter implements XMLStreamWriter
{
    /**
     *
     */
    private final XMLStreamWriter delegate;

    /**
     *
     */
    private int depth;

    /**
     *
     */
    private int indentAmount = 4;

    /**
     *
     */
    private char indentChar = ' ';

    /**
     *
     */
    private String lineSeparator;

    /**
     *
     */
    private Map<Integer, Boolean> nodeStates = new HashMap<>();

    /**
     * Erstellt ein neues {@link PrettyPrintXMLStreamWriter} Object.<br>
     *
     * @param outputStream {@link OutputStream}
     * @throws FactoryConfigurationError if an instance of this factory cannot be loaded
     * @throws XMLStreamException Falls was schief geht.
     */
    public PrettyPrintXMLStreamWriter(final OutputStream outputStream) throws XMLStreamException, FactoryConfigurationError
    {
        this(outputStream, "UTF8");
    }

    /**
     * Erstellt ein neues {@link PrettyPrintXMLStreamWriter} Object.
     *
     * @param outputStream {@link OutputStream}
     * @param encoding String; UTF8, ISO-8859-1...
     * @throws FactoryConfigurationError if an instance of this factory cannot be loaded
     * @throws XMLStreamException Falls was schief geht.
     */
    public PrettyPrintXMLStreamWriter(final OutputStream outputStream, final String encoding) throws XMLStreamException, FactoryConfigurationError
    {
        this(XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream, encoding));
    }

    /**
     * Erstellt ein neues {@link PrettyPrintXMLStreamWriter} Object.
     *
     * @param delegate {@link XMLStreamWriter}
     */
    public PrettyPrintXMLStreamWriter(final XMLStreamWriter delegate)
    {
        super();

        this.delegate = delegate;

        this.lineSeparator = AccessController.doPrivileged((PrivilegedAction<String>) () -> System.getProperty("line.separator"));
    }

    /**
     * @see javax.xml.stream.XMLStreamWriter#close()
     */
    @Override
    public void close() throws XMLStreamException
    {
        getDelegate().close();
    }

    /**
     * @see javax.xml.stream.XMLStreamWriter#flush()
     */
    @Override
    public void flush() throws XMLStreamException
    {
        getDelegate().flush();
    }

    /**
     * @return {@link XMLStreamWriter}
     */
    protected XMLStreamWriter getDelegate()
    {
        return this.delegate;
    }

    /**
     * @return int
     */
    protected int getDepth()
    {
        return this.depth;
    }

    /**
     * @return int
     */
    public int getIndentAmount()
    {
        return this.indentAmount;
    }

    /**
     * @return char
     */
    public char getIndentChar()
    {
        return this.indentChar;
    }

    /**
     * @return String
     */
    public String getLineSeparator()
    {
        return this.lineSeparator;
    }

    /**
     * @see javax.xml.stream.XMLStreamWriter#getNamespaceContext()
     */
    @Override
    public NamespaceContext getNamespaceContext()
    {
        return getDelegate().getNamespaceContext();
    }

    /**
     * @return {@link Map}<Integer,Boolean>
     */
    protected Map<Integer, Boolean> getNodeStates()
    {
        return this.nodeStates;
    }

    /**
     * @see javax.xml.stream.XMLStreamWriter#getPrefix(java.lang.String)
     */
    @Override
    public String getPrefix(final String uri) throws XMLStreamException
    {
        return getDelegate().getPrefix(uri);
    }

    /**
     * @see javax.xml.stream.XMLStreamWriter#getProperty(java.lang.String)
     */
    @Override
    public Object getProperty(final String name)
    {
        return getDelegate().getProperty(name);
    }

    /**
     * @throws XMLStreamException Falls was schief geht.
     */
    protected void handleWriteEmptyElement() throws XMLStreamException
    {
        if (this.depth > 0)
        {
            getNodeStates().put(getDepth() - 1, true);
        }

        getDelegate().writeCharacters(getLineSeparator());
        getDelegate().writeCharacters(repeat(getDepth(), getIndentAmount(), getIndentChar()));
    }

    /**
     * @throws XMLStreamException Falls was schief geht.
     */
    protected void handleWriteEndElement() throws XMLStreamException
    {
        this.depth--;

        if (getNodeStates().get(getDepth()))
        {
            getDelegate().writeCharacters(getLineSeparator());
            getDelegate().writeCharacters(repeat(getDepth(), getIndentAmount(), getIndentChar()));
        }
    }

    /**
     * @throws XMLStreamException Falls was schief geht.
     */
    protected void handleWriteStartElement() throws XMLStreamException
    {
        if (this.depth > 0)
        {
            getNodeStates().put(getDepth() - 1, true);
        }

        getNodeStates().put(getDepth(), false);

        getDelegate().writeCharacters(getLineSeparator());
        getDelegate().writeCharacters(repeat(getDepth(), getIndentAmount(), getIndentChar()));

        this.depth++;
    }

    /**
     * @param depth int
     * @param amount int
     * @param filler char
     * @return String
     */
    protected String repeat(final int depth, final int amount, final char filler)
    {
        if (depth == 0)
        {
            return null;
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < (depth * amount); i++)
        {
            sb.append(filler);
        }

        return sb.toString();
    }

    /**
     * @see javax.xml.stream.XMLStreamWriter#setDefaultNamespace(java.lang.String)
     */
    @Override
    public void setDefaultNamespace(final String uri) throws XMLStreamException
    {
        getDelegate().setDefaultNamespace(uri);
    }

    /**
     * @param depth int
     */
    protected void setDepth(final int depth)
    {
        this.depth = depth;
    }

    /**
     * @param indentAmount int
     */
    public void setIndentAmount(final int indentAmount)
    {
        this.indentAmount = indentAmount;
    }

    /**
     * @param indentChar char
     */
    public void setIndentChar(final char indentChar)
    {
        this.indentChar = indentChar;
    }

    /**
     * @param lineSeparator String
     */
    public void setLineSeparator(final String lineSeparator)
    {
        this.lineSeparator = lineSeparator;
    }

    /**
     * @see javax.xml.stream.XMLStreamWriter#setNamespaceContext(javax.xml.namespace.NamespaceContext)
     */
    @Override
    public void setNamespaceContext(final NamespaceContext context) throws XMLStreamException
    {
        getDelegate().setNamespaceContext(context);
    }

    /**
     * @see javax.xml.stream.XMLStreamWriter#setPrefix(java.lang.String, java.lang.String)
     */
    @Override
    public void setPrefix(final String prefix, final String uri) throws XMLStreamException
    {
        getDelegate().setPrefix(prefix, uri);
    }

    /**
     * @see javax.xml.stream.XMLStreamWriter#writeAttribute(java.lang.String, java.lang.String)
     */
    @Override
    public void writeAttribute(final String localName, final String value) throws XMLStreamException
    {
        getDelegate().writeAttribute(localName, value);
    }

    /**
     * @see javax.xml.stream.XMLStreamWriter#writeAttribute(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void writeAttribute(final String namespaceURI, final String localName, final String value) throws XMLStreamException
    {
        getDelegate().writeAttribute(namespaceURI, localName, value);
    }

    /**
     * @see javax.xml.stream.XMLStreamWriter#writeAttribute(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void writeAttribute(final String prefix, final String namespaceURI, final String localName, final String value) throws XMLStreamException
    {
        getDelegate().writeAttribute(prefix, namespaceURI, localName, value);
    }

    /**
     * @see javax.xml.stream.XMLStreamWriter#writeCData(java.lang.String)
     */
    @Override
    public void writeCData(final String data) throws XMLStreamException
    {
        getDelegate().writeCData(data);
    }

    /**
     * @see javax.xml.stream.XMLStreamWriter#writeCharacters(char[], int, int)
     */
    @Override
    public void writeCharacters(final char[] text, final int start, final int len) throws XMLStreamException
    {
        getDelegate().writeCharacters(text, start, len);
    }

    /**
     * @see javax.xml.stream.XMLStreamWriter#writeCharacters(java.lang.String)
     */
    @Override
    public void writeCharacters(final String text) throws XMLStreamException
    {
        getDelegate().writeCharacters(text);
    }

    /**
     * @see javax.xml.stream.XMLStreamWriter#writeComment(java.lang.String)
     */
    @Override
    public void writeComment(final String data) throws XMLStreamException
    {
        getDelegate().writeComment(data);
    }

    /**
     * @see javax.xml.stream.XMLStreamWriter#writeDefaultNamespace(java.lang.String)
     */
    @Override
    public void writeDefaultNamespace(final String namespaceURI) throws XMLStreamException
    {
        getDelegate().writeDefaultNamespace(namespaceURI);
    }

    /**
     * @see javax.xml.stream.XMLStreamWriter#writeDTD(java.lang.String)
     */
    @Override
    public void writeDTD(final String dtd) throws XMLStreamException
    {
        getDelegate().writeDTD(dtd);
    }

    /**
     * @see javax.xml.stream.XMLStreamWriter#writeEmptyElement(java.lang.String)
     */
    @Override
    public void writeEmptyElement(final String localName) throws XMLStreamException
    {
        handleWriteEmptyElement();
        getDelegate().writeEmptyElement(localName);
    }

    /**
     * @see javax.xml.stream.XMLStreamWriter#writeEmptyElement(java.lang.String, java.lang.String)
     */
    @Override
    public void writeEmptyElement(final String namespaceURI, final String localName) throws XMLStreamException
    {
        handleWriteEmptyElement();
        getDelegate().writeEmptyElement(namespaceURI, localName);
    }

    /**
     * @see javax.xml.stream.XMLStreamWriter#writeEmptyElement(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void writeEmptyElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException
    {
        handleWriteEmptyElement();
        getDelegate().writeEmptyElement(prefix, localName, namespaceURI);
    }

    /**
     * @see javax.xml.stream.XMLStreamWriter#writeEndDocument()
     */
    @Override
    public void writeEndDocument() throws XMLStreamException
    {
        getDelegate().writeEndDocument();
    }

    /**
     * @see javax.xml.stream.XMLStreamWriter#writeEndElement()
     */
    @Override
    public void writeEndElement() throws XMLStreamException
    {
        handleWriteEndElement();
        getDelegate().writeEndElement();
    }

    /**
     * @see javax.xml.stream.XMLStreamWriter#writeEntityRef(java.lang.String)
     */
    @Override
    public void writeEntityRef(final String name) throws XMLStreamException
    {
        getDelegate().writeEntityRef(name);
    }

    /**
     * @see javax.xml.stream.XMLStreamWriter#writeNamespace(java.lang.String, java.lang.String)
     */
    @Override
    public void writeNamespace(final String prefix, final String namespaceURI) throws XMLStreamException
    {
        getDelegate().writeNamespace(prefix, namespaceURI);
    }

    /**
     * @see javax.xml.stream.XMLStreamWriter#writeProcessingInstruction(java.lang.String)
     */
    @Override
    public void writeProcessingInstruction(final String target) throws XMLStreamException
    {
        getDelegate().writeProcessingInstruction(target);
    }

    /**
     * @see javax.xml.stream.XMLStreamWriter#writeProcessingInstruction(java.lang.String, java.lang.String)
     */
    @Override
    public void writeProcessingInstruction(final String target, final String data) throws XMLStreamException
    {
        getDelegate().writeProcessingInstruction(target, data);
    }

    /**
     * @see javax.xml.stream.XMLStreamWriter#writeStartDocument()
     */
    @Override
    public void writeStartDocument() throws XMLStreamException
    {
        getDelegate().writeStartDocument();
    }

    /**
     * @see javax.xml.stream.XMLStreamWriter#writeStartDocument(java.lang.String)
     */
    @Override
    public void writeStartDocument(final String version) throws XMLStreamException
    {
        getDelegate().writeStartDocument(version);
    }

    /**
     * @see javax.xml.stream.XMLStreamWriter#writeStartDocument(java.lang.String, java.lang.String)
     */
    @Override
    public void writeStartDocument(final String encoding, final String version) throws XMLStreamException
    {
        getDelegate().writeStartDocument(encoding, version);
    }

    /**
     * @see javax.xml.stream.XMLStreamWriter#writeStartElement(java.lang.String)
     */
    @Override
    public void writeStartElement(final String localName) throws XMLStreamException
    {
        handleWriteStartElement();
        getDelegate().writeStartElement(localName);
    }

    /**
     * @see javax.xml.stream.XMLStreamWriter#writeStartElement(java.lang.String, java.lang.String)
     */
    @Override
    public void writeStartElement(final String namespaceURI, final String localName) throws XMLStreamException
    {
        handleWriteStartElement();
        getDelegate().writeStartElement(namespaceURI, localName);
    }

    /**
     * @see javax.xml.stream.XMLStreamWriter#writeStartElement(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void writeStartElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException
    {
        handleWriteStartElement();
        getDelegate().writeStartElement(prefix, localName, namespaceURI);
    }
}
