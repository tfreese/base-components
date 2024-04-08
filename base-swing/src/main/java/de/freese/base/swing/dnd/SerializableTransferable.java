package de.freese.base.swing.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;

/**
 * @author Thomas Freese
 */
public class SerializableTransferable implements Transferable {
    public static final DataFlavor FLAVOR = new NotSerializableDataFlavor(Serializable[].class, "Serializable");

    private final Serializable[] objects;

    public SerializableTransferable(final Serializable[] objects) {
        super();

        this.objects = objects;
    }

    @Override
    public Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (isDataFlavorSupported(flavor)) {
            return this.objects;
        }

        throw new UnsupportedFlavorException(flavor);
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{FLAVOR};
    }

    @Override
    public boolean isDataFlavorSupported(final DataFlavor flavor) {
        return flavor.getRepresentationClass().isAssignableFrom(Serializable[].class);
    }
}
