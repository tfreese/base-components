package de.freese.base.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

/**
 * @author Thomas Freese
 */
public final class XmlUtils {
    private static final Map<String, JAXBContext> JAXB_CONTEXT_CACHE = new HashMap<>();
    private static final Map<Class<?>, Schema> SCHEMA_CACHE = new HashMap<>();

    private static final class XmlValidationErrorHandler implements ErrorHandler {
        private static final Logger LOGGER = LoggerFactory.getLogger(XmlValidationErrorHandler.class);

        private final Set<String> messages = new HashSet<>();

        @Override
        public void error(final SAXParseException exception) {
            messages.add(exception.toString());
        }

        @Override
        public void fatalError(final SAXParseException exception) {
            messages.add(exception.toString());
        }

        public void logMessages() {
            messages.forEach(LOGGER::error);
        }

        @Override
        public void warning(final SAXParseException exception) {
            messages.add(exception.toString());
        }
    }

    public static Document getDocument(final InputSource inputSource) throws Exception {
        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

        // Protect against to XXE attacks.
        documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
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
    @SuppressWarnings("java:S2755")
    public static Schema getSchema(final Class<?> clazz) throws Exception {
        Schema schema = SCHEMA_CACHE.get(clazz);

        if (schema != null) {
            return schema;
        }

        // Determine jar file based on types class file
        // final String typeClassFilePath = clazz.getResource("/" + clazz.getName().replace('.', '/') + ".class").getFile();
        // final URI jarFileURI = URI.create(typeClassFilePath.substring(0, typeClassFilePath.indexOf(".jar!") + 4));

        final URI jarFileURI = clazz.getProtectionDomain().getCodeSource().getLocation().toURI();

        final Source[] schemas;

        // Search schema files in the META-INF folder of the Jar.
        try (FileSystem fileSystem = FileSystems.newFileSystem(Paths.get(jarFileURI), clazz.getClassLoader());
             Stream<Path> stream = Files.walk(fileSystem.getPath("/META-INF"), 1)) {
            schemas = stream.filter(path -> path.toString().endsWith(".xsd")).map(XmlUtils::toSchema).toArray(Source[]::new);
        }

        final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "file"); // file, http
        schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");

        schema = schemaFactory.newSchema(schemas);

        SCHEMA_CACHE.put(clazz, schema);

        return schema;
    }

    public static void marshall(final Object value, final OutputStream outputStream) throws Exception {
        final Schema schema = getSchema(value.getClass());
        final Marshaller marshaller = getMarshaller(Set.of(value.getClass()));
        marshaller.setSchema(schema);

        marshaller.marshal(value, outputStream);
        outputStream.flush();
    }

    @SuppressWarnings("unchecked")
    public static <T> T unmarshall(final InputStream inputStream, final Class<T> type) throws Exception {
        final Schema schema = getSchema(type);
        final Unmarshaller unmarshaller = getUnmarshaller(Set.of(type));
        unmarshaller.setSchema(schema);

        return (T) unmarshaller.unmarshal(inputStream);
    }

    public static void validate(final Schema schema, final InputStream inputStream) throws Exception {
        final Validator validator = schema.newValidator();

        final XmlValidationErrorHandler errorHandler = new XmlValidationErrorHandler();
        validator.setErrorHandler(errorHandler);

        try {
            validator.validate(new StreamSource(inputStream));
        }
        finally {
            errorHandler.logMessages();
        }
    }

    private static JAXBContext getJAXBContext(final Set<Class<?>> clazzes) throws JAXBException {
        final String key = clazzes.stream().map(Class::getSimpleName).collect(Collectors.joining("_"));

        JAXBContext jaxbContext = JAXB_CONTEXT_CACHE.get(key);

        if (jaxbContext != null) {
            return jaxbContext;
        }

        jaxbContext = JAXBContext.newInstance(clazzes.toArray(new Class<?>[0]));

        JAXB_CONTEXT_CACHE.put(key, jaxbContext);

        return jaxbContext;
    }

    private static Marshaller getMarshaller(final Set<Class<?>> clazzes) throws JAXBException {
        final Marshaller marshaller = getJAXBContext(clazzes).createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_ENCODING, StandardCharsets.UTF_8.name());
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        // marshaller.setProperty(Marshaller.JAXB_FRAGMENT, !asDocument);

        return marshaller;
    }

    private static Unmarshaller getUnmarshaller(final Set<Class<?>> clazzes) throws JAXBException {
        return getJAXBContext(clazzes).createUnmarshaller();
    }

    private static Source toSchema(final Path path) {
        try {
            final URI uri = path.toUri();

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
