package de.freese.base.swing.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.TreePath;

import de.freese.base.utils.TableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basisklasse eines {@link TransferHandler}s für {@link Serializable}s.
 *
 * @author Thomas Freese
 */
public class SerializableTransferHandler extends TransferHandler
{
    @Serial
    private static final long serialVersionUID = -5613552763719090039L;

    private final transient Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @see javax.swing.TransferHandler#canImport(javax.swing.JComponent, java.awt.datatransfer.DataFlavor[])
     */
    @Override
    public boolean canImport(final JComponent comp, final DataFlavor[] transferFlavors)
    {
        for (DataFlavor transferFlavor : transferFlavors)
        {
            if (SerializableTransferable.FLAVOR.equals(transferFlavor))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * @see javax.swing.TransferHandler#getSourceActions(javax.swing.JComponent)
     */
    @Override
    public int getSourceActions(final JComponent c)
    {
        return COPY;
    }

    /**
     * @see javax.swing.TransferHandler#createTransferable(javax.swing.JComponent)
     */
    @Override
    protected Transferable createTransferable(final JComponent c)
    {
        List<Serializable> objects = new ArrayList<>();

        if (c instanceof JList<?> list)
        {
            List<?> selectedValues = list.getSelectedValuesList();

            for (Object value : selectedValues)
            {
                objects.add((Serializable) value);
            }
        }
        else if (c instanceof JTree tree)
        {
            TreePath[] selectedPaths = tree.getSelectionPaths();

            for (TreePath treePath : selectedPaths)
            {
                objects.add((Serializable) treePath.getLastPathComponent());
            }
        }
        else if (c instanceof JTable table)
        {
            Object[] selectedObjects = TableUtils.getSelectedObjects(table);

            for (Object object : selectedObjects)
            {
                objects.add((Serializable) object);
            }
        }

        if (objects.isEmpty())
        {
            return null;
        }

        return new SerializableTransferable(objects.toArray(new Serializable[0]));
    }

    protected Logger getLogger()
    {
        return this.logger;
    }
}
