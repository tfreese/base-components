package de.freese.base.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.StreamWriteConstraints;
import tools.jackson.core.json.JsonFactory;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.dataformat.xml.XmlFactory;
import tools.jackson.dataformat.xml.XmlMapper;
import tools.jackson.datatype.jsonp.JSONPModule;

import java.util.TimeZone;

/**
 * See META-INF/services/jakarta.json.spi.JsonProvider<br/>
 * #org.glassfish.json.JsonProviderImpl<br/>
 * org.eclipse.parsson.JsonProviderImpl<br/>
 * <p/>
 * implementation("jakarta.json:jakarta.json-api")<br/>
 * runtimeOnly("org.eclipse.parsson:jakarta.json") // jakarta.json-api Impl. See META-INF/services/jakarta.json.spi.JsonProvider<br/>
 * // runtimeOnly("org.glassfish:jakarta.json") // jakarta.json-api Impl.<br/>
 *
 * @author Thomas Freese
 * @since 06.08.26
 */
@SuppressWarnings({"java:S2143"})
public final class JacksonMapperConfig {
    private static final JsonMapper JSON_MAPPER = createJsonMapper();
    private static final XmlMapper XML_MAPPER = createXmlMapper();

    public static JsonMapper createJsonMapper() {
        final StreamReadConstraints streamReadConstraints = StreamReadConstraints.builder()
                .maxDocumentLength(-1L)
                .maxNumberLength(1_000)
                .maxNestingDepth(1_000)
                .maxStringLength(20_000_000)
                .maxTokenCount(-1L)
                .build();

        final StreamWriteConstraints streamWriteConstraints = StreamWriteConstraints.builder()
                .maxNestingDepth(1_000)
                .build();

        final JsonFactory jsonFactory = JsonFactory.builder()
                .streamReadConstraints(streamReadConstraints)
                .streamWriteConstraints(streamWriteConstraints)
                .build();

        // return JsonMapper.builder()
        return JsonMapper.builder(jsonFactory)
                .changeDefaultPropertyInclusion(value -> value.withValueInclusion(JsonInclude.Include.NON_EMPTY))
                // .defaultPropertyInclusion(JsonInclude.Value.construct(JsonInclude.Include.NON_EMPTY, JsonInclude.Include.NON_EMPTY))
                // .serializationInclusion(JsonInclude.Include.NON_EMPTY)
                // .addModule(new JavaTimeModule()) //  Already included in Jackson 3.x.
                .addModule(new JSONPModule()) // Direct Conversion from Jakarta in Jackson JSON Objects.
                .defaultTimeZone(TimeZone.getDefault())
                .enable(SerializationFeature.INDENT_OUTPUT)
                .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                // .findAndAddModules()
                .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .build();
    }

    public static XmlMapper createXmlMapper() {
        final StreamReadConstraints streamReadConstraints = StreamReadConstraints.builder()
                .maxDocumentLength(-1L)
                .maxNumberLength(1_000)
                .maxNestingDepth(1_000)
                .maxStringLength(20_000_000)
                .maxTokenCount(-1L)
                .build();

        final StreamWriteConstraints streamWriteConstraints = StreamWriteConstraints.builder()
                .maxNestingDepth(1_000)
                .build();

        final XmlFactory xmlFactory = XmlFactory.builder()
                .streamReadConstraints(streamReadConstraints)
                .streamWriteConstraints(streamWriteConstraints)
                .build();

        // return XmlMapper.builder()
        return XmlMapper.builder(xmlFactory)
                .changeDefaultPropertyInclusion(value -> value.withValueInclusion(JsonInclude.Include.NON_EMPTY))
                // .addModule(new JavaTimeModule()) //  Already included in Jackson 3.x.
                .defaultTimeZone(TimeZone.getDefault())
                .enable(SerializationFeature.INDENT_OUTPUT)
                .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                // .findAndAddModules()
                .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .build();
    }

    public static JsonMapper getJsonMapper() {
        return JSON_MAPPER;
    }

    public static XmlMapper getXmlMapper() {
        return XML_MAPPER;
    }

    private JacksonMapperConfig() {
        super();
    }
}
