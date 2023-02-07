package de.freese.base.mvc.guistate;

import java.nio.file.Path;
import java.nio.file.Paths;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import de.freese.base.mvc.storage.LocalStorage;
import de.freese.base.swing.state.GuiState;
import de.freese.base.swing.state.GuiStates;

/**
 * @author Thomas Freese
 */
public final class XMLGuiStateManager extends AbstractGuiStateManager
{
    private Marshaller marshaller;

    private Unmarshaller unMarshaller;

    public XMLGuiStateManager(final LocalStorage localStorage, final GuiStates guiStates)
    {
        super(localStorage, guiStates);

    }

    @Override
    protected GuiState load(final Class<GuiState> stateClazz, final String name)
    {
        Path fileName = Paths.get(name + ".xml");
        GuiState state = null;

        try
        {
            Path path = getLocalStorage().getAbsolutPath(fileName);

            state = (GuiState) getUnMarshaller().unmarshal(path.toFile());
        }
        catch (Exception ex)
        {
            // StringBuilder sb = new StringBuilder();
            // StackTraceLimiter.printStackTrace(ex, sb, 3);
            getLogger().warn("Can not load GuiState for {}: ", fileName, ex);
        }

        return state;
    }

    @Override
    protected void save(final GuiState state, final String name)
    {
        Path fileName = Paths.get(name + ".xml");

        try
        {
            Path path = getLocalStorage().getAbsolutPath(fileName);

            // @XmlRootElement(name = "FrameGuiState")
            // @XmlAccessorType(XmlAccessType.FIELD)
            getMarshaller().marshal(state, path.toFile());

            // Ohne @XMLRootElement in der Bean muss dieses manuell erzeugt werden.
            // QName qName = new QName(state.getClass().getName(), state.getClass().getSimpleName());
            // JAXBElement root = new JAXBElement(qName, state.getClass(), state);
            // JAXBElement root = new JAXBElement(new QName(state.getClass().getName()), state.getClass(), state);
            //
            // getMarshaller().marshal(root, path.toFile());

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

    private Marshaller getMarshaller()
    {
        if (marshaller == null)
        {
            initJaxB();
        }

        return marshaller;
    }

    private Unmarshaller getUnMarshaller()
    {
        if (unMarshaller == null)
        {
            initJaxB();
        }

        return unMarshaller;
    }

    private void initJaxB()
    {
        try
        {
            JAXBContext jaxbContext = JAXBContext.newInstance(getGuiStates().getGuiStates());

            this.marshaller = jaxbContext.createMarshaller();
            this.marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            this.unMarshaller = jaxbContext.createUnmarshaller();
        }
        catch (RuntimeException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            RuntimeException re = new RuntimeException(ex);
            re.setStackTrace(ex.getStackTrace());

            throw re;
        }
    }
}
