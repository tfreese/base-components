package de.freese.base.mvc.guistate;

import java.awt.Component;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import de.freese.base.mvc.storage.LocalStorage;
import de.freese.base.swing.state.GuiState;
import de.freese.base.swing.state.GuiStates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public abstract class AbstractGuiStateManager implements GuiStateManager
{
    private final String fileExtension;
    private final GuiStates guiStates;
    private final LocalStorage localStorage;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @param fileExtension String; Like 'xml' or 'json'.
     */
    protected AbstractGuiStateManager(final LocalStorage localStorage, final GuiStates guiStates, final String fileExtension)
    {
        super();

        this.localStorage = Objects.requireNonNull(localStorage, "localStorage required");
        this.guiStates = Objects.requireNonNull(guiStates, "guiStates required");
        this.fileExtension = Objects.requireNonNull(fileExtension, "fileExtension required");
    }

    @Override
    public void restore(final Component component, final String name)
    {
        Path relativePath = Paths.get(name + "." + this.fileExtension);

        GuiState guiState = getGuiStates().getState(component.getClass());

        if (guiState == null)
        {
            getLogger().warn("GuiState not found for: {}", component.getClass());

            return;
        }

        if (Files.notExists(getLocalStorage().getAbsolutPath(relativePath)))
        {
            return;
        }

        try (InputStream inputStream = getLocalStorage().getInputStream(relativePath))
        {
            guiState = load(guiState, inputStream);
        }
        catch (Exception ex)
        {
            getLogger().warn("Can not load GuiState for {}: ", relativePath, ex);
        }

        guiState.restore(component);
    }

    @Override
    public void store(final Component component, final String name)
    {
        Path relativePath = Paths.get(name + "." + this.fileExtension);

        GuiState guiState = getGuiStates().getState(component.getClass());

        if (guiState == null)
        {
            getLogger().warn("GuiState not found for: {}", component.getClass());

            return;
        }

        guiState.store(component);

        try (OutputStream outputStream = getLocalStorage().getOutputStream(relativePath))
        {
            save(guiState, outputStream);
        }
        catch (Exception ex)
        {
            getLogger().warn("Can not save GuiState for {}: ", relativePath, ex);
        }
    }

    protected GuiStates getGuiStates()
    {
        return this.guiStates;
    }

    protected LocalStorage getLocalStorage()
    {
        return this.localStorage;
    }

    protected Logger getLogger()
    {
        return this.logger;
    }

    protected abstract GuiState load(GuiState guiState, InputStream inputStream) throws Exception;

    protected abstract void save(GuiState guiState, OutputStream outputStream) throws Exception;
}
