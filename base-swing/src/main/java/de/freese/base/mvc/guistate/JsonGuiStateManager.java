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
public class JsonGuiStateManager extends AbstractGuiStateManager
{
    private final ObjectMapper mapper;

    public JsonGuiStateManager(final LocalStorage localStorage, final GuiStates guiStates)
    {
        super(localStorage, guiStates, "json");

        this.mapper = new ObjectMapper();

        this.mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        this.mapper.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);
        this.mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.mapper.enable(SerializationFeature.WRAP_ROOT_VALUE);

        // this.mapper.setVisibility(jsonMapper.getVisibilityChecker().with(Visibility.NONE));
        this.mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        // this.mapper.setVisibility(PropertyAccessor.SETTER, Visibility.PUBLIC_ONLY);
        // this.mapper.setVisibility(PropertyAccessor.GETTER, Visibility.PUBLIC_ONLY);

        // this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // this.mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

        // SimpleModule module = new SimpleModule();
        // module.registerSubtypes(getGuiStateClasses());
        // this.mapper.registerModule(module);
    }

    @Override
    protected GuiState load(final GuiState guiState, final InputStream inputStream) throws Exception
    {
        return this.mapper.readValue(inputStream, guiState.getClass());
    }

    @Override
    protected void save(final GuiState guiState, final OutputStream outputStream) throws Exception
    {
        this.mapper.writer().writeValue(outputStream, guiState);
    }
}
