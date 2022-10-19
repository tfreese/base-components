package de.freese.base.mvc.guistate;

import java.nio.file.Path;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import de.freese.base.mvc.storage.LocalStorage;
import de.freese.base.swing.state.GuiState;

/**
 * Der {@link XMLGuiStateProvider} nutzt den {@link LocalStorage} für das Speichern im XML-Format.
 *
 * @author Thomas Freese
 */
public final class XMLGuiStateProvider extends AbstractGuiStateProvider
{
    private final Marshaller marshaller;

    private final Unmarshaller unMarshaller;

    public XMLGuiStateProvider(final LocalStorage localStorage, final GuiStateManager guiStateManager)
    {
        super(localStorage, guiStateManager);

        try
        {
            JAXBContext jaxbContext = JAXBContext.newInstance(getGuiStateClasses());

            this.marshaller = jaxbContext.createMarshaller();
            this.marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            this.unMarshaller = jaxbContext.createUnmarshaller();
        }
        catch (Exception ex)
        {
            RuntimeException re = new RuntimeException(ex);
            re.setStackTrace(ex.getStackTrace());

            throw re;
        }
    }

    /**
     * @see de.freese.base.mvc.guistate.GuiStateProvider#load(java.lang.String, java.lang.Class)
     */
    @Override
    public GuiState load(final String filePrefix, final Class<GuiState> stateClazz)
    {
        String fileName = filePrefix + ".xml";
        GuiState state = null;

        try
        {
            Path path = getLocalStorage().getPath(fileName);

            state = (GuiState) this.unMarshaller.unmarshal(path.toFile());
        }
        catch (Exception ex)
        {
            // StringBuilder sb = new StringBuilder();
            // StackTraceLimiter.printStackTrace(ex, sb, 3);
            getLogger().warn("Can not load GuiState for {}: ", fileName, ex);
        }

        return state;
    }

    /**
     * @see de.freese.base.mvc.guistate.GuiStateProvider#save(java.lang.String, GuiState)
     */
    @Override
    public void save(final String filePrefix, final GuiState state)
    {
        String fileName = filePrefix + ".xml";

        try
        {
            Path path = getLocalStorage().getPath(fileName);

            // @XmlRootElement(name = "FrameGuiState")
            // @XmlAccessorType(XmlAccessType.FIELD)
            this.marshaller.marshal(state, path.toFile());

            // Ohne @XMLRootElement in der Bean muss dieses manuell erzeugt werden.
            // QName qName = new QName(state.getClass().getName(), state.getClass().getSimpleName());
            // JAXBElement root = new JAXBElement(qName, state.getClass(), state);
            // JAXBElement root = new JAXBElement(new QName(state.getClass().getName()), state.getClass(), state);
            //
            // this.marshaller.marshal(root, path.toFile());

            // try (OutputStream outputStream = Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE))
            // {
            // XmlStorage.saveBean(outputStream, state);
            // }
        }
        catch (Exception ex)
        {
            // StringBuilder sb = new StringBuilder();
            // StackTraceLimiter.printStackTrace(ex, sb, 3);
            // LOGGER.warn(sb.toString());

            getLogger().warn("Can not save GuiState for {}", fileName);
        }
    }
}
