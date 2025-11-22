// Created: 26.01.2018
package de.freese.base.mvc.guistate;

import java.io.InputStream;
import java.io.OutputStream;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

import de.freese.base.mvc.storage.LocalStorage;
import de.freese.base.swing.state.GuiState;
import de.freese.base.swing.state.GuiStates;

/**
 * @author Thomas Freese
 */
public class JsonGuiStateManager extends AbstractGuiStateManager {
    private final JsonMapper jsonMapper;

    public JsonGuiStateManager(final LocalStorage localStorage, final GuiStates guiStates) {
        super(localStorage, guiStates, "json");

        jsonMapper = JsonMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .enable(DeserializationFeature.UNWRAP_ROOT_VALUE)
                .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                .enable(SerializationFeature.INDENT_OUTPUT)
                .enable(SerializationFeature.WRAP_ROOT_VALUE)
                // .setVisibility(jsonMapper.getVisibilityChecker().with(Visibility.NONE));
                .changeDefaultVisibility(handler -> handler
                                .withFieldVisibility(Visibility.ANY)
                        // .withGetterVisibility(Visibility.PUBLIC_ONLY)
                        // .withSetterVisibility(Visibility.PUBLIC_ONLY)
                )
                .build()
        ;

        // SimpleModule module = new SimpleModule();
        // module.registerSubtypes(getGuiStateClasses());
        // mapper.registerModule(module);
    }

    @Override
    protected GuiState load(final GuiState guiState, final InputStream inputStream) throws Exception {
        return jsonMapper.readValue(inputStream, guiState.getClass());
    }

    @Override
    protected void save(final GuiState guiState, final OutputStream outputStream) throws Exception {
        jsonMapper.writeValue(outputStream, guiState);
    }
}
