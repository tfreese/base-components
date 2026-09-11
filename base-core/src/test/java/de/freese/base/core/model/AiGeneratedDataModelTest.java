package de.freese.base.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.dataformat.xml.XmlMapper;

class AiGeneratedDataModelTest {

    private static AiGeneratedDataModel createModel() {
        final AiGeneratedDataModel model = new AiGeneratedDataModel();
        final AiGeneratedDataModel.Column columnId = model.addColumn("id", Long.class);
        model.addColumn("name", String.class);

        model.column(0).metadata().put("hint", "serializable");

        final AiGeneratedDataModel.Row row0 = model.addRow();
        row0.setValue(columnId.name(), 1001L);
        row0.setValue(1, "Alice");
        row0.cellMetadata(1).put("quality", "verified");

        final AiGeneratedDataModel.Row row1 = model.addRow();
        row1.setValue(columnId.name(), 2002L);
        row1.setValue(1, "Bob");

        return model;
    }

    @Test
    void shouldExposeSimpleStructure() {
        final AiGeneratedDataModel model = createModel();

        assertEquals(2, model.columnCount());
        assertEquals(2, model.rowCount());
        assertEquals("id", model.columnNames().getFirst());
        assertEquals("Row[index=0]", model.row(0).toString());
    }

    @Test
    void shouldRoundTripAsJsonAndKeepColumnRowAccess() throws Exception {
        final AiGeneratedDataModel model = createModel();
        final JsonMapper jsonMapper = JsonMapper.builder()
                // Don't serialize empty values.
                .changeDefaultPropertyInclusion(value -> value.withValueInclusion(JsonInclude.Include.NON_EMPTY))
                .enable(SerializationFeature.INDENT_OUTPUT)
                .build();

        final String json = jsonMapper.writeValueAsString(model);
        final AiGeneratedDataModel copy = jsonMapper.readValue(json, AiGeneratedDataModel.class);

        assertEquals(2, copy.columnCount());
        assertEquals(2, copy.rowCount());
        assertEquals("Alice", copy.row(0).getValue(1));
        assertEquals(2002, copy.column(0).getValue(1));
        assertEquals("serializable", copy.column("id").metadata().getRequired("hint"));
        assertEquals("verified", copy.row(0).cellMetadata(1).getRequired("quality"));
    }

    @Test
    void shouldRoundTripAsXmlAndKeepMetadataStructure() throws Exception {
        final AiGeneratedDataModel model = createModel();
        final XmlMapper xmlMapper = XmlMapper.builder()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .build();

        final String xml = xmlMapper.writeValueAsString(model);
        final AiGeneratedDataModel copy = xmlMapper.readValue(xml, AiGeneratedDataModel.class);

        assertEquals("java.lang.Long", copy.column("id").typeName());
        assertEquals("Bob", copy.row(1).getValue(1));
        assertEquals("verified", copy.row(0).cellMetadata(1).getRequired("quality"));
        assertTrue(xml.contains("<columns>"));
        assertTrue(xml.contains("<rows>"));
        assertNotNull(copy.toString());
    }
}
