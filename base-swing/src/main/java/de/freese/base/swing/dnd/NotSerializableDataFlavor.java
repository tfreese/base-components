package de.freese.base.swing.dnd;

import java.awt.datatransfer.DataFlavor;

/**
 * Eigenes FlavorObject, um das Cloning von Serializable-Objekten zu verhindern.
 *
 * @author Thomas Freese
 */
public class NotSerializableDataFlavor extends DataFlavor
{
    /**
     *
     */
    private static final long serialVersionUID = 782562737576235278L;

    /**
     * Erstellt ein neues {@link NotSerializableDataFlavor} Object.
     *
     * @param representationClass {@link Class}
     * @param humanPresentableName String
     */
    public NotSerializableDataFlavor(final Class<?> representationClass, final String humanPresentableName)
    {
        super(representationClass, humanPresentableName);
    }

    /**
     * @see java.awt.datatransfer.DataFlavor#isFlavorSerializedObjectType()
     */
    @Override
    public boolean isFlavorSerializedObjectType()
    {
        // Sonst würde er beim Transfer das Object clonen !
        return false;
    }
}
