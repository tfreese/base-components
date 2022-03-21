package de.freese.base.swing.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;

/**
 * {@link Transferable} für {@link Serializable}s.
 *
 * @author Thomas Freese
 */
public class SerializableTransferable implements Transferable
{
    /**
     *
     */
    public static final DataFlavor FLAVOR = new NotSerializableDataFlavor(Serializable[].class, "Serializable");
    /**
     *
     */
    private final Serializable[] objects;

    /**
     * Erstellt ein neues {@link SerializableTransferable} Objekt.
     *
     * @param objects {@link Serializable}[]
     */
    public SerializableTransferable(final Serializable[] objects)
    {
        super();

        this.objects = objects;
    }

    /**
     * @see java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer.DataFlavor)
     */
    @Override
    public Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException, IOException
    {
        if (isDataFlavorSupported(flavor))
        {
            return this.objects;
        }

        throw new UnsupportedFlavorException(flavor);
    }

    /**
     * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
     */
    @Override
    public DataFlavor[] getTransferDataFlavors()
    {
        return new DataFlavor[]
                {
                        FLAVOR
                };
    }

    /**
     * @see java.awt.datatransfer.Transferable#isDataFlavorSupported(java.awt.datatransfer.DataFlavor)
     */
    @Override
    public boolean isDataFlavorSupported(final DataFlavor flavor)
    {
        return (flavor.getRepresentationClass().isAssignableFrom(Serializable[].class));
    }
}
