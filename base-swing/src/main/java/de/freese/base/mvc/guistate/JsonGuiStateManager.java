// Created: 26.01.2018
package de.freese.base.mvc.guistate;

import java.io.InputStream;
import java.io.OutputStream;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.freese.base.mvc.storage.LocalStorage;
import de.freese.base.swing.state.GuiState;
import de.freese.base.swing.state.GuiStates;

/**
 * @author Thomas Freese
 */
public class JsonGuiStateManager extends AbstractGuiStateManager {
    private final ObjectMapper mapper;

    public JsonGuiStateManager(final LocalStorage localStorage, final GuiStates guiStates) {
        super(localStorage, guiStates, "json");

        mapper = new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .enable(DeserializationFeature.UNWRAP_ROOT_VALUE)
                .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                .enable(SerializationFeature.INDENT_OUTPUT)
                .enable(SerializationFeature.WRAP_ROOT_VALUE)
                // .setVisibility(jsonMapper.getVisibilityChecker().with(Visibility.NONE));
                .setVisibility(PropertyAccessor.FIELD, Visibility.ANY)
        // .setVisibility(PropertyAccessor.SETTER, Visibility.PUBLIC_ONLY)
        // .setVisibility(PropertyAccessor.GETTER, Visibility.PUBLIC_ONLY)
        // .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        // .configure(SerializationFeature.INDENT_OUTPUT, true)
        ;

        // SimpleModule module = new SimpleModule();
        // module.registerSubtypes(getGuiStateClasses());
        // mapper.registerModule(module);
    }

    @Override
    protected GuiState load(final GuiState guiState, final InputStream inputStream) throws Exception {
        return mapper.readValue(inputStream, guiState.getClass());
    }

    @Override
    protected void save(final GuiState guiState, final OutputStream outputStream) throws Exception {
        mapper.writer().writeValue(outputStream, guiState);
    }
}
