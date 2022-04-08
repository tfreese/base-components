package de.freese.base.swing.components.tree.lazy;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Basisknoten für ein TreeModel, welches die Kinderknoten erst beim Aufklappen des Parents lädt.
 *
 * @author Thomas Freese
 * @see LazyLoadingTreeController
 */
public class DefaultLazyLoadingTreeNode extends DefaultMutableTreeNode
{
    /**
     *
     */
    private static final long serialVersionUID = -8434762605446347104L;
    /**
     *
     */
    private boolean childrenLoaded = false;

    /**
     * Erstellt ein neues {@link DefaultLazyLoadingTreeNode} Object.
     *
     * @param userObject Object
     */
    public DefaultLazyLoadingTreeNode(final Object userObject)
    {
        super(userObject);
    }

    /**
     * @see DefaultMutableTreeNode#isLeaf()
     */
    @Override
    public boolean isLeaf()
    {
        if (!childrenLoaded)
        {
            // To display the Expand-Knob.
            return false;
        }

        return super.isLeaf();
    }

    /**
     * @param childrenLoaded boolean
     */
    void setChildrenLoaded(final boolean childrenLoaded)
    {
        this.childrenLoaded = childrenLoaded;
    }
}
