package de.freese.base.swing.components.tree.lazy;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

/**
 * Basisknoten fuer ein TreeModel, welches die Kinderknoten erst beim aufklappen des Parents laedt.
 *
 * @author Thomas Freese
 * @see LazyLoadingTreeController
 */
public abstract class AbstractLazyLoadingTreeNode extends DefaultMutableTreeNode
{
    /**
     *
     */
    private static final long serialVersionUID = -8434762605446347104L;

    /**
     *
     */
    private final DefaultTreeModel model;

    /**
     * Erstellt ein neues {@link AbstractLazyLoadingTreeNode} Object.
     *
     * @param userObject Object
     * @param model {@link DefaultTreeModel}
     */
    public AbstractLazyLoadingTreeNode(final Object userObject, final DefaultTreeModel model)
    {
        super(userObject);

        this.model = model;
        super.setAllowsChildren(true);
    }

    /**
     * @see javax.swing.tree.DefaultMutableTreeNode#isLeaf()
     */
    @Override
    public boolean isLeaf()
    {
        return !getAllowsChildren();
    }

    /**
     * @return {@link DefaultTreeModel}
     */
    protected DefaultTreeModel getModel()
    {
        return this.model;
    }

    /**
     * Laden der Kinder dieses Knotens.
     *
     * @return {@link MutableTreeNode}[]
     */
    protected abstract MutableTreeNode[] loadChilds();

    /**
     * Liefert true, wenn dieser Knoten seine Kinder bereits geladen hat.
     *
     * @return boolean
     */
    boolean areChildrenLoaded()
    {
        return (getChildCount() > 0) && getAllowsChildren();
    }

    /**
     * Setzt die geladenen Kinder in das {@link DefaultTreeModel}.
     *
     * @param nodes {@link MutableTreeNode}[]
     */
    void setChildren(final MutableTreeNode... nodes)
    {
        int childCount = getChildCount();

        if (childCount > 0)
        {
            // Loading Node entfernen
            for (int i = 0; i < childCount; i++)
            {
                this.model.removeNodeFromParent((MutableTreeNode) getChildAt(0));
            }
        }

        if (nodes == null)
        {
            return;
        }

        for (int i = 0; i < nodes.length; i++)
        {
            this.model.insertNodeInto(nodes[i], this, i);
        }
    }
}
