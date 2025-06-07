package de.freese.base.mvc.guistate;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import de.freese.base.mvc.storage.LocalStorage;
import de.freese.base.swing.state.GuiState;
import de.freese.base.swing.state.GuiStates;

/**
 * @author Thomas Freese
 */
public final class XmlGuiStateManager extends AbstractGuiStateManager {
    private Marshaller marshaller;

    private Unmarshaller unMarshaller;

    public XmlGuiStateManager(final LocalStorage localStorage, final GuiStates guiStates) {
        super(localStorage, guiStates, "xml");

    }

    @Override
    protected GuiState load(final GuiState guiState, final InputStream inputStream) throws Exception {
        return (GuiState) getUnMarshaller().unmarshal(inputStream);
    }

    @Override
    protected void save(final GuiState guiState, final OutputStream outputStream) throws Exception {
        getMarshaller().marshal(guiState, outputStream);
    }

    private Marshaller getMarshaller() {
        if (marshaller == null) {
            initJaxB();
        }

        return marshaller;
    }

    private Unmarshaller getUnMarshaller() {
        if (unMarshaller == null) {
            initJaxB();
        }

        return unMarshaller;
    }

    private void initJaxB() {
        try {
            final JAXBContext jaxbContext = JAXBContext.newInstance(getGuiStates().getGuiStates().toArray(Class[]::new));

            marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, StandardCharsets.UTF_8.name());

            unMarshaller = jaxbContext.createUnmarshaller();
        }
        catch (RuntimeException ex) {
            throw ex;
        }
        catch (Exception ex) {
            final RuntimeException re = new RuntimeException(ex);
            re.setStackTrace(ex.getStackTrace());

            throw re;
        }
    }
}
