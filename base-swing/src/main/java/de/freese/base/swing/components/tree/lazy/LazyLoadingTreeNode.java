package de.freese.base.swing.components.tree.lazy;

import java.io.Serial;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * TreeNode for LazyLoading of the Child-Nodes.
 *
 * @author Thomas Freese
 */
public class LazyLoadingTreeNode extends DefaultMutableTreeNode {
    @Serial
    private static final long serialVersionUID = -8434762605446347104L;

    private boolean childrenLoaded;

    public LazyLoadingTreeNode(final Object userObject) {
        super(userObject);
    }

    @Override
    public boolean isLeaf() {
        if (!childrenLoaded) {
            // To display the Expand-Knob.
            return false;
        }

        return super.isLeaf();
    }

    void setChildrenLoaded(final boolean childrenLoaded) {
        this.childrenLoaded = childrenLoaded;
    }
}
