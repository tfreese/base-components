package de.freese.base.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.net.URI;
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
import javax.xml.validation.Validator;

import jakarta.xml.bind.Unmarshaller;

import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author Thomas Freese
 */
public final class XmlUtils {
    public static Document getDocument(final byte[] bytes) throws Exception {
        return getDocument(new ByteArrayInputStream(bytes));
    }

    public static Document getDocument(final InputSource inputSource) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

        // Protect against to XXE attacks.
        documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

        documentBuilderFactory.setValidating(false);

        return documentBuilderFactory.newDocumentBuilder().parse(inputSource);
    }

    public static Document getDocument(final InputStream inputStream) throws Exception {
        return getDocument(new InputSource(inputStream));
    }

    public static Document getDocument(final String string) throws Exception {
        return getDocument(new InputSource(new StringReader(string)));
    }

    /**
     * Getting all Schema-Files from the Jar of the Type.
     */
    public static Schema getSchema(final Class<?> type) throws Exception {
        Schema schema;

        //        schema = schemaCache.get(type);
        //
        //        if (schema != null) {
        //            return schema;
        //        }

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
        //            schemaCache.put(type, schema);

        return schema;
    }

    public static void validate(Unmarshaller unmarshaller, Schema schema, String xml, boolean validate, boolean logValidationErrors) throws Exception {
        if (!validate) {
            return;
        }

        if (logValidationErrors) {
            Validator validator = schema.newValidator();

            ErrorHandler errorHandler = new ErrorHandler() {
                @Override
                public void error(final SAXParseException exception) throws SAXException {
                    // Empty
                }

                @Override
                public void fatalError(final SAXParseException exception) throws SAXException {
                    // Empty
                }

                @Override
                public void warning(final SAXParseException exception) throws SAXException {
                    // Empty
                }
            };
            validator.setErrorHandler(errorHandler);

            try (Reader readerValidation = new StringReader(xml)) {
                validator.validate(new StreamSource(readerValidation));
            }
        }
        else {
            unmarshaller.setSchema(schema);
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
