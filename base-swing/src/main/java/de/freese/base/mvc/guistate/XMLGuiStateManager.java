package de.freese.base.mvc.guistate;

import java.io.InputStream;
import java.io.OutputStream;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import de.freese.base.mvc.storage.LocalStorage;
import de.freese.base.swing.state.GuiState;
import de.freese.base.swing.state.GuiStates;

/**
 * @author Thomas Freese
 */
public final class XMLGuiStateManager extends AbstractGuiStateManager {
    private Marshaller marshaller;

    private Unmarshaller unMarshaller;

    public XMLGuiStateManager(final LocalStorage localStorage, final GuiStates guiStates) {
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
            JAXBContext jaxbContext = JAXBContext.newInstance(getGuiStates().getGuiStates().toArray(Class[]::new));

            this.marshaller = jaxbContext.createMarshaller();
            this.marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            this.unMarshaller = jaxbContext.createUnmarshaller();
        }
        catch (RuntimeException ex) {
            throw ex;
        }
        catch (Exception ex) {
            RuntimeException re = new RuntimeException(ex);
            re.setStackTrace(ex.getStackTrace());

            throw re;
        }
    }
}
