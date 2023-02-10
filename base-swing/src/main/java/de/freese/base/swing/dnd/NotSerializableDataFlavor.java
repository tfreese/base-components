package de.freese.base.swing.dnd;

import java.awt.datatransfer.DataFlavor;
import java.io.Serial;

/**
 * Eigenes FlavorObject, um das Cloning von Serializable-Objekten zu verhindern.
 *
 * @author Thomas Freese
 */
public class NotSerializableDataFlavor extends DataFlavor {
    @Serial
    private static final long serialVersionUID = 782562737576235278L;

    public NotSerializableDataFlavor() {
        super();
    }

    public NotSerializableDataFlavor(final Class<?> representationClass, final String humanPresentableName) {
        super(representationClass, humanPresentableName);
    }

    /**
     * @see java.awt.datatransfer.DataFlavor#isFlavorSerializedObjectType()
     */
    @Override
    public boolean isFlavorSerializedObjectType() {
        // Sonst würde er beim Transfer das Object klonen !
        return false;
    }
}
