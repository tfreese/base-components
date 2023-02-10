// Created: 15.06.2012
package de.freese.base.core.xml;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.stream.XMLStreamWriter;

import org.junit.jupiter.api.Test;

/**
 * @author Thomas Freese
 */
class TestStaxWriter {
    @Test
    void testStaxWriter() throws Exception {
        String encoding = "ISO-8859-1";

        // System.setProperty("javax.xml.stream.XMLOutputFactory", value) ;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (OutputStream os = baos) {
            // OutputStream os1 = new ByteArrayOutputStream();

            // XMLOutputFactory factory = XMLOutputFactory.newInstance();
            // factory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, true);
            // XMLStreamWriter writer = factory.createXMLStreamWriter(os, encoding);
            XMLStreamWriter writer = new PrettyPrintXMLStreamWriter(os, encoding);

            // PrettyPrint per Proxy
            // StaxPrettyPrintHandler handler = new StaxPrettyPrintHandler(writer);
            // writer =
            // (XMLStreamWriter) Proxy.newProxyInstance(XMLStreamWriter.class.getClassLoader(),
            // new Class[]
            // {
            // XMLStreamWriter.class
            // }, handler);

            writer.writeStartDocument(encoding, "1.0");

            // Write the root element "person" with a single attribute "gender"
            writer.writeStartElement("person");
            writer.writeNamespace("one", "http://namespaceOne");
            writer.writeAttribute("gender", "f");
            // writer.writeCharacters("\n");

            // Write the "name" element with some content and two attributes
            // writer.writeCharacters(" ");
            writer.writeStartElement("one", "name", "http://namespaceOne");
            writer.writeAttribute("hair", "pigtails");
            writer.writeAttribute("freckles", "yes");

            writer.writeStartElement("firstname");
            writer.writeCharacters("Pippi Longstocking");
            writer.writeEndElement();

            writer.writeEmptyElement("empty");

            // End the "name" element
            writer.writeEndElement();
            // writer.writeCharacters("\n");

            // End the "person" element
            writer.writeEndElement();

            writer.flush();
            writer.close();
        }

        // Transformation, PrettyPrint
        // StreamSource xmlInput = new StreamSource(new ByteArrayInputStream(bytes));
        //
        // StreamResult xmlOutput = new StreamResult(baos);
        //
        // Transformer transformer = TransformerFactory.newInstance().newTransformer();
        // transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        // transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        // transformer.transform(xmlInput, xmlOutput);
        //
        // xmlInput.getInputStream().close();
        // xmlOutput.getOutputStream().close();

        String xml = baos.toString(StandardCharsets.UTF_8);
        System.out.println(xml);

        assertTrue(true);
    }
}
