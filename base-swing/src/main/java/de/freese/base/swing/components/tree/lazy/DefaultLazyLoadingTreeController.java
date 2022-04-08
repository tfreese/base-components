package de.freese.base.swing.components.tree.lazy;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;
import java.util.function.Function;

import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Tree ExpandListener for LazyLoading of the Children for a MutableTreeNode.<br>
 *
 * @author Thomas Freese
 * @see DefaultLazyLoadingTreeNode
 */
public class DefaultLazyLoadingTreeController implements TreeWillExpandListener
{
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultLazyLoadingTreeController.class);
    /**
     *
     */
    private final Executor executor;
    /**
     *
     */
    private final Function<DefaultLazyLoadingTreeNode, List<MutableTreeNode>> loadFunction;
    /**
     * BlockingSemaphore für den SwingWorker.
     */
    private final Semaphore semaphore = new Semaphore(1, true);

    /**
     * Erstellt ein neues {@link DefaultLazyLoadingTreeController} Object.<br>
     * Das Laden der Kind-Knoten wird als {@link SwingWorker} mit der Methode {@link SwingWorker#execute()} ausgeführt.
     *
     * @param loadFunction {@link Function}
     */
    public DefaultLazyLoadingTreeController(Function<DefaultLazyLoadingTreeNode, List<MutableTreeNode>> loadFunction)
    {
        this(loadFunction, null);
    }

    /**
     * Erstellt ein neues {@link DefaultLazyLoadingTreeController} Object.<br>
     * Das Laden der Kind-Knoten wird als {@link SwingWorker} in dem übergebenen {@link Executor} durchgeführt. Ist dieser {@link Executor} null, wird
     * {@link SwingWorker#execute()} ausgeführt.
     *
     * @param loadFunction {@link Function}
     * @param executor {@link Executor}; Optional
     */
    public DefaultLazyLoadingTreeController(Function<DefaultLazyLoadingTreeNode, List<MutableTreeNode>> loadFunction, final Executor executor)
    {
        super();

        this.loadFunction = Objects.requireNonNull(loadFunction, "loadFunction required");
        this.executor = executor;
    }

    /**
     * @see TreeWillExpandListener#treeWillCollapse(TreeExpansionEvent)
     */
    @Override
    public void treeWillCollapse(final TreeExpansionEvent event) throws ExpandVetoException
    {
        // NOOP
    }

    /**
     * @see TreeWillExpandListener#treeWillExpand(TreeExpansionEvent)
     */
    @Override
    public void treeWillExpand(final TreeExpansionEvent event) throws ExpandVetoException
    {
        DefaultTreeModel treeModel = (DefaultTreeModel) ((JTree) event.getSource()).getModel();
        TreePath path = event.getPath();
        Object lastPathComponent = path.getLastPathComponent();

        if (lastPathComponent instanceof DefaultLazyLoadingTreeNode lazyNode)
        {
            loadChildren(treeModel, lazyNode);
        }
    }

    /**
     * Liefert den DummyKnoten für den Kinderzweig des Parents mit dem "Laden"-Text.
     *
     * @return {@link MutableTreeNode}
     */
    protected MutableTreeNode createLoadingNode()
    {
        return new DefaultMutableTreeNode("Loading ...", false);
    }

    /**
     * @return {@link Executor}
     */
    protected Executor getExecutor()
    {
        return this.executor;
    }

    /**
     * @param treeModel {@link DefaultTreeModel}
     * @param node {@link DefaultLazyLoadingTreeNode}
     */
    private void loadChildren(DefaultTreeModel treeModel, final DefaultLazyLoadingTreeNode node)
    {
        semaphore.acquireUninterruptibly();

        // Alle Children entfernen.
        node.removeAllChildren();

        // Loading Node setzen.
        MutableTreeNode loadingNode = createLoadingNode();
        node.add(loadingNode);
        //treeModel.insertNodeInto(loadingNode, node, 0);
        treeModel.nodeStructureChanged(node);

        SwingWorker<List<MutableTreeNode>, Void> worker = new SwingWorker<List<MutableTreeNode>, Void>()
        {
            @Override
            protected List<MutableTreeNode> doInBackground() throws Exception
            {
                return loadFunction.apply(node);
            }

            @Override
            protected void done()
            {
                loadingNode.removeFromParent();

                try
                {
                    List<MutableTreeNode> children = get();

                    for (int i = 0; i < children.size(); i++)
                    {
                        node.add(children.get(i));
                    }

                    node.setChildrenLoaded(true);
                }
                catch (Exception ex)
                {
                    LOGGER.error(null, ex);
                }
                finally
                {
                    treeModel.nodeStructureChanged(node);

                    semaphore.release();
                }
            }
        };

        if (getExecutor() == null)
        {
            worker.execute();
        }
        else
        {
            getExecutor().execute(worker);
        }
    }
}
