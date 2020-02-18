package de.freese.base.mvc.context.guistate;

import java.nio.file.Path;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import de.freese.base.mvc.context.storage.LocalStorage;
import de.freese.base.swing.state.GUIState;

/**
 * Der {@link XMLGuiStateProvider} nutzt den {@link LocalStorage} fuer das Speichern im XML-Format.
 *
 * @author Thomas Freese
 */
public final class XMLGuiStateProvider extends AbstractGuiStateProvider
{
    /**
     *
     */
    private final Marshaller marshaller;

    /**
     *
     */
    private final Unmarshaller unMarshaller;

    /**
     * Erstellt ein neues {@link XMLGuiStateProvider} Object.
     *
     * @param localStorage {@link LocalStorage}
     * @param guiStateManager {@link GuiStateManager}
     */
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
     * @see de.freese.base.mvc.context.guistate.GuiStateProvider#load(java.lang.String, java.lang.Class)
     */
    @Override
    public GUIState load(final String filePrefix, final Class<GUIState> stateClazz)
    {
        String fileName = filePrefix + ".xml";
        GUIState state = null;

        try
        {
            Path path = getLocalStorage().getPath(fileName);

            state = (GUIState) this.unMarshaller.unmarshal(path.toFile());
        }
        catch (Exception ex)
        {
            // StringBuilder sb = new StringBuilder();
            // StackTraceLimiter.printStackTrace(ex, sb, 3);
            getLogger().warn("Can not load GUIState for {}: ", fileName, ex);
        }

        return state;
    }

    /**
     * @see de.freese.base.mvc.context.guistate.GuiStateProvider#save(java.lang.String, de.freese.base.swing.state.GUIState)
     */
    @Override
    public void save(final String filePrefix, final GUIState state)
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
            // XMLStorage.saveBean(outputStream, state);
            // }
        }
        catch (Exception ex)
        {
            // StringBuilder sb = new StringBuilder();
            // StackTraceLimiter.printStackTrace(ex, sb, 3);
            // LOGGER.warn(sb.toString());

            getLogger().warn("Can not save GUIState for {}", fileName);
        }
    }
}
