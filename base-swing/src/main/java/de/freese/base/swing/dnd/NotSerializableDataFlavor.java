package de.freese.base.swing.dnd;

import java.awt.datatransfer.DataFlavor;
import java.io.Serial;

/**
 * FlavorObject, to avoid the Cloning of Serializable-Objects.
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

    @Override
    public boolean isFlavorSerializedObjectType() {
        // Do not clone !
        return false;
    }
}
