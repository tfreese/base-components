package de.freese.base.utils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.StreamWriteConstraints;
import tools.jackson.core.json.JsonFactory;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.cfg.MapperBuilder;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.dataformat.xml.XmlFactory;
import tools.jackson.dataformat.xml.XmlMapper;
import tools.jackson.datatype.jsonp.JSONPModule;

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

    private static final class EmptyValueFilter {
        @Override
        public boolean equals(final Object obj) {
            // 1. Wenn die Property null ist > ausschließen (true).
            if (obj == null) {
                return true;
            }

            // 2. Wenn es ein String ist und nach dem trim() leer ist > ausschließen (true).
            if (obj instanceof final String stringValue) {
                return stringValue.isBlank();
            }

            // Optional 3: Wenn du auch leere Listen/Maps global ausschließen willst (wie bei NON_EMPTY).
            if (obj instanceof final Collection<?> collection) {
                return collection.isEmpty();
            }

            if (obj instanceof final Map<?, ?> map) {
                return map.isEmpty();
            }

            if (obj.getClass().isArray()) {
                return Array.getLength(obj) == 0;
            }

            if (obj instanceof final Optional<?> optional) {
                return optional.isEmpty();
            }

            // Alle anderen Datentypen (Integers, Booleans etc.) standardmäßig behalten.
            return false;
        }
    }

    public static JsonMapper createJsonMapper() {
        final JsonFactory jsonFactory = JsonFactory.builder()
                .streamReadConstraints(createStreamReadConstraints())
                .streamWriteConstraints(createStreamWriteConstraints())
                .build();

        return configureBuilder(JsonMapper.builder(jsonFactory)).build();
    }

    public static XmlMapper createXmlMapper() {
        final XmlFactory xmlFactory = XmlFactory.builder()
                .streamReadConstraints(createStreamReadConstraints())
                .streamWriteConstraints(createStreamWriteConstraints())
                .build();

        return configureBuilder(XmlMapper.builder(xmlFactory)).build();
    }

    public static JsonMapper getJsonMapper() {
        return JSON_MAPPER;
    }

    public static XmlMapper getXmlMapper() {
        return XML_MAPPER;
    }

    @SuppressWarnings("unchecked")
    private static <B extends MapperBuilder<?, ?>> B configureBuilder(final B builder) {
        return (B) builder
                // Don't serialize empty values.
                .changeDefaultPropertyInclusion(value -> value.withValueInclusion(JsonInclude.Include.NON_EMPTY))
                // .changeDefaultPropertyInclusion(include -> include
                //         .withValueInclusion(JsonInclude.Include.CUSTOM)
                //         .withValueFilter(EmptyValueFilter.class)
                // )
                // .withConfigOverride(String.class, co -> co.setInclude(
                //         JsonInclude.Value.construct(
                //                 JsonInclude.Include.CUSTOM,
                //                 JsonInclude.Include.USE_DEFAULTS,
                //                 EmptyValueFilter.class,
                //                 null
                //         )
                // ))
                // .addModule(new JavaTimeModule()) //  Already included in Jackson 3.x.
                .addModule(new JSONPModule()) // Direct Conversion from Jakarta in Jackson JSON Objects.
                .defaultTimeZone(TimeZone.getDefault())
                .enable(SerializationFeature.INDENT_OUTPUT)
                .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                // .findAndAddModules()
                .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    private static StreamReadConstraints createStreamReadConstraints() {
        return StreamReadConstraints.builder()
                .maxDocumentLength(-1L)
                .maxNumberLength(1_000)
                .maxNestingDepth(1_000)
                .maxStringLength(20_000_000)
                .maxTokenCount(-1L)
                .build();
    }

    private static StreamWriteConstraints createStreamWriteConstraints() {
        return StreamWriteConstraints.builder()
                .maxNestingDepth(1_000)
                .build();
    }

    private JacksonMapperConfig() {
        super();
    }
}
