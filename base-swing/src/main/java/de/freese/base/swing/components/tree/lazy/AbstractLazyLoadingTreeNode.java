package de.freese.base.swing.components.tree.lazy;

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

/**
 * Basisknoten für ein TreeModel, welches die Kinderknoten erst beim Aufklappen des Parents lädt.
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
    protected AbstractLazyLoadingTreeNode(final Object userObject, final DefaultTreeModel model)
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
     * @param nodes {@link List}
     */
    void setChildren(final List<MutableTreeNode> nodes)
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

        for (int i = 0; i < nodes.size(); i++)
        {
            this.model.insertNodeInto(nodes.get(i), this, i);
        }
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
     * @return {@link List}
     */
    protected abstract List<MutableTreeNode> loadChilds();
}
