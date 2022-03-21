// Created: 26.01.2018
package de.freese.base.mvc.guistate;

import java.nio.file.Path;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.freese.base.mvc.storage.LocalStorage;
import de.freese.base.swing.state.GUIState;

/**
 * Der {@link JsonGuiStateProvider} nutzt den {@link LocalStorage} für das Speichern im JSON-Format.
 *
 * @author Thomas Freese
 */
public class JsonGuiStateProvider extends AbstractGuiStateProvider
{
    /**
     *
     */
    private final ObjectMapper mapper;

    /**
     * Erzeugt eine neue Instanz von {@link JsonGuiStateProvider}.
     *
     * @param localStorage {@link LocalStorage}
     * @param guiStateManager {@link GuiStateManager}
     */
    public JsonGuiStateProvider(final LocalStorage localStorage, final GuiStateManager guiStateManager)
    {
        super(localStorage, guiStateManager);

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

    /**
     * @see de.freese.base.mvc.guistate.GuiStateProvider#load(java.lang.String, java.lang.Class)
     */
    @Override
    public GUIState load(final String filePrefix, final Class<GUIState> stateClazz)
    {
        String fileName = filePrefix + ".json";
        GUIState state = null;

        try
        {
            Path path = getLocalStorage().getPath(fileName);

            // state = this.mapper.reader().readValue(path.toFile());
            state = this.mapper.readValue(path.toFile(), stateClazz);

            // mapper.reader().forType(User.class)
            // .forType(new TypeReference<List<User>>() {})
        }
        catch (Exception ex)
        {
            // StringBuilder sb = new StringBuilder();
            // StackTraceLimiter.printStackTrace(ex, sb, 3);
            // getLogger().warn("Can not load GUIState for {}: ", fileName);
        }

        return state;
    }

    /**
     * @see de.freese.base.mvc.guistate.GuiStateProvider#save(java.lang.String, de.freese.base.swing.state.GUIState)
     */
    @Override
    public void save(final String filePrefix, final GUIState state)
    {
        String fileName = filePrefix + ".json";

        try
        {
            Path path = getLocalStorage().getPath(fileName);

            this.mapper.writer().writeValue(path.toFile(), state);
        }
        catch (Exception ex)
        {
            // StringBuilder sb = new StringBuilder();
            // StackTraceLimiter.printStackTrace(ex, sb, 3);
            // LOGGER.warn(sb.toString());

            getLogger().warn("Can not save GUIState for {}", fileName, ex);
        }
    }
}
