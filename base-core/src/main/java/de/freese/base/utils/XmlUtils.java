package de.freese.base.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Thomas Freese
 */
public final class XmlUtils {
    public static Document getDocument(final byte[] bytes) throws Exception {
        return getDocument(new ByteArrayInputStream(bytes));
    }

    public static Document getDocument(final InputSource inputSource) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        // Protect against to XXE attacks.
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, ""); // Compliant
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, ""); // Compliant

        factory.setValidating(false);

        return factory.newDocumentBuilder().parse(inputSource);
    }

    public static Document getDocument(final InputStream inputStream) throws Exception {
        return getDocument(new InputSource(inputStream));
    }

    public static Document getDocument(final String string) throws Exception {
        return getDocument(new InputSource(new StringReader(string)));
    }

    public static Schema getSchema(final Class<?> type) {
        Schema schema;

        //        schema = schemaCache.get(type);
        //
        //        if (schema != null) {
        //            return schema;
        //        }

        try {
            // Determine jar file based on types class file
            String typeClassFilePath = type.getResource("/" + type.getName().replace('.', '/') + ".class").getFile();
            URL jarFileURL = URI.create(typeClassFilePath.substring(0, typeClassFilePath.indexOf(".jar!") + 4)).toURL();

            // Search schema files in jars meta-inf folder
            Source[] schemas;

            try (FileSystem fileSystem = FileSystems.newFileSystem(Paths.get(jarFileURL.toURI()), type.getClassLoader());
                 Stream<Path> stream = Files.walk(fileSystem.getPath("/META-INF"), 1)) {
                schemas = stream.filter(path -> path.toString().endsWith(".xsd")).map(XmlUtils::toSchema).toArray(Source[]::new);
            }

            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");

            schema = schemaFactory.newSchema(schemas);
            //            schemaCache.put(type, schema);            schemaCache.put(type, schema);

            return schema;
        }
        catch (SAXException | IOException | URISyntaxException ex) {
            throw new RuntimeException("Could not parse schemas for type " + type, ex);
        }
    }

    private static Source toSchema(final Path path) {
        try {
            URI uri = path.toUri();

            return new StreamSource(uri.toURL().openStream(), uri.toString());
        }
        catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private XmlUtils() {
        super();
    }
}
